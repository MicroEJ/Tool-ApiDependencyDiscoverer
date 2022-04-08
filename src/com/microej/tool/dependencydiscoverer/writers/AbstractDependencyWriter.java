/*
 * Java
 *
 * Copyright 2014-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.writers;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import com.microej.tool.dependencydiscoverer.analysis.FieldDependency;
import com.microej.tool.dependencydiscoverer.analysis.MethodDependency;
import com.microej.tool.dependencydiscoverer.analysis.TypeDependency;

/**
 * An abstract dependency writer containing the common variables to every
 * writer.
 */
public abstract class AbstractDependencyWriter implements IDependencyWriter {

	/**
	 * Array of all type dependencies
	 */
	protected @Nullable List<TypeDependency> typesDep;

	/**
	 * Array of all fields dependencies
	 */
	protected @Nullable List<FieldDependency> fieldsDep;

	/**
	 * Array of all methods dependencies
	 */
	protected @Nullable List<MethodDependency> methodsDep;

	@Override
	public void setTypeDependencies(List<TypeDependency> deps) {
		this.typesDep = deps;
	}

	@Override
	public void setFieldDependencies(List<FieldDependency> deps) {
		this.fieldsDep = deps;
	}

	@Override
	public void setMethodDependencies(List<MethodDependency> deps) {
		this.methodsDep = deps;
	}
}
