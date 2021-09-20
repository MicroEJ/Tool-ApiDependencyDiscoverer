/*
 * Java
 *
 * Copyright 2013-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
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
