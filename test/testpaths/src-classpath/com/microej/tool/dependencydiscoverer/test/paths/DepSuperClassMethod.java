/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.test.paths;

/**
 * fooC is declared in C
 */
public class DepSuperClassMethod {

	public void foo(){
		new B().fooC();
	}
	
}