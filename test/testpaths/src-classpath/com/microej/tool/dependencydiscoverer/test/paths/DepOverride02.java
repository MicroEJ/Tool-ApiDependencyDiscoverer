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
public class DepOverride02 {

	public void foo(){
		new XDepPrivateClass().foo();
	}
}

abstract class XDepPrivateClassBase02 {

	public void bar() {
		new B().foo();
	}
	
	abstract public void foo();

}

class XDepPrivateClass02 extends XDepPrivateClassBase02 {
	
	@Override
	public void foo() {
		bar();
	}
}
