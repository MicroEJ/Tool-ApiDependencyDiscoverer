/*
 * Java
 *
 * Copyright 2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.writers;

/**
 * A java descriptor serializer. Can converts any java bytecode descriptor
 * represented in the class file format to a printable form.
 */
public class JavaDescriptorSerializer {

	/**
	 * Constructor keyword
	 */
	private static final String INIT = "<init>";

	private JavaDescriptorSerializer() {
		throw new IllegalStateException("Serializer class");
	}

	/**
	 * Serialize the given typeName.
	 *
	 * @param typeName
	 * @return serialized full type name
	 */
	public static String serializeFullTypeName(String typeName) {
		return serializeFullTypeName(typeName, 0, typeName.length());
	}

	/**
	 * @param typeName
	 * @param fieldName
	 * @return the serialized field
	 */
	public static String serializeField(String typeName, String fieldName) {
		StringBuilder builder = new StringBuilder();
		builder.append(serializeFullTypeName(typeName));
		builder.append('.').append(fieldName);
		return builder.toString();
	}

	/**
	 * Formats the input parameters into a single printable method descriptor.
	 *
	 * @param owner      of the method.
	 * @param name       of the method.
	 * @param descriptor of the method.
	 * @return the formated method name.
	 */
	public static String serializeMethodTypePrintableDescriptor(String owner, String name, String descriptor) {
		StringBuilder builder = new StringBuilder();
		builder.append(serializeFullTypeName(owner));
		builder.append('.');
		if (String.valueOf(name).equals(INIT)) {
			builder.append(serializeSimpleTypeName(owner));
		} else {
			builder.append(name);
		}

		builder.append(serializeMethodTypePrintableDescriptor(descriptor));
		return builder.toString();
	}

	private static String serializeFullTypeName(String typeName, int start, int length) {
		int cpt = start;
		StringBuilder builder = new StringBuilder();

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
			for (; cpt < length; ++cpt) {
				c = typeName.charAt(cpt);
				if (c == '/') {
					builder.append('.');
				} else if (c != ';') {
					builder.append(c);
				}
			}
		}

		// Add the arrays back into the type
		for (int i = 0; i < nbBraces; ++i) {
			builder.append("[]");
		}

		return builder.toString();
	}

	/**
	 * Serialize the given type name.
	 *
	 * @param type
	 * @return the serialized type
	 */
	private static String serializeSimpleTypeName(String type) {
		int len = type.length();
		int cpt = len - 1;
		StringBuilder builder = new StringBuilder();

		// set cpt to the last position of the '/' character
		for (; cpt >= 0; cpt--) {
			if (type.charAt(cpt) == '/') {
				cpt++; // skip the '/' character
				break;
			}
		}

		// copy the simple type name
		for (; cpt < len; cpt++) {
			builder.append(type.charAt(cpt));
		}
		return builder.toString();
	}

	/**
	 *
	 * @param descriptor the full descriptor of a method
	 *                   &ltowner&gt.&ltname&gt(&ltparameters
	 *                   descriptors&gt)&ltreturn value descriptor&gt
	 * @return the serialized method
	 */
	private static String serializeMethodTypePrintableDescriptor(String descriptor) {
		StringBuilder builder = new StringBuilder();
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
			} else {
				if (withinArguments && !firstArgument) {
					builder.append(',');
				}

				int start = descriptorPtr;
				// Ignore the array parameters
				while (argType == '[') {
					argType = descriptor.charAt(++descriptorPtr);
				}

				if ("ZBCDFIJSV".indexOf(argType) != -1) {
					// Handle built-in types
					builder.append(serializeFullTypeName(descriptor, start, descriptorPtr + 1));
					++descriptorPtr;
				} else {
					// Handle reference types (those which start with L)
					endArgTypePtr = String.valueOf(descriptor).indexOf(';', descriptorPtr);
					builder.append(serializeFullTypeName(descriptor, start, endArgTypePtr));
					descriptorPtr = endArgTypePtr + 1;
				}

				firstArgument = false;
			}
		}
		return builder.toString();
	}

}