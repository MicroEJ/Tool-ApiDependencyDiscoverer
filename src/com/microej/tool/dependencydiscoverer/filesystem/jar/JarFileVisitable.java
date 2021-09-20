/*
 * Java
 *
 * Copyright 2011-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.filesystem.jar;

import java.io.IOException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.microej.tool.dependencydiscoverer.filesystem.DirectoryVisitable;
import com.microej.tool.dependencydiscoverer.filesystem.IDirectoryIterator;
import com.microej.tool.dependencydiscoverer.filesystem.IFileSystemVisitable;

/**
 * Wrapper of {@link JarFile} that gives methods to visit it, contains one or
 * more {@link JarEntry} to visit.
 *
 * @see JarFileVisitable#iterator()
 * @see JarFileVisitable#visitUsing(com.microej.tool.dependencydiscoverer.filesystem.IFileSystemVisitor)
 */
public class JarFileVisitable extends DirectoryVisitable {

	/**
	 * The opened JAR file
	 */
	protected final JarFile wrappedEntry;

	/**
	 * The entries to visit
	 */
	protected Enumeration<JarEntry> entries;

	/**
	 * Default constructor (visit all entries)
	 *
	 * @param wrappedEntry jar file to visit.
	 */
	public JarFileVisitable(JarFile wrappedEntry) {
		this.wrappedEntry = wrappedEntry;
		this.entries = wrappedEntry.entries();
	}

	/**
	 * Constructor to visit only provided entries
	 *
	 * @param wrappedEntry jar file to visit.
	 * @param entries      the subset of entries to visit.
	 */
	public JarFileVisitable(JarFile wrappedEntry, Enumeration<JarEntry> entries) {
		this.wrappedEntry = wrappedEntry;
		this.entries = entries;
	}

	@Override
	public IDirectoryIterator iterator() {
		Enumeration<JarEntry> entries = this.entries;
		return new IDirectoryIterator() {
			@Override
			public IFileSystemVisitable next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return new JarEntryVisitable(wrappedEntry, entries.nextElement());
			}

			@Override
			public boolean hasNext() {
				return entries.hasMoreElements();
			}

			@Override
			public void remove() {
				// no-op
			}
		};
	}

	@Override
	public String getName() {
		String name = wrappedEntry.getName();
		assert (name != null);
		return name;
	}

	@Override
	public boolean create() throws IOException {
		return false; // already exists
	}

	@Override
	public boolean delete() {
		return false; // nothing to do
	}


}
