/*
 * Java
 *
 * Copyright 2014-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.test.paths;


public class DepSubclassWithoutOverride3 extends DepSubclassWithoutOverride3SubFoo {

	public void zork() {
		bar();
	}

}