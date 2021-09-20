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
