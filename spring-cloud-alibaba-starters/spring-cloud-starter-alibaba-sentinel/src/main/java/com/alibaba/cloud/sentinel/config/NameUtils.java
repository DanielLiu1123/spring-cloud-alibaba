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

package com.alibaba.cloud.sentinel.config;

import com.alibaba.cloud.sentinel.config.servlet.predicate.RoutePredicateFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copy from Spring Cloud Gateway.
 *
 * @author Spencer Gibb
 * @author <a href="mailto:freemanliu.me@gmail.com">Freeman</a>
 */
public final class NameUtils {

	private NameUtils() {
		throw new AssertionError("Must not instantiate utility class.");
	}

	/**
	 * Generated name prefix.
	 */
	public static final String GENERATED_NAME_PREFIX = "__GenerateKey__";

	private static final Pattern NAME_PATTERN = Pattern.compile("([A-Z][a-z\\d]+)");

	public static String generateName(int i) {
		return GENERATED_NAME_PREFIX + i;
	}

	public static String normalizeRoutePredicateName(
			Class<? extends RoutePredicateFactory> clazz) {
		return removeGarbage(clazz.getSimpleName()
				.replace(RoutePredicateFactory.class.getSimpleName(), ""));
	}

	public static String normalizeRoutePredicateNameAsProperty(
			Class<? extends RoutePredicateFactory> clazz) {
		return normalizeToCanonicalPropertyFormat(normalizeRoutePredicateName(clazz));
	}

	public static String normalizeToCanonicalPropertyFormat(String name) {
		Matcher matcher = NAME_PATTERN.matcher(name);
		StringBuffer stringBuffer = new StringBuffer();
		while (matcher.find()) {
			if (stringBuffer.length() != 0) {
				matcher.appendReplacement(stringBuffer, "-" + matcher.group(1));
			}
			else {
				matcher.appendReplacement(stringBuffer, matcher.group(1));
			}
		}
		return stringBuffer.toString().toLowerCase();
	}

	private static String removeGarbage(String s) {
		int garbageIdx = s.indexOf("$Mockito");
		if (garbageIdx > 0) {
			return s.substring(0, garbageIdx);
		}

		return s;
	}

}
