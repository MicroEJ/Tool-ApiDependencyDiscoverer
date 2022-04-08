/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.test.paths;

import java.util.Vector;

/**
 * Dependency to Class / Vector types
 */
public class DepDotClass {

	public void foo() {
		// prevents JDT unused local removal
		bar(Vector.class);
	}

	private void bar(Class<Vector> class1) {
	}
}
