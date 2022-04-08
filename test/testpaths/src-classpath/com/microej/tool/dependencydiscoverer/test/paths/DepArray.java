/*
 * Java
 *
 * Copyright 2015-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.test.paths;

public class DepArray {

	public void foo(){
		A[][] a = new A[10][];
		Class c = A[][].class;
		bar(a, c);
	}

	public void bar(A[][] a, Class c) {
		// empty method to keep unused variables in the compiled class file
	}
}
