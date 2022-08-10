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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Freeman
 * @since 2021.0.1.1
 */
public final class Copier {

	private Copier() {
		throw new AssertionError("No Copier instances for you!");
	}

	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Deep copy the given object.
	 *
	 * @param source source object
	 * @param <T> type of the source object
	 * @return copied object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copy(T source) {
		try {
			return (T) objectMapper.readValue(objectMapper.writeValueAsString(source),
					source.getClass());
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
