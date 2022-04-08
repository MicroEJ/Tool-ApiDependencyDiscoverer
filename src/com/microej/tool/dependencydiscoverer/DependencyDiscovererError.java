/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer;

import com.microej.tool.dependencydiscoverer.error.Error;

/**
 * <p>
 * Contains multiple methods to initialize {@link Error} fields . To access
 * these methods you must create a {@code DependencyDiscovererError} object as
 * they are not static.
 * <p>
 * Also contains a {@code isWarning} field and overrides of methods
 * {@link Error#isFatal()} and {@link Error#isWarning()} to use its value.
 *
 */
public class DependencyDiscovererError extends Error {

	private static final long serialVersionUID = 455269644565964629L;
	private boolean isWarning;

	@Override
	public String[] getMessages() {
		return ErrorMessages.MESSAGES;
	}

	/**
	 * Create a {@link DependencyDiscovererError} with kind set to
	 * {@link ErrorMessageConstants#PATH_DOES_NOT_EXIST}.
	 *
	 * @param path to the file causing the error.
	 * @return the created error.
	 */
	public DependencyDiscovererError pathDoesNotExist(String path){
		parts = new char[][] { path.toCharArray() };
		kind = ErrorMessageConstants.PATH_DOES_NOT_EXIST;
		return this;
	}

	/**
	 * Create a {@link DependencyDiscovererError} with kind set to
	 * {@link ErrorMessageConstants#INVALID_CLASSPATH}.
	 *
	 * @param path to the file causing the error.
	 * @return the created error.
	 */
	public DependencyDiscovererError invalidClasspath(String path) {
		parts = new char[][] { path.toCharArray() };
		kind = ErrorMessageConstants.INVALID_CLASSPATH;
		return this;
	}

	@Override
	public boolean isFatal() {
		return !isWarning;
	}

	@Override
	public boolean isWarning() {
		return isWarning;
	}

	/**
	 * Create a {@link DependencyDiscovererError} with kind set to
	 * {@link ErrorMessageConstants#MISSING_CLASSPATH}.
	 *
	 * @return the created error.
	 */
	public DependencyDiscovererError missingClasspath() {
		kind = ErrorMessageConstants.MISSING_CLASSPATH;
		return this;
	}

	/**
	 * Create a {@link DependencyDiscovererError} with kind set to
	 * {@link ErrorMessageConstants#MISSING_ENTRYPOINT}.
	 *
	 * @return the created error.
	 */
	public DependencyDiscovererError missingEntryPoint() {
		kind = ErrorMessageConstants.MISSING_ENTRYPOINT;
		return this;
	}

	/**
	 * Create a {@link DependencyDiscovererError} with kind set to
	 * {@link ErrorMessageConstants#UNEXPECTED_IO_ERROR}.
	 *
	 * @param message containing IO error details
	 * @return the created error.
	 */
	public DependencyDiscovererError unexpectedIOError(String message) {
		kind = ErrorMessageConstants.UNEXPECTED_IO_ERROR;
		parts = new char[][] { message.toCharArray() };
		return this;
	}

	/**
	 * Create a {@link DependencyDiscovererError} with kind set to
	 * {@link ErrorMessageConstants#NO_MATCHING_ENTRYPOINT}.
	 *
	 * @param patterns  the list of class matching patterns
	 * @param classpath the list of classpath
	 *
	 * @return the created error.
	 */
	public DependencyDiscovererError noMatchingEntryPoints(String patterns, String classpath) {
		kind = ErrorMessageConstants.NO_MATCHING_ENTRYPOINT;
		parts = new char[][] { patterns.toCharArray(), classpath.toCharArray() };
		return this;
	}


	@Override
	public String outputName() {
		return "DEPENDENCY DISCOVERER "+super.outputName();
	}

	/**
	 *
	 * @param isWarning
	 * @return the modified object
	 */
	public DependencyDiscovererError setIsWarning(boolean isWarning) {
		this.isWarning = isWarning;
		return this;
	}

	/**
	 * This interface describes error messages codes
	 */
	@SuppressWarnings("javadoc") // no more required error code documentation
	public static interface ErrorMessageConstants {
		public static final int MAX_MESSAGES = 50;
		public static final int PATH_DOES_NOT_EXIST = 1;
		public static final int MISSING_CLASSPATH = 2;
		public static final int MISSING_ENTRYPOINT = 3;
		public static final int UNEXPECTED_IO_ERROR = 4;
		public static final int NO_MATCHING_ENTRYPOINT = 5;
		public static final int INVALID_CLASSPATH = 6;
	}

	static class ErrorMessages implements ErrorMessageConstants {

		static final String[] MESSAGES;
		static {
			MESSAGES = new String[MAX_MESSAGES];
			MESSAGES[PATH_DOES_NOT_EXIST] = "Path \\0 does not exist";
			MESSAGES[MISSING_CLASSPATH] = "Missing classpath option";
			MESSAGES[MISSING_ENTRYPOINT] = "Missing en entryPoint option";
			MESSAGES[UNEXPECTED_IO_ERROR] = "Unexpected IO error: \\0";
			MESSAGES[NO_MATCHING_ENTRYPOINT] = "No class matching '\\0' in classpath '\\1'";
			MESSAGES[INVALID_CLASSPATH] = "Path \\0 is not a valid classpath";
		}

		private ErrorMessages() {
		}

	}
}