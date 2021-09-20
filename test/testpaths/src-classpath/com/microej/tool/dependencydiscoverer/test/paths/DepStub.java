/*
 * Java
 *
 * Copyright 2013-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.test.paths;

import com.microej.tool.dependencydiscoverer.test.paths.Stub;

/**
 * Dependency to a stub class put in against classpath (test that stub class is not analyzed)
 */
public class DepStub {

	public void foo(){
		Stub.foo();
	}
}
