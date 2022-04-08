/*
 * Java
 *
 * Copyright 2021-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
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
