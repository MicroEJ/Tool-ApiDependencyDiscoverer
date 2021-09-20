/*
 * Java
 *
 * Copyright 2014-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
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
