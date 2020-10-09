/*
 * Java
 *
 * Copyright 2016-2020 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer;

/**
 * Defines the options for the Dependency Discoverer.
 */
public interface DependencyDiscovererOptions {

	/**
	 * MicroEJ repository version to download.
	 */
	String MICROEJ_VERSION = "5_0";

	/**
	 * Whether the dependency discoverer tries to download the latest repository or not.
	 */
	boolean OFFLINE = false;

}
