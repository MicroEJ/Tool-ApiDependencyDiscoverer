/*
 * Java
 *
 * Copyright 2021-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.test.paths;

/**
 * Indirect dependency from a polymorphic call to a private class method. A
 * second class contains an override of the same method but don't call it.
 */
public class DepOverride04 {

	public void foo(){
		new XDepPrivateClassBis().foo();
		new XDepPrivateClass2().triple(3);
	}
}

class XDepPrivateClassBis extends XDepPrivateClassBase {

	@Override
	public void bar() {
		new B().foo();
	}
}

class XDepPrivateClass2 extends XDepPrivateClassBase {

	public int triple(int nb) {
		return nb*3;
	}

	@Override
	public void bar() {
		B.zork();
	}
}
