/*
 * Java
 *
 * Copyright 2015-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
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
