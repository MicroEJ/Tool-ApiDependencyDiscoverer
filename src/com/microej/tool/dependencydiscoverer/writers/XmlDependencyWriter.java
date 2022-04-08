/*
 * Java
 *
 * Copyright 2014-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.writers;

import java.io.PrintStream;

/**
 * A dependency writer that will save the result in a .xml file with an xml
 * formating
 */
public class XmlDependencyWriter extends ProcessedDependencyWriter {

	private static final String XML_DESCRIPTION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	@Override
	protected void writeStartElement(PrintStream out, String key) {
		out.println(XML_DESCRIPTION);
		out.println("<" + key + ">");
	}

	@Override
	protected void writeElementWithAttributeName(PrintStream out, String element, String attributeName) {
		out.println("\t<" + element + " name=\"" + attributeName + "\"/>");
	}

	@Override
	protected void writeEndElement(PrintStream out, String key) {
		out.println("</" + key + ">");
	}

}
