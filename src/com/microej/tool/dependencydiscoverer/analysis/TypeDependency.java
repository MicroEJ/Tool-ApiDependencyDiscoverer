/*
 * Java
 *
 * Copyright 2013-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.analysis;

import org.eclipse.jdt.annotation.Nullable;
import org.objectweb.asm.tree.ClassNode;

/**
 * The TypeDependency class wraps a String containing a type name.<br>
 * Type formatting must be done outside of this class
 *
 */
public class TypeDependency extends Dependency {
	private final String name;

	/**
	 * null if {@link #isNotFound()}
	 */
	@Nullable
	public ClassNode classfile;

	/**
	 * Instantiate a {@code TypeDependency} object from {@code type} argument
	 *
	 * @param type
	 */
	public TypeDependency(String type) {
		this.name = type;
	}

	/**
	 * Gets the name.
	 * @return the name.
	 */
	public String getName() {
		return name;
	}
}
