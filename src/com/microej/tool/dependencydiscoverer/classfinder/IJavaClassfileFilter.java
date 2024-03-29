/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.classfinder;

/**
 * <p>
 * Determine if a file name is acceptable as class file according to implementation criterion.
 * </p>
 */
public interface IJavaClassfileFilter {

	/**
	 * <p>
	 * Returns <code>true</code> if the given file name is accepted as a classfile, <code>false</code> otherwise.
	 * </p>
	 *
	 * @param name the canonized name of the file to check (canonized on the form a/b/C.ext)
	 * @return <code>true</code> if the given file name is accepted as a classfile, <code>false</code> otherwise.
	 */
	public boolean accept(String name);
}
