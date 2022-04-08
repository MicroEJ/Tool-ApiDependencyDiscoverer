/*
 * Java
 *
 * Copyright 2014-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.test.paths;

import java.util.List;


public class DepInvokeInterface {

	public void foo(List list) {
		list.add(null);
	}
}
