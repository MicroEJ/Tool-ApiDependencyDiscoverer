/*
 * Java
 *
 * Copyright 2015-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
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
