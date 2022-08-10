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

package com.alibaba.cloud.sentinel.config.servlet.predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;

import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * @author <a href="mailto:freemanliu.me@gmail.com">Freeman</a>
 * @since 2021.0.1.1
 */
public class HeaderRoutePredicateFactory
		extends AbstractRoutePredicateFactory<HeaderRoutePredicateFactory.Config> {

	private static final String HEADER = "header";
	private static final String REGEXP = "regexp";

	public HeaderRoutePredicateFactory() {
		super(Config.class);
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList(HEADER, REGEXP);
	}

	@Override
	public Predicate<HttpServletRequest> apply(Config config) {
		return request -> {
			List<String> values = new ArrayList<>();
			Enumeration<String> headers = request.getHeaderNames();
			while (headers.hasMoreElements()) {
				String header = headers.nextElement();
				if (config.header.equalsIgnoreCase(header)) {
					Collections.addAll(values, request.getHeader(header).split(","));
				}
			}
			if (values.isEmpty()) {
				return false;
			}
			// values is now guaranteed to not be empty
			if (StringUtils.hasText(config.regexp)) {
				// check if a header value matches
				for (String value : values) {
					if (value.matches(config.regexp)) {
						return true;
					}
				}
				return false;
			}

			// there is a value and since regexp is empty, we only check existence.
			return true;
		};
	}

	@Validated
	public static class Config {

		@NotEmpty
		private String header;

		private String regexp;

		public String getHeader() {
			return header;
		}

		public Config setHeader(String header) {
			this.header = header;
			return this;
		}

		public String getRegexp() {
			return regexp;
		}

		public Config setRegexp(String regexp) {
			this.regexp = regexp;
			return this;
		}

	}

}
