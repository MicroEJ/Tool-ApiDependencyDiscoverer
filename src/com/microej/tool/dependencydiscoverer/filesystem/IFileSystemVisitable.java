/*
 * Java
 *
 * Copyright 2011-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
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
