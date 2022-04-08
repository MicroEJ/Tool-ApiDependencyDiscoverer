/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
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
