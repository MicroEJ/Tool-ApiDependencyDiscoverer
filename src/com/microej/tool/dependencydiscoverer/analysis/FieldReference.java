/*
 * Java
 *
 * Copyright 2021-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.analysis;

import org.eclipse.jdt.annotation.Nullable;

/**
 *
 */
public class FieldReference {

	private final String typeName;
	private final String fieldType;
	private final String fieldName;

	/**
	 * Instantiate a FieldRefrence object with the given parameters
	 *
	 * @param typeName  type of the class containing the field
	 * @param fieldType type of the field
	 * @param fieldName name of the field
	 */
	public FieldReference(String typeName, String fieldType, String fieldName) {
		this.typeName = typeName;
		this.fieldType = fieldType;
		this.fieldName = fieldName;
	}

	/**
	 * Gets the typeName.
	 *
	 * @return the typeName.
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * Gets the fieldType.
	 *
	 * @return the fieldType.
	 */
	public String getFieldType() {
		return fieldType;
	}

	/**
	 * Gets the fieldName.
	 *
	 * @return the fieldName.
	 */
	public String getFieldName() {
		return fieldName;
	}

	@Override
	public String toString() {
		return fieldType + "." + fieldName;
	}

	@Override
	public int hashCode() {
		// default hashcode implementation generated by Eclipse
		final int prime = 31;
		int result = 1;
		result = prime * result + fieldName.hashCode();
		result = prime * result + fieldType.hashCode();
		result = prime * result + typeName.hashCode();
		return result;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		// default equals implementation generated by Eclipse
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FieldReference other = (FieldReference) obj;
		if (!fieldName.equals(other.fieldName)) {
			return false;
		}
		if (!fieldType.equals(other.fieldType)) {
			return false;
		}
		if (!typeName.equals(other.typeName)) {
			return false;
		}
		return true;
	}

}
