/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
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
