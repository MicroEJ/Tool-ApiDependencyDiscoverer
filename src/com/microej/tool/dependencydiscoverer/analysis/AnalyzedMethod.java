/*
 * Java
 *
 * Copyright 2013-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.analysis;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

class AnalyzedMethod extends AnalyzedMember {
	private final MethodNode md;

	public AnalyzedMethod(ClassNode declaringType, MethodNode md) {
		super(declaringType);
		this.md = md;
	}

	/**
	 * Gets the md.
	 * @return the md.
	 */
	public MethodNode getMd() {
		return md;
	}
}
