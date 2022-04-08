/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.classfinder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import com.microej.tool.dependencydiscoverer.DependencyDiscovererError;
import com.microej.tool.dependencydiscoverer.error.ErrorHandler;
import com.microej.tool.dependencydiscoverer.filesystem.FileVisitable;
import com.microej.tool.dependencydiscoverer.filesystem.RecursiveFileSystemVisitor;

/**
 * <p>
 * Find Java classes visit a File System-like structure and delegate the concrete visit to sub classes.
 * </p>
 */
public abstract class JavaClassFinder extends RecursiveFileSystemVisitor {

	/**
	 * .jar extension in a String
	 */
	public static final String JAR_EXT = ".jar";

	/**
	 * HashMap of loaded classNodes
	 */
	protected @Nullable Map<String, ClassNode> classNodeData;

	/**
	 * HashMap of loaded FinderHolder
	 */
	private static final Map<String, FinderHolder> pathTypes = new HashMap<>();

	/**
	 * <p>
	 * The Java class name filter to use while visiting the File system structure.<br>
	 * Can't be <code>null</code>, if given filter is <code>null</code>, it is replaced by a {@link JavaClassfileNoFilter} instance.
	 * </p>
	 */
	protected @Nullable IJavaClassfileFilter javaClassNameFilter;

	private @Nullable File path;

	/**
	 * @see #find(List, File[], IJavaClassfileFilter)
	 */

	/**
	 * <p>
	 * Visits the given path using a concrete {@link JavaClassFinder} instance
	 * according to the kind of path.
	 * <p>
	 * <p>
	 * For every File objects, will test once its type on disk and save it in a
	 * cache variable for future accesses.
	 * <p>
	 * The pool variable is directly modified instead of returning it.
	 *
	 * @param pool                List where are added classfiles founds
	 * @param classpath           the classpath where to search class files (may be
	 *                            composed of directories or Jar files)
	 * @param javaClassNameFilter the filter to use (may be <code>null</code>)
	 * @param errorHandler
	 * @throws IOException if any IO error occurs
	 * @throw {@link IllegalArgumentException} if the given path can't be handled by
	 *        any concrete implementation
	 */
	public static void find(Map<String, ClassNode> pool, @NonNull File[] classpath,
			IJavaClassfileFilter javaClassNameFilter, ErrorHandler errorHandler)
					throws IOException {

		JavaClassFinder concreteJavaClassFinder;
		int nPaths = classpath.length;
		for (int i = -1; ++i < nPaths;) { // keep order, in classpath notion it may be important
			File path = classpath[i];

			concreteJavaClassFinder = getFinderHolder(path).getJavaClassFinder();
			if (concreteJavaClassFinder != null) {
				concreteJavaClassFinder.concreteFind(path, javaClassNameFilter, pool);
			} else {
				// Invalid classpath entry
				String absolutePath = path.getPath();
				assert (absolutePath != null);
				errorHandler.addNoFile(new DependencyDiscovererError().invalidClasspath(absolutePath).setIsWarning(true));
			}
		}
	}

	/**
	 * Get the FinderHolder of the given path from the cache if it already is
	 * loaded. Else, it will create the appropriate finder and save it in the cache.
	 *
	 * @param path
	 * @return pathType of the input path
	 */
	private static FinderHolder getFinderHolder(File path) {
		@Nullable
		FinderHolder type = pathTypes.get(path.getPath());
		if(type==null) {
			if (path.isDirectory()) {
				type = new FinderHolder(new FSJavaClassFinder());
			} else if (path.isFile() && path.getName().endsWith(JAR_EXT)) {
				type = new FinderHolder(new JarJavaClassFinder());
			} else {
				type = new FinderHolder(null);
			}
			// type NonNull by construction
			assert (type != null);
			pathTypes.put(path.getPath(), type);
		}
		return type;

	}

	/**
	 * <p>
	 * Finds recursively all class files from the given root applying the given
	 * filter.
	 * </p>
	 * <p>
	 * If the given classname filter is <code>null</code>, a
	 * {@link JavaClassfileNoFilter} instance will be used instead.
	 * </p>
	 *
	 * @param path                the root where to find class files
	 * @param javaClassNameFilter the java class name to use (can be
	 *                            <code>null</code>)
	 * @param pool                the class file data pool to populate
	 * @throws IOException if any IO error occurs
	 */
	protected void concreteFind(File path, @Nullable IJavaClassfileFilter javaClassNameFilter,
			Map<String, ClassNode> pool)
					throws IOException {
		this.path = path;
		this.javaClassNameFilter = javaClassNameFilter == null ? new JavaClassfileNoFilter() : javaClassNameFilter;
		classNodeData = pool;
		visit(path);
	}

	/**
	 * <p>
	 * The way to visit the given path is implementation dependent.
	 * </p>
	 *
	 * @param path the root of the visit
	 * @throws IOException if any IO error occurs
	 */
	protected abstract void visit(File path) throws IOException;

	/**
	 * <p>
	 * Apply the {@link IJavaClassfileFilter} to the given file and add its
	 * {@link InputStream} to the pool if accepted.<br>
	 * Nothing is done otherwise.
	 * </p>
	 */
	@Override
	public void visitFile(FileVisitable file) {
		String name = file.getName();
		// canonize
		// path cannot be null by construction
		assert (path != null);
		String absolutePath = path.getAbsolutePath();
		if(name.startsWith(absolutePath)){
			name = name.substring(absolutePath.length()+1, name.length()); // +1: remove separator
		}

		String separator = File.separator;
		assert (separator != null);
		name = name.replace(separator, "/");//always use '/' as separator (jars use '/' and filesystem depends on the OS)

		// the filter can't be null by construction
		assert (javaClassNameFilter != null);
		assert (name != null);
		if (!javaClassNameFilter.accept(name)) {
			return;
		}

		ClassNode cn;
		try {
			cn = getNode(file);
		} catch (IOException e) {
			return;
		}
		// classNodeData can't be null by construction
		String className = cn.name;
		assert (classNodeData != null);
		classNodeData.put(className, cn);

	}

	private static ClassNode getNode(FileVisitable file) throws IOException {
		ClassReader cr = new ClassReader(file.getInputStream());
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		return cn;
	}

}

class FinderHolder {

	private final @Nullable JavaClassFinder javaClassFinder;

	public FinderHolder(@Nullable JavaClassFinder javaClassFinder) {
		this.javaClassFinder = javaClassFinder;
	}

	public @Nullable JavaClassFinder getJavaClassFinder() {
		return this.javaClassFinder;
	}


}
