/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
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
