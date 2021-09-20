/*
 * Java
 *
 * Copyright 2020-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.test.paths;

/**
 * Test native listing
 */
public class TestNativeListing {

	public void foo() {
		callANativeDirectly();
		new TestNativeListing2();
	}

	public native void nativeEntryPoint();

	private native void callANativeDirectly();

}

class TestNativeListing2 {

	public TestNativeListing2() {
		callANativeIndirectly();
	}

	private native void callANativeIndirectly();
}
