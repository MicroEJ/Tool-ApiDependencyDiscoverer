/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.analysis;

/**
 * <p>
 * The MethodDependency class wraps a {@link MethodReference} and a boolean that
 * indicates if the method is native.
 * <p>
 */
public class MethodDependency extends Dependency {

	private final MethodReference methodRef;

	/**
	 * Indicates by {@code true} that the method referenced by
	 * {@link MethodDependency#methodRef} is native. Equals {@code false} by
	 * default.
	 */
	private boolean isNative;

	/**
	 * Instantiate a {@code MethodDependency} object from {@code methodRef} argument
	 * The value of {@link MethodDependency#isNative()} must be set with the
	 * {@link MethodDependency#setNative()}
	 *
	 * @param methodRef the method reference {@link MethodReference}
	 */
	public MethodDependency(MethodReference methodRef) {
		this.methodRef = methodRef;
	}

	/**
	 * Sets the value of {@link MethodDependency#isNative()} to {@code true}
	 */
	public void setNative() {
		this.isNative = true;
	}

	/**
	 *
	 * @return {@link MethodDependency#isNative()} value
	 */
	public boolean isNative() {
		return isNative;
	}

	/**
	 * Gets the methodRef.
	 * @return the methodRef.
	 */
	public MethodReference getMethodRef() {
		return methodRef;
	}
}
