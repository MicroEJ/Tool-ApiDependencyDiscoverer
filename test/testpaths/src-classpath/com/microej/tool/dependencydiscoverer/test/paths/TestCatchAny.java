/*
 * Java
 *
 * Copyright 2020-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.test.paths;

public class TestCatchAny {

	void foo() {
		try {

		} catch (Throwable e) {

		}
	}

	void bar() {
		synchronized (this) {
			while (true) {
				;
			}
		}
	}

}
