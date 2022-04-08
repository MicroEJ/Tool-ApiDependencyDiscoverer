/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.test.paths;

/**
 * A_FIELD_NOTCONSTANT is declared in I
 */
public class DepSuperInterfaceField {

	public void foo(){
		int i = B.A_FIELD_NOTCONSTANT;
	}
	
}
