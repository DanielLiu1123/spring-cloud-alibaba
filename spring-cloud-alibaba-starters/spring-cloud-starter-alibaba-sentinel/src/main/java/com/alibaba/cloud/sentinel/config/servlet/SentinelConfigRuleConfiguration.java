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

import java.util.List;

import com.alibaba.cloud.sentinel.SentinelProperties;
import com.alibaba.cloud.sentinel.config.ConfigurationService;
import com.alibaba.cloud.sentinel.config.servlet.predicate.HeaderRoutePredicateFactory;
import com.alibaba.cloud.sentinel.config.servlet.predicate.PathRoutePredicateFactory;
import com.alibaba.cloud.sentinel.config.servlet.predicate.QueryRoutePredicateFactory;
import com.alibaba.cloud.sentinel.config.servlet.predicate.RoutePredicateFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

/**
 * @author <a href="mailto:freemanliu.me@gmail.com">Freeman</a>
 * @since 2021.0.1.1
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = SERVLET)
public class SentinelConfigRuleConfiguration {

	@Bean
	public WebMvcConfigurer sentinelConfigWebMvcConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				registry.addInterceptor(new SentinelConfigHandlerInterceptor());
			}
		};
	}

	@Bean
	public ConfigurationService sentinelConfigurationService(BeanFactory beanFactory,
			ObjectProvider<ConversionService> conversionService,
			ObjectProvider<Validator> validator) {
		return new ConfigurationService(beanFactory, conversionService, validator);
	}

	@Bean
	public SentinelConfigListener sentinelConfigListener(
			ConfigurationService configurationService, SentinelProperties properties,
			List<RoutePredicateFactory<?>> predicates) {
		return new SentinelConfigListener(configurationService, properties, predicates);
	}

	@Configuration(proxyBeanMethods = false)
	static class PredicateFactoriesConfiguration {

		@Bean
		public HeaderRoutePredicateFactory routePredicateFactory() {
			return new HeaderRoutePredicateFactory();
		}

		@Bean
		public PathRoutePredicateFactory pathRoutePredicateFactory() {
			return new PathRoutePredicateFactory();
		}

		@Bean
		public QueryRoutePredicateFactory queryRoutePredicateFactory() {
			return new QueryRoutePredicateFactory();
		}
	}

}
