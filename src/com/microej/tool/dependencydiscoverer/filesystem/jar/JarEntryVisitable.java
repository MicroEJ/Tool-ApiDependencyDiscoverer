/*
 * Java
 *
 * Copyright 2011-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
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
