/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.filesystem;

/**
 * Used to recursively search files.
 *
 * @see com.microej.tool.dependencydiscoverer.classfinder.JavaClassFinder
 */
public abstract class RecursiveFileSystemVisitor implements IFileSystemVisitor {

	/**
	 * <p>
	 * Using a {@link IDirectoryIterator}, visits all children of the directory.
	 * </p>
	 */
	@Override
	public void visitDirectory(DirectoryVisitable directory) {
		IDirectoryIterator iterator = directory.iterator();
		while (iterator.hasNext()) {
			iterator.next().visitUsing(this);
		}
	}

}
