/*
 * Java
 *
 * Copyright 2016-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer;

/**
 * Defines the options for the Dependency Discoverer.
 */
public abstract class DependencyDiscovererDefaultOptions {

	/**
	 * MicroEJ repository version to download.
	 */
	static final String MICROEJ_VERSION = "5_0";

	/**
	 * Base directory to perform analysis
	 */
	public static final String PROJECT_PATH = "";

	/**
	 * Path of the directory that contains the jars to test
	 */
	public static final String CLASS_PATH = "classpath";

	/**
	 * Path of the directory that contains the libraries to exclude from
	 * dependencies.<br>
	 */
	public static final String PROVIDED_CLASSPATH = "providedClasspath";

	/**
	 * Path of the file that contains the result
	 */
	public static final String OUTPUT_RESULT_FILE = "result.txt";

	/**
	 * Format of the output
	 */
	public static final String OUTPUT_TYPE = "text";

	/**
	 * If the repositories cache is cleaned at start
	 */
	public static final boolean CLEAN_CACHE = false;

	/**
	 * Directory used to load cached files
	 */
	public static final String CACHE_DIR = "~/.microej/caches/dd";

	/**
	 * If the verbose mode is active
	 */
	public static final boolean VERBOSE = false;

	private DependencyDiscovererDefaultOptions() {
		// empty private constructor to prevent instantiations
	}

}
