/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.filesystem;

import java.util.Iterator;

/**
 * <p>
 * Iterate over a File System like structure.
 * </p>
 */
public interface IDirectoryIterator extends Iterator<IFileSystemVisitable> {

	/**
	 * {@inheritDoc}<br>
	 * The returned element is a File System-like structure element
	 */
	@Override
	public IFileSystemVisitable next();

}
