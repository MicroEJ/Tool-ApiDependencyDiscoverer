/*
 * Java
 *
 * Copyright 2014-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.writers;

import java.io.PrintStream;

import com.microej.tool.dependencydiscoverer.analysis.FieldDependency;
import com.microej.tool.dependencydiscoverer.analysis.MethodDependency;
import com.microej.tool.dependencydiscoverer.analysis.TypeDependency;

/**
 * A dependency writer that will save the result in a .txt file without
 * formating
 */
public class TextDependencyWriter extends AbstractDependencyWriter {
	@Override
	public void write(PrintStream out) {

		assert (typesDep != null);
		for (TypeDependency dep : typesDep) {
			if (dep.isNotFound()) {
				out.println(dep.getName());
			}
		}


		assert (fieldsDep != null);
		for (FieldDependency dep : fieldsDep) {
			if (dep.isNotFound()) {
				out.println(new StringBuilder().append(dep.getFieldReference().getTypeName()).append('.')
						.append(dep.getFieldReference().getFieldName()));
			}
		}


		assert (methodsDep != null);
		for (MethodDependency dep : methodsDep) {
			if (dep.isNotFound()) {
				out.println(dep.getMethodRef());
			}
		}


		assert (methodsDep != null);
		for (MethodDependency dep : methodsDep) {
			if (dep.isNative()) {
				out.println(new StringBuilder("[NATIVE] ").append(dep.getMethodRef()));
			}
		}

	}
}
