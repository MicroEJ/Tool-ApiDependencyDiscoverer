/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
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
