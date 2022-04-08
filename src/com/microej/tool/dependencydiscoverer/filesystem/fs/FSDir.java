/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.filesystem.fs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.NoSuchElementException;

import com.microej.tool.dependencydiscoverer.filesystem.DirectoryVisitable;
import com.microej.tool.dependencydiscoverer.filesystem.IDirectoryIterator;
import com.microej.tool.dependencydiscoverer.filesystem.IFileSystemVisitable;

/**
 * Abstract representation of a directory, wraps a {@link File} object and a
 * list of visitable children.
 */
public class FSDir extends DirectoryVisitable {

	private final File wrappedDirectory;
	private final IFileSystemVisitable[] abstractChildren;

	/**
	 * Create a {@code FSDir} object from the given {@link File}, {@code throw}
	 * {@link IllegalArgumentException} if the parameter isn't a directory. If the
	 * parameter is valid it will then populate the list it's children.
	 *
	 * @param wrappedDirectory
	 */
	public FSDir(File wrappedDirectory) {
		if (!wrappedDirectory.isDirectory()) {
			throw new IllegalArgumentException();
		}
		this.wrappedDirectory = wrappedDirectory;

		File[] children = wrappedDirectory.listFiles();
		int nChildren = children.length;
		abstractChildren = new IFileSystemVisitable[nChildren];
		for (int i = nChildren; --i >= 0;) {
			File currentChild = children[i];
			abstractChildren[i] = currentChild.isDirectory() ? new FSDir(currentChild) : new FSFile(currentChild);
		}
	}

	@Override
	public IDirectoryIterator iterator() {
		return new IDirectoryIterator() {
			private int currentChildPtr; // = 0; // vm done

			@Override
			public boolean hasNext() {
				IFileSystemVisitable[] localAbstractChildren = FSDir.this.abstractChildren;
				return currentChildPtr < localAbstractChildren.length;
			}

			@Override
			public IFileSystemVisitable next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				IFileSystemVisitable[] localAbstractChildren = FSDir.this.abstractChildren;
				IFileSystemVisitable currentChild = localAbstractChildren[currentChildPtr++];
				assert (currentChild != null);
				return currentChild;
			}

			@Override
			public void remove() {
				// no-op
			}
		};
	}

	@Override
	public String getName() {
		String pathName = wrappedDirectory.getAbsolutePath();
		assert (pathName != null);
		return pathName;
	}

	@Override
	public boolean create() {
		return wrappedDirectory.mkdirs();
	}

	@Override
	public boolean delete() {
		try {
			Files.delete(wrappedDirectory.toPath());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
