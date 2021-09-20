/*
 * Java
 *
 * Copyright 2013-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
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
