/*
 * Java
 *
 * Copyright 2011-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
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
