/*
 * Java
 *
 * Copyright 2013-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
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
