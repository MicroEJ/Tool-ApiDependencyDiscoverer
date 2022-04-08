/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.filesystem;

import java.util.List;

class FileDeletor<T> implements IFileVisitableHandler<T> {

	@Override
	public void handle(FileVisitable file, List<T> pool) {
		file.delete();
	}

}
