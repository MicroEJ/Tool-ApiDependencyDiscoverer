/*
 * Java
 *
 * Copyright 2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.test.paths;

/**
 * Indirect dependency from a polymorphic call to a class method with an
 * intermediary method in between.
 */
public class DepOverride05 {

	public void foo(){
		new XDepClass05().foo();
	}
}

abstract class XDepClassBase05 {

	abstract public void bar();

	public void foo() {
		foobar();
	}

	public void foobar() {
		bar();
	}

}

class XDepClass05 extends XDepClassBase05 {

	@Override
	public void bar() {
		new B().foo();
	}
}
