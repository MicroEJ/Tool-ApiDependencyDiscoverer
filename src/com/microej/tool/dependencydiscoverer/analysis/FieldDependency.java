/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.analysis;

/**
 * The FieldDependency class wraps a {@link FieldReference}.<br>
 */
public class FieldDependency extends Dependency {

	private final FieldReference fieldRef;

	/**
	 * Instantiate a {@code FieldDependency} object from {@code fieldRef} argument
	 *
	 * @param fieldRef the field reference {@link FieldReference}
	 */
	public FieldDependency(FieldReference fieldRef) {
		this.fieldRef = fieldRef;
	}

	/**
	 * Gets the field reference.
	 *
	 * @return the field reference
	 */
	public FieldReference getFieldReference() {
		return fieldRef;
	}
}
