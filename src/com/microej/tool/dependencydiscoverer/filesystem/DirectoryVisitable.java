/*
 * Java
 *
 * Copyright 2011-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.filesystem;

/**
 * Abstract visitable directory, define {@link DirectoryVisitable#iterator()}
 * methods iterate through directory sub-files and sub-directories.
 */
public abstract class DirectoryVisitable implements IFileSystemVisitable {

	@Override
	public void visitUsing(IFileSystemVisitor visitor) {
		visitor.visitDirectory(this);
	}

	/**
	 * Directory content iterator to Override.
	 *
	 * @return the iterator
	 */
	public abstract IDirectoryIterator iterator();
}
