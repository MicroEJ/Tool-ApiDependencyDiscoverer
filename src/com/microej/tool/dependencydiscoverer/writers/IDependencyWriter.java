/*
 * Java
 *
 * Copyright 2014-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.writers;

import java.io.PrintStream;
import java.util.List;

import com.microej.tool.dependencydiscoverer.analysis.FieldDependency;
import com.microej.tool.dependencydiscoverer.analysis.MethodDependency;
import com.microej.tool.dependencydiscoverer.analysis.TypeDependency;


/**
 * Interfaces for DependencyWriters
 *
 * @see AbstractDependencyWriter
 * @see TextDependencyWriter
 * @see XmlDependencyWriter
 */
public interface IDependencyWriter {

	/**
	 * Print the dependencies
	 *
	 * @param out the output PrintStream
	 *
	 */
	public void write (PrintStream out) ;

	/**
	 *
	 * @param typesDep
	 */
	public void setTypeDependencies(List<TypeDependency> typesDep);

	/**
	 *
	 * @param fieldsDep
	 */
	public void setFieldDependencies(List<FieldDependency> fieldsDep);

	/**
	 *
	 * @param methodsRef
	 */
	public void setMethodDependencies(List<MethodDependency> methodsRef);
}
