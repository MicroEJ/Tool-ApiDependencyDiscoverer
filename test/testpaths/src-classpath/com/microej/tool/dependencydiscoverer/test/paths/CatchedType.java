/*
 * Java
 *
 * Copyright 2017-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.test.paths;

public class CatchedType {

	public static void foo() {
		try{
			new Object();
		}
		catch(IllegalAccessError e){ // a type only used in a catch clause

		}
	}

}
