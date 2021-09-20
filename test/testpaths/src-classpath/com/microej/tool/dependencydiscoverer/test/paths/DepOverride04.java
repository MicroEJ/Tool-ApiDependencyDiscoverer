/*
 * Java
 *
 * Copyright 2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
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
