/*
 * Java
 *
 * Copyright 2011-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.filesystem;

/**
 * <p>
 * Generic File System oriented visitor.
 * </p>
 */
public interface IFileSystemVisitor {

	/**
	 * <p>
	 * Visits a directory-like structure.
	 * </p>
	 *
	 * @param directory the directory to visit
	 */
	public void visitDirectory(DirectoryVisitable directory);

	/**
	 * <p>
	 * Visits a file-like structure.
	 * </p>
	 *
	 * @param file the file to visit
	 */
	public void visitFile(FileVisitable file);

}
