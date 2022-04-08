/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.analysis;

import org.objectweb.asm.tree.ClassNode;

class AnalyzedClassfile {
	private final ClassNode classfile;

	public AnalyzedClassfile(ClassNode c) {
		this.classfile = c;

	}

	/**
	 * Gets the classfile.
	 * @return the classfile.
	 */
	public ClassNode getClassfile() {
		return classfile;
	}
}
