/*
 * Java
 *
 * Copyright 2014-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.writers;

import java.io.PrintStream;

import com.microej.tool.dependencydiscoverer.analysis.FieldDependency;
import com.microej.tool.dependencydiscoverer.analysis.MethodDependency;
import com.microej.tool.dependencydiscoverer.analysis.MethodReference;
import com.microej.tool.dependencydiscoverer.analysis.TypeDependency;

/**
 * A dependency writer that will save the result in a .xml file with an xml
 * formating
 */
public class XmlDependencyWriter extends AbstractDependencyWriter {
	private static final String REQUIRES = "require";
	private static final String TYPE = "type";
	private static final String METHOD = "method";
	private static final String NATIVE = "native";
	private static final String FIELD = "field";
	private static final String NAME = "name";

	private static final String INIT = "<init>";
	private static final String PROLOGUE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	@Override
	public void write(PrintStream out) {
		out.println(PROLOGUE);
		writeStartElement(out);

		assert (typesDep != null);
		for(TypeDependency dep : typesDep){
			if(dep.isNotFound()){
				writeType(out, dep.getName());
			}
		}


		assert (fieldsDep != null);
		for(FieldDependency dep : fieldsDep){
			if(dep.isNotFound()){
				writeField(out, dep.getFieldReference().getTypeName(), dep.getFieldReference().getFieldName());
			}
		}


		assert (methodsDep != null);
		for (MethodDependency dep : methodsDep) {
			if(dep.isNotFound()){
				MethodReference ref = dep.getMethodRef();
				writeMethod(out, ref.getOwner(), ref.getName(), ref.getDescriptor());
			}
		}


		assert (methodsDep != null);
		for (MethodDependency dep : methodsDep) {
			if (dep.isNative()) {
				MethodReference ref = dep.getMethodRef();
				writeNative(out, ref.getOwner(), ref.getName(), ref.getDescriptor());
			}
		}

		writeEndElement(out);
	}

	private void appendConvertedFullTypeName(StringBuilder builder, String typeName) {
		appendConvertedFullTypeName(builder, typeName, 0, typeName.length());
	}

	private void appendConvertedFullTypeName(StringBuilder builder, String typeName, int start, int length) {

		int cpt = start;

		// Count the number of arrays in the type
		int nbBraces = 0;
		while (typeName.charAt(cpt) == '[') {
			++cpt;
			++nbBraces;
		}

		// Transform basic types
		char c = typeName.charAt(cpt);
		switch (c) {
		case 'Z':
			builder.append("boolean");
			break;
		case 'B':
			builder.append("byte");
			break;
		case 'C':
			builder.append("char");
			break;
		case 'D':
			builder.append("double");
			break;
		case 'F':
			builder.append("float");
			break;
		case 'I':
			builder.append("int");
			break;
		case 'J':
			builder.append("long");
			break;
		case 'S':
			builder.append("short");
			break;
		case 'V':
			builder.append("void");
			break;
		case 'L':
			++cpt;
			// Intended fallthrough
		default:
			// Transform most types
			for (; cpt < length ; ++cpt) {
				c = typeName.charAt(cpt);
				if (c == '/') {
					builder.append('.');
				}
				else if (c != ';') {
					builder.append(c);
				}
			}
		}

		// Add the arrays back into the type
		for (int i = 0 ; i < nbBraces ; ++i) {
			builder.append("[]");
		}
	}

	private void writeType(PrintStream out, String typeName) {
		StringBuilder builder = new StringBuilder();
		appendConvertedFullTypeName(builder, typeName);
		String name = builder.toString();
		assert (name != null);
		writeElementWithAttributeName(out, TYPE, name);
	}


	private void appendSimpleTypeName(StringBuilder builder, String owner) {
		int len = owner.length();
		int cpt = len - 1;

		// set cpt to the last position of the '/' character
		for (; cpt >= 0; cpt--) {
			if (owner.charAt(cpt) == '/') {
				cpt++; // skip the '/' character
				break;
			}
		}

		// copy the simple type name
		for (; cpt < len; cpt++) {
			builder.append(owner.charAt(cpt));
		}
	}

	/*
	 * methodType : (aTypes)rType | ()rType
	 * aTypes : type | type aTypes
	 * rType : type
	 * type : "java type"
	 */
	private void appendMethodType(StringBuilder builder, String descriptor) {
		int descriptorPtr = 0;
		int descriptorLength = descriptor.length();
		boolean withinArguments = false;
		boolean firstArgument = true;

		while (descriptorPtr < descriptorLength) {
			char argType = descriptor.charAt(descriptorPtr);
			int endArgTypePtr;
			if (argType == '(' || argType == ')') {
				withinArguments = argType == '(';
				builder.append(argType);
				++descriptorPtr;
			}
			else {
				if (withinArguments && !firstArgument) {
					builder.append(',');
				}

				int start = descriptorPtr;
				// Ignore the array parameters
				while(argType == '[') {
					argType = descriptor.charAt(descriptorPtr++);
				}

				if ("ZBCDFIJSV".indexOf(argType) != -1) {
					// Handle built-in types
					appendConvertedFullTypeName(builder, descriptor, start, descriptorPtr + 1);
					++descriptorPtr;
				}
				else {
					// Handle reference types (those which start with L)
					endArgTypePtr = String.valueOf(descriptor).indexOf(';', descriptorPtr);
					appendConvertedFullTypeName(builder, descriptor, start, endArgTypePtr);
					descriptorPtr = endArgTypePtr + 1;
				}

				firstArgument = false;
			}
		}
	}

	private void writeMethod(PrintStream out, String owner, String name, String descriptor) {
		String builder = getMethodPrintableName(owner, name, descriptor);
		writeMethod(out, builder, false);
	}

	private void writeNative(PrintStream out, String owner, String name, String descriptor) {
		String builder = getMethodPrintableName(owner, name, descriptor);
		writeMethod(out, builder, true);
	}

	private String getMethodPrintableName(String owner, String name, String descriptor) {
		StringBuilder builder = new StringBuilder();
		appendConvertedFullTypeName(builder, owner);
		builder.append('.');
		if (String.valueOf(name).equals(INIT)) {
			appendSimpleTypeName(builder, owner);
		} else {
			builder.append(name);
		}

		appendMethodType(builder, descriptor);
		String printableName = builder.toString();
		assert (printableName != null);
		return printableName;
	}


	private void writeMethod(PrintStream out, String name, boolean isNative) {
		writeElementWithAttributeName(out, isNative ? NATIVE : METHOD, name);
	}

	private void writeField(PrintStream out, String typeName, String fieldName) {
		StringBuilder builder = new StringBuilder();
		appendConvertedFullTypeName(builder, typeName);
		builder.append('.').append(fieldName);
		String name = builder.toString();
		assert (name != null);
		writeField(out, name);
	}

	private void writeField(PrintStream out, String name) {
		writeElementWithAttributeName(out, FIELD, name);
	}

	private void writeElementWithAttributeName (PrintStream out, String element, String attributeName) {
		out.print("\t<");
		out.print(element);
		out.print(' ');
		out.print(NAME);
		out.print("=\"");
		out.print(attributeName);
		out.println("\"/>");
	}

	private void writeStartElement(PrintStream out) {
		writeElement(out, REQUIRES, false);
	}

	private void writeEndElement(PrintStream out) {
		writeElement(out, REQUIRES, true);
	}

	private void writeElement (PrintStream out, String element, boolean end) {
		out.print('<');
		if (end) {
			out.print('/');
		}

		out.print(element);
		out.println('>');
	}
}
