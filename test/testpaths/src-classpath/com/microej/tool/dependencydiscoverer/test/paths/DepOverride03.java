/*
 * Java
 *
 * Copyright 2015-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.test.paths;

/**
 * Indirect dependency from a polymorphic call to a
 * private class method.
 */
public class DepOverride03 {

	public void foo(){
		new XDepPrivateClass().foo();
	}
}

abstract class XDepPrivateClassBase {

	abstract public void bar();
	
	public void foo() {
		bar();
	}
}

class XDepPrivateClass extends XDepPrivateClassBase {
	
	@Override
	public void bar() {
		new B().foo();
	}
}
