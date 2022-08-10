/*
 * Copyright 2013-2020 the original author or authors.
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

import java.util.List;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;

/**
 * @author <a href="mailto:freemanliu.me@gmail.com">Freeman</a>
 * @since 2021.0.1.1
 */
public class Route {

	private final String id;

	private final Predicate<HttpServletRequest> predicate;

	private final List<FlowRule> flowRules;

	public Route(String id, Predicate<HttpServletRequest> predicate,
			List<FlowRule> flowRules) {
		this.id = id;
		this.predicate = predicate;
		this.flowRules = flowRules;
	}

	public String getId() {
		return id;
	}

	public Predicate<HttpServletRequest> getPredicate() {
		return predicate;
	}

	public List<FlowRule> getFlowRules() {
		return flowRules;
	}
}
