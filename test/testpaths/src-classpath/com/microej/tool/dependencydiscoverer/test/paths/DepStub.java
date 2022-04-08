/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
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
