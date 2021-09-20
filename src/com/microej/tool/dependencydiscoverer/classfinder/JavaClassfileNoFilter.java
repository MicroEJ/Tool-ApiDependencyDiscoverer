/*
 * Java
 *
 * Copyright 2011-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.classfinder;

/**
 * <p>
 * Accept any class file (check only of file name extension {@link #CLASS_EXT}).
 * </p>
 */
public class JavaClassfileNoFilter implements IJavaClassfileFilter {

	/**
	 * String containing the .class extension
	 */
	public static final String CLASS_EXT = ".class";

	@Override
	public boolean accept(String fileName) {
		return fileName.endsWith(CLASS_EXT);
	}

}
