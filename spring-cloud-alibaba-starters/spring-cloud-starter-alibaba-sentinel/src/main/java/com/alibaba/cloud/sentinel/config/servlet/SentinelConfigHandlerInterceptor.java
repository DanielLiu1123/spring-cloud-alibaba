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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author <a href="mailto:freemanliu.me@gmail.com">Freeman</a>
 * @since 2021.0.1.1
 */
public class SentinelConfigHandlerInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		Route route = getMatchedRoute(request);
		if (route == null) {
			return true;
		}

		// match route, Sentinel show off your skills!
		Entry entry = null;
		try {
			entry = SphU.entry(route.getId());
			return true;
		}
		catch (BlockException ex) {
			// TODO: customize the exception handling
			response.getWriter().write("blocked by sentinel");
			response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
			return false;
		}
		catch (Throwable bizException) {
			Tracer.trace(bizException);
			throw bizException;
		}
		finally {
			if (entry != null) {
				entry.exit();
			}
		}
	}

	private Route getMatchedRoute(HttpServletRequest request) {
		for (Route route : RouteHolder.getRoutes()) {
			if (route.getPredicate().test(request)) {
				return route;
			}
		}
		return null;
	}
}
