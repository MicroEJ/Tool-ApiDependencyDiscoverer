/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.filesystem.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import com.microej.tool.dependencydiscoverer.filesystem.FileVisitable;

/**
 * Abstract representation of a file,wraps a{@link File} object.
 */
public class FSFile extends FileVisitable {

	private final File wrappedFile;

	/**
	 * Create a {@code FSFile} object from the given {@link File}, {@code throw}
	 * {@link IllegalArgumentException} if the parameter isn't a file.
	 *
	 * @param wrappedFile
	 */
	public FSFile(File wrappedFile) {
		if (!wrappedFile.isFile()) { // ensures validity and existence
			throw new IllegalArgumentException();
		}
		this.wrappedFile = wrappedFile;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(wrappedFile);
	}

	@Override
	public String getName() {
		String pathName = wrappedFile.getAbsolutePath();
		assert (pathName != null);
		return pathName;
	}

	@Override
	public boolean create() throws IOException {
		return wrappedFile.createNewFile();
	}

	@Override
	public boolean delete() {
		try {
			Files.delete(wrappedFile.toPath());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
