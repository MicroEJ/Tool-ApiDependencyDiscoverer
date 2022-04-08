/*
 * Java
 *
 * Copyright 2021-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
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
