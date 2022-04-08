/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.classfinder;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.microej.tool.dependencydiscoverer.filesystem.jar.JarFileVisitable;

/**
 * <p>
 * Jar File ({@link JarFile}) structure visit entry point.
 * </p>
 */
public class JarJavaClassFinder extends JavaClassFinder {

	/**
	 * Cache JAR name to JAR entries Names.
	 */
	private static final HashMap<String, HashSet<String>> entriesCache = new HashMap<>();

	/**
	 * @param jarFile must be an existing Jar file
	 * @see File#isFile()
	 */
	@Override
	protected void visit(File jarFile) throws IOException {
		// Hotspot Optimization: check if the entry is in the given jar filename.
		// The JAR file is opened (slow operation) only if entries cache has not been
		// computed and when loading the expected available resource
		String path = jarFile.getPath();
		HashSet<String> entries = entriesCache.get(path);
		if (entries == null) {
			// First time this JAR is visited => compute the cache
			entries = new HashSet<>();
			try (JarFile jf = new JarFile(path, false)) {
				Enumeration<JarEntry> entriesVect = jf.entries();
				while (entriesVect.hasMoreElements()) {
					JarEntry entry = entriesVect.nextElement();
					entries.add(entry.getName());
				}
			}
			entriesCache.put(path, entries);
		}

		// the filter can't be null by construction
		IJavaClassfileFilter javaClassNameFilter = this.javaClassNameFilter;
		assert (javaClassNameFilter != null);

		Vector<JarEntry> v = new Vector<>(); // NOSONAR use Vector as temporary container to build an Enumeration
		for (String entry : entries) {
			if (javaClassNameFilter.accept(entry)) {
				v.add(new JarEntry(entry));
			}
		}
		if (!v.isEmpty()) {
			// at least one entry matches the filter => open the JAR file again
			try (JarFile jf = new JarFile(path)) {
				new JarFileVisitable(jf, v.elements()).visitUsing(this);
			}
		}
	}
}
