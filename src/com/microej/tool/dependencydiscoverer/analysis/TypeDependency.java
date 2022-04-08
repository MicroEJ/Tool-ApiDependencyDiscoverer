/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
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
