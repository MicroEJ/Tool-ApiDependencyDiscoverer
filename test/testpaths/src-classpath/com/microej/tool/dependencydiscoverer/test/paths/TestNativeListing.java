/*
 * Java
 *
 * Copyright 2020-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
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
