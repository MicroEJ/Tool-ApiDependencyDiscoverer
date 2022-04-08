/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
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
