/*
 * Java
 *
 * Copyright 2011-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.filesystem;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

class FileCreator<T> implements IFileVisitableHandler<T> {

	@Override
	public void handle(FileVisitable file, List<T> pool) {
		try {
			file.create();
		} catch (IOException e) {
			PrintStream errorStream = System.err;
			assert (errorStream != null);
			errorStream.println("Can't create " + file.getName());
		}
	}
}
