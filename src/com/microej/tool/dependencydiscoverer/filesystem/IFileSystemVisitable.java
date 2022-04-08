/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.filesystem;

import java.io.IOException;

/**
 * <p>
 * Describes a file system structure element which can be visited by a {@link IFileSystemVisitor}.
 * </p>
 */
public interface IFileSystemVisitable {

	/**
	 * <p>
	 * Entry point to visit.
	 *
	 * @param visitor
	 */
	public void visitUsing(IFileSystemVisitor visitor);

	/**
	 * @return element name, may change depending on the implementation
	 */
	public String getName();

	/**
	 * Delete element.
	 *
	 * @return {@code true} for success {@code false} else.
	 */
	public boolean delete();

	/**
	 * Create element
	 *
	 * @return {@code true} for success {@code false} else.
	 * @throws IOException
	 */
	public boolean create() throws IOException;

}
