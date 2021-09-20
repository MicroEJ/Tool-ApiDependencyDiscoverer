/*
 * Java
 *
 * Copyright 2011-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
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
