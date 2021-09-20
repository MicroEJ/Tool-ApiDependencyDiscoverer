/*
 * Java
 *
 * Copyright 2013-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.test.paths;


/**
 * Dependency to abstract method
 */
public class DepAbstract {

	public void foo(){
		AbstractA p = get();
		p.foo();
	}

	private AbstractA get() {
		return null;
	}
}
