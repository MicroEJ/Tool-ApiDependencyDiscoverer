/*
 * Java
 *
 * Copyright 2021-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.writers;

import java.io.PrintStream;

import org.eclipse.jdt.annotation.Nullable;

/**
 * A dependency writer that will save the result in a .json file with an json
 * formating
 */
public class JsonDependencyWriter extends ProcessedDependencyWriter {

	private boolean comaToWrite = false;
	private @Nullable String currentElement = null;

	@Override
	protected void writeStartElement(PrintStream out, String key) {
		out.println("{");
		out.println("\t\"" + key + "\":{");
	}

	@Override
	protected void writeElementWithAttributeName(PrintStream out, String element, String attributeName) {
		writeElementStart(out, element);
		if (this.comaToWrite) {
			writeComma(out);
		}

		out.println("\t\t\t{");
		out.println("\t\t\t\t\"name\":\"" + attributeName + "\"");
		out.print("\t\t\t}");

		this.comaToWrite = true;
	}

	@Override
	protected void writeEndElement(PrintStream out, String key) {
		this.comaToWrite = false;
		writeElementEnd(out);
		out.println("\t}");
		out.print("}");
	}

	private void writeElementStart(PrintStream out, String element) {
		if (!element.equals(currentElement)) {
			writeElementEnd(out);
			out.print("\t\t\"");
			out.print(element);
			out.print("\":[");
			out.println("");
			this.currentElement = element;
			this.comaToWrite = false;
		}
	}

	private void writeElementEnd(PrintStream out) {
		if (this.currentElement != null) {
			out.println("");
			out.print("\t\t]");
			if (this.comaToWrite) {
				writeComma(out);
			} else {
				out.println();
			}
		}
	}

	private void writeComma(PrintStream out) {
		comaToWrite = false;
		out.println(",");
	}

}
