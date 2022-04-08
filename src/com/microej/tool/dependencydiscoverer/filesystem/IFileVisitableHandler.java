/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.filesystem;

import java.util.List;


interface IFileVisitableHandler<T> {

	public void handle(FileVisitable file, List<T> pool);
}
