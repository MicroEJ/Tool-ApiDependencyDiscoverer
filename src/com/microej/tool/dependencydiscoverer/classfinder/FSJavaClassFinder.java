/*
 * Java
 *
 * Copyright 2011-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.classfinder;

import java.io.File;

import com.microej.tool.dependencydiscoverer.filesystem.fs.FSDir;

/**
 * <p>
 * Concrete Java File System ({@link File}) structure visit entry point.
 * </p>
 */
public class FSJavaClassFinder extends JavaClassFinder {

	/**
	 * @param path directory containing Java element (package or class). Must be an existing directory
	 * @see File#isDirectory()
	 */
	@Override
	protected void visit(File path) {
		(new FSDir(path)).visitUsing(this);
	}
}
