/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
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
