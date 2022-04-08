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
