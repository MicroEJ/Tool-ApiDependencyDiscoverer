/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.analysis;

import org.objectweb.asm.tree.ClassNode;

class AnalyzedMember {
	private final ClassNode declaringType;

	public AnalyzedMember(ClassNode declaringType) {
		this.declaringType = declaringType;
	}

	/**
	 * Gets the declaringType.
	 * @return the declaringType.
	 */
	public ClassNode getDeclaringType() {
		return declaringType;
	}

}
