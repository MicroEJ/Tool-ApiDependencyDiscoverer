/*
 * Java
 *
 * Copyright 2013-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.test.paths;

import java.io.InputStream;

public class B extends C{

	public void foo(){
		A[] array = {};
		"aaa".split("a");
	}

	public void bar(boolean bool, byte b, char c, short s, int i, long l, float f,
			double d, String string) {
		"aaa".charAt(0);
	}

	public static void zork() {
		InputStream is = System.in;
		zorkbar(is);
	}

	public static void zorkbar(InputStream is) {
		// empty method to keep unused variable in the compiled class file
	}
}
