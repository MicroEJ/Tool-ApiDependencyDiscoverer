/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.test.paths;

/**
 * Indirect Dependency
 */
public class Dep3 {

	public void foo(){
		new B().bar(true, (byte)5, 'a', (short)-2, 12, 0l, 2f, 6.4, "aaa");
	}
}
