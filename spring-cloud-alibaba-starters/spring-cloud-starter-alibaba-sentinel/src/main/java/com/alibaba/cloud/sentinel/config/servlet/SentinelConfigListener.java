/*
 * Copyright 2013-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.sentinel.config.servlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.cloud.sentinel.SentinelProperties;
import com.alibaba.cloud.sentinel.config.ConfigurationService;
import com.alibaba.cloud.sentinel.config.Copier;
import com.alibaba.cloud.sentinel.config.servlet.predicate.RoutePredicateFactory;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author <a href="mailto:freemanliu.me@gmail.com">Freeman</a>
 * @since 2021.0.1.1
 */
public class SentinelConfigListener implements
		ApplicationListener<RefreshScopeRefreshedEvent>, SmartInitializingSingleton {
	private static final Logger log = LoggerFactory
			.getLogger(SentinelConfigListener.class);

	private static final String FLOW_CONTROL_ID_PREFIX = "#GenerateFlowControlRule-";
	private static final AtomicInteger flowControlCounter = new AtomicInteger();

	private SentinelProperties propertiesBackup;

	private final SentinelProperties properties;
	private final Map<String, RoutePredicateFactory<?>> predicates = new LinkedHashMap<>();
	private final ConfigurationService configurationService;

	public SentinelConfigListener(ConfigurationService configurationService,
			SentinelProperties properties, List<RoutePredicateFactory<?>> predicates) {
		this.properties = properties;
		this.configurationService = configurationService;
		initFactories(predicates);
	}

	private void initFactories(List<RoutePredicateFactory<?>> predicates) {
		predicates.forEach(factory -> {
			String key = factory.name();
			if (this.predicates.containsKey(key)) {
				log.warn("A RoutePredicateFactory named " + key
						+ " already exists, class: " + this.predicates.get(key)
						+ ". It will be overwritten.");
			}
			this.predicates.put(key, factory);
		});
		if (log.isInfoEnabled()) {
			log.info("[Sentinel Starter] Loaded RoutePredicateFactory "
					+ this.predicates.keySet());
		}
	}

	@Override
	public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
		if (!Objects.equals(properties.getFlowControl(),
				propertiesBackup.getFlowControl())) {
			clearPreviousRules();
			setup();
			log.info("[Sentinel Starter] Flow control rules updated");
		}
	}

	private void setup() {
		addConfigRules();
		updateBackup();
	}

	private void clearPreviousRules() {
		// clear routes
		RouteHolder.clear();
		// clear sentinel flow rules
		List<FlowRule> nonConfigRules = FlowRuleManager.getRules().stream().filter(
				resource -> !resource.getResource().startsWith(FLOW_CONTROL_ID_PREFIX))
				.collect(Collectors.toList());
		FlowRuleManager.loadRules(nonConfigRules);
	}

	@Override
	public void afterSingletonsInstantiated() {
		setup();
	}

	private void addConfigRules() {
		// convert routeDefinition to route
		List<FlowRule> all = new ArrayList<>(FlowRuleManager.getRules());
		properties.getFlowControl().forEach(routeDefinition -> {
			String id = FLOW_CONTROL_ID_PREFIX + flowControlCounter.getAndIncrement();
			Predicate<HttpServletRequest> predicate = combinePredicates(routeDefinition);
			List<FlowRule> flowRules = routeDefinition.getRules().stream().peek(flow -> {
				// do not allow to set resource, because it's hard to remove
				if (flow.getResource() != null) {
					log.warn(
							"Please don't set resource, it will be automatically generated, resource '{}' will be overwrite",
							flow.getResource());
				}
				flow.setResource(id);
			}).collect(Collectors.toList());
			// route id and sentinel resource name must be same
			RouteHolder.add(new Route(id, predicate, flowRules));
			all.addAll(flowRules);
		});
		FlowRuleManager.loadRules(Collections.unmodifiableList(all));
		log.info("[Sentinel Starter] Loaded {} flow-control rules for {} routes",
				properties.getFlowControl().stream().map(rd -> rd.getRules().size())
						.reduce(0, Integer::sum),
				properties.getFlowControl().size());
	}

	private void updateBackup() {
		propertiesBackup = Copier.copy(properties);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Predicate<HttpServletRequest> lookup(
			SentinelProperties.RouteDefinition.PredicateDefinition predicate) {
		RoutePredicateFactory factory = this.predicates.get(predicate.getName());
		if (factory == null) {
			throw new IllegalArgumentException(
					"Unable to find RoutePredicateFactory with name "
							+ predicate.getName());
		}

		Object config = this.configurationService.with(factory).name(predicate.getName())
				.properties(predicate.getArgs()).bind();

		return factory.apply(config);
	}

	private Predicate<HttpServletRequest> combinePredicates(
			SentinelProperties.RouteDefinition routeDefinition) {
		Predicate<HttpServletRequest> predicate = request -> true;
		for (SentinelProperties.RouteDefinition.PredicateDefinition andPredicate : routeDefinition
				.getPredicates()) {
			Predicate<HttpServletRequest> found = lookup(andPredicate);
			predicate = predicate.and(found);
		}
		return predicate;
	}

}
