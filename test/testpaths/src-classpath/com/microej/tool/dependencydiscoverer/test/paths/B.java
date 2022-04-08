/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
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
