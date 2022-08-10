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

package com.alibaba.cloud.sentinel.config.servlet.predicate;

import com.alibaba.cloud.sentinel.config.Configurable;
import com.alibaba.cloud.sentinel.config.NameUtils;
import com.alibaba.cloud.sentinel.config.ShortcutConfigurable;

import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;

/**
 * Copy from Spring Cloud Gateway.
 *
 * @author Spencer Gibb
 * @author <a href="mailto:freemanliu.me@gmail.com">Freeman</a>
 * @since 2021.0.1.1
 */
@FunctionalInterface
public interface RoutePredicateFactory<C> extends ShortcutConfigurable, Configurable<C> {

	default Class<C> getConfigClass() {
		throw new UnsupportedOperationException("getConfigClass() not implemented");
	}

	@Override
	default C newConfig() {
		throw new UnsupportedOperationException("newConfig() not implemented");
	}

	Predicate<HttpServletRequest> apply(C config);

	default String name() {
		return NameUtils.normalizeRoutePredicateName(getClass());
	}

}
