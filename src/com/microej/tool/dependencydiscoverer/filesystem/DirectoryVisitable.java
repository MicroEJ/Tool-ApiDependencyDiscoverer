/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
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
