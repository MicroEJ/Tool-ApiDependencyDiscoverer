/*
 * Java
 *
 * Copyright 2013-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.test.paths;

import java.util.Vector;

/**
 * Dependency to Class / Vector types (static)
 */
public class DepDotClass2 {

	public static Class RESULT;

	public void foo(){
		RESULT = Vector.class;
	}
}
