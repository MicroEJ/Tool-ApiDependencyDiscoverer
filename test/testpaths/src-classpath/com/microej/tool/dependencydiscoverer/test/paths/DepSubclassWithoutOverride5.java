/*
 * Java
 *
 * Copyright 2014-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.test.paths;

import com.microej.tool.dependencydiscoverer.test.paths.DepSubclassWithoutOverride5SubFoo;

//same as DepSubclassWithoutOverride2 but with super class in "against classpath"
public class DepSubclassWithoutOverride5 extends DepSubclassWithoutOverride5SubFoo {

	public void zork() {
		bar();
	}

}