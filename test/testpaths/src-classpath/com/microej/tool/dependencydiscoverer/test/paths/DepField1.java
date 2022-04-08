/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
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
