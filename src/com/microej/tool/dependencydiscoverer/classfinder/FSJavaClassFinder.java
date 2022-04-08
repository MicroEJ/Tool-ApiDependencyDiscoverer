/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.classfinder;

import java.io.File;
import java.util.HashMap;

import com.microej.tool.dependencydiscoverer.filesystem.fs.FSDir;

/**
 * <p>
 * Concrete Java File System ({@link File}) structure visit entry point.
 * </p>
 */
public class FSJavaClassFinder extends JavaClassFinder {

	/**
	 * Cache Directory path to corresponding FSDir instance.
	 */
	private static final HashMap<String, FSDir> directoriesCache = new HashMap<>();

	/**
	 * @param path directory containing Java element (package or class). Must be an
	 *             existing directory
	 * @see File#isDirectory()
	 */
	@Override
	protected void visit(File path) {
		FSDir directory = directoriesCache.get(path.getPath());
		if (directory == null) {
			directory = new FSDir(path);
			directoriesCache.put(path.getPath(), directory);
		}
		directory.visitUsing(this);
	}
}
