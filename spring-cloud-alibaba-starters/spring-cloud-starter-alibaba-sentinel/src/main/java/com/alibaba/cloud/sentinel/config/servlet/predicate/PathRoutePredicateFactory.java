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
import java.util.List;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.cloud.sentinel.config.ShortcutConfigurable;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import static org.springframework.http.server.PathContainer.parsePath;

/**
 * @author <a href="mailto:freemanliu.me@gmail.com">Freeman</a>
 * @since 2021.0.1.1
 */
public class PathRoutePredicateFactory
		extends AbstractRoutePredicateFactory<PathRoutePredicateFactory.Config> {

	private static final String PATTERNS = "patterns";
	private static final String MATCH_TRAILING_SLASH = "matchTrailingSlash";

	private final PathPatternParser pathPatternParser = new PathPatternParser();

	public PathRoutePredicateFactory() {
		super(Config.class);
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList(PATTERNS, MATCH_TRAILING_SLASH);
	}

	@Override
	public ShortcutConfigurable.ShortcutType shortcutType() {
		return ShortcutConfigurable.ShortcutType.GATHER_LIST_TAIL_FLAG;
	}

	@Override
	public Predicate<HttpServletRequest> apply(Config config) {
		final List<PathPattern> pathPatterns = new ArrayList<>();
		synchronized (this.pathPatternParser) {
			pathPatternParser
					.setMatchOptionalTrailingSeparator(config.isMatchTrailingSlash());
			config.getPatterns().forEach(pattern -> {
				PathPattern pathPattern = this.pathPatternParser.parse(pattern);
				pathPatterns.add(pathPattern);
			});
		}
		return exchange -> {
			PathContainer path = parsePath(exchange.getServletPath());
			for (PathPattern pathPattern : pathPatterns) {
				if (pathPattern.matches(path)) {
					return true;
				}
			}
			return false;
		};
	}

	public static class Config {

		private List<String> patterns = new ArrayList<>();

		private boolean matchTrailingSlash = true;

		public List<String> getPatterns() {
			return patterns;
		}

		public Config setPatterns(List<String> patterns) {
			this.patterns = patterns;
			return this;
		}

		public boolean isMatchTrailingSlash() {
			return matchTrailingSlash;
		}

		public Config setMatchTrailingSlash(boolean matchTrailingSlash) {
			this.matchTrailingSlash = matchTrailingSlash;
			return this;
		}

	}

}
