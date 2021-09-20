/*
 * Java
 *
 * Copyright 2013-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.test.paths;

import java.io.PrintStream;

/**
 * Dependency to Class / Vector types
 */
public class DepField1 {

	public void foo(){
		PrintStream p = System.out;
		B.zork();
		bar(p);
	}

	public void bar(PrintStream p) {
		// empty method to keep unused variable in the compiled class file
	}
}
