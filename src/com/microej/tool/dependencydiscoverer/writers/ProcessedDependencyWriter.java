/*
 * Java
 *
 * Copyright 2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.writers;

import java.io.PrintStream;

import com.microej.tool.dependencydiscoverer.analysis.FieldDependency;
import com.microej.tool.dependencydiscoverer.analysis.MethodDependency;
import com.microej.tool.dependencydiscoverer.analysis.MethodReference;
import com.microej.tool.dependencydiscoverer.analysis.TypeDependency;

/**
 * Define a processed writer that will write dependencies in a file in a format
 * specified by subclasses.
 */
public abstract class ProcessedDependencyWriter extends AbstractDependencyWriter {

	private static final String REQUIRES = "require";
	private static final String TYPE = "type";
	private static final String METHOD = "method";
	private static final String NATIVE = "native";
	private static final String FIELD = "field";

	@Override
	public void write(PrintStream out) {
		writeStartElement(out, REQUIRES);

		assert (typesDep != null);
		for (TypeDependency dep : typesDep) {
			if (dep.isNotFound()) {
				String serialType = JavaDescriptorSerializer.serializeFullTypeName(dep.getName());
				writeElementWithAttributeName(out, TYPE, serialType);
			}
		}

		assert (fieldsDep != null);
		for (FieldDependency dep : fieldsDep) {
			if (dep.isNotFound()) {
				String serialField = JavaDescriptorSerializer.serializeField(dep.getFieldReference().getTypeName(),
						dep.getFieldReference().getFieldName());
				writeElementWithAttributeName(out, FIELD, serialField);
			}
		}

		assert (methodsDep != null);
		for (MethodDependency dep : methodsDep) {
			if (dep.isNotFound()) {
				MethodReference ref = dep.getMethodRef();
				String serialMethod = JavaDescriptorSerializer.serializeMethodTypePrintableDescriptor(ref.getOwner(), ref.getName(),
						ref.getDescriptor());
				writeElementWithAttributeName(out, METHOD, serialMethod);

			}
		}

		assert (methodsDep != null);
		for (MethodDependency dep : methodsDep) {
			if (dep.isNative()) {
				MethodReference ref = dep.getMethodRef();
				String serialMethod = JavaDescriptorSerializer.serializeMethodTypePrintableDescriptor(ref.getOwner(), ref.getName(),
						ref.getDescriptor());
				writeElementWithAttributeName(out, NATIVE, serialMethod);
			}
		}

		writeEndElement(out, REQUIRES);
	}

	/**
	 * Write the start element of the implemented format on the given output stream.
	 *
	 * @param out stream to write on.
	 * @param key outermost element key.
	 */
	protected abstract void writeStartElement(PrintStream out, String key);

	/**
	 * Write a dependency on the given output stream.
	 *
	 *
	 * @param out           stream to write on.
	 * @param element       the type of dependency. Possible values are :
	 *                      {@link ProcessedDependencyWriter#FIELD},
	 *                      {@link ProcessedDependencyWriter#METHOD},
	 *                      {@link ProcessedDependencyWriter#NATIVE} or
	 *                      {@link ProcessedDependencyWriter#TYPE}.
	 * @param attributeName formatted name of the dependency.
	 */
	protected abstract void writeElementWithAttributeName(PrintStream out, String element, String attributeName);

	/**
	 * Write the end element of the implemented format on the given output stream.
	 *
	 * @param out stream to write on.
	 * @param key outermost element key.
	 */
	protected abstract void writeEndElement(PrintStream out, String key);

}
