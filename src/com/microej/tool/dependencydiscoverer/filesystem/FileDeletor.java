/*
 * Java
 *
 * Copyright 2011-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.filesystem;

import java.util.List;

class FileDeletor<T> implements IFileVisitableHandler<T> {

	@Override
	public void handle(FileVisitable file, List<T> pool) {
		file.delete();
	}

}
