/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.filesystem;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstract visitable file, define {@link FileVisitable#getInputStream()}
 * methods to read file content.
 */
public abstract class FileVisitable implements IFileSystemVisitable {

	@Override
	public void visitUsing(IFileSystemVisitor visitor) {
		visitor.visitFile(this);
	}

	/**
	 * @return inputStream of the file content.
	 * @throws IOException
	 */
	public abstract InputStream getInputStream() throws IOException;

}
