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

package com.alibaba.cloud.examples.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:freemanliu.me@gmail.com">Freeman</a>
 */
@RestController
public class TestController {

	@GetMapping("/a")
	public String a() {
		return "a";
	}

	@GetMapping("/b")
	public String b() {
		return "b";
	}

	@GetMapping("/c")
	public String c() {
		return "c";
	}

	@GetMapping("/d")
	public String d() {
		return "d";
	}
}
