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
 * intermediary class in between.
 */
public class DepOverride06 {

	public void foo(){
		new XDepClassIntermediate().foobar(new XDepClass06());
	}
}

abstract class XDepClassBase06 {

	abstract public void bar();

	public void foo() {
		bar();
	}
}

class XDepClassIntermediate {
	public void foobar(XDepClass06 baz) {
		baz.foo();
	}
}


class XDepClass06 extends XDepClassBase06 {

	@Override
	public void bar() {
		new B().foo();
	}
}
