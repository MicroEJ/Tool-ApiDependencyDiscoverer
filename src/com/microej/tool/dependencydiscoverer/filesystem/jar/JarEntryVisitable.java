/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.filesystem.jar;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.microej.tool.dependencydiscoverer.filesystem.FileVisitable;

/**
 * Wrapper of {@link JarEntry} that gives methods to visit it.
 *
 * @see JarEntryVisitable#getInputStream()
 */
public class JarEntryVisitable extends FileVisitable {

	private final JarFile owner;
	private final JarEntry wrappedEntry;

	/**
	 * Constructor
	 *
	 * @param owner        jarFile containing the entry.
	 * @param wrappedEntry jar file to visit.
	 */
	public JarEntryVisitable(JarFile owner, JarEntry wrappedEntry) {
		this.owner = owner;
		this.wrappedEntry = wrappedEntry;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return owner.getInputStream(wrappedEntry);
	}

	@Override
	public String getName() {
		String name = wrappedEntry.getName();
		assert (name != null);
		return name;
	}

	@Override
	public boolean create() throws IOException {
		return false; // already exists!
	}

	@Override
	public boolean delete() {
		return false; // implementable?
	}

}
