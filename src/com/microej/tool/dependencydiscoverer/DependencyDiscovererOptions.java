/*
 * Java
 *
 * Copyright 2013-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import com.microej.tool.dependencydiscoverer.analysis.DependencyDiscoverer;

/**
 * Option list used to perform the analysis by the DependencyDiscoverer
 *
 * @see DependencyDiscoverer
 */
public class DependencyDiscovererOptions {

	/**
	 * The entry points of the user application (on the form a.b.C, a.b.*)
	 */
	private final ArrayList<String> entryPoints;


	/**
	 * The classpath used to retrieve .class files
	 */
	@Nullable
	private String classpath;

	/**
	 * The classpath where {@link #classpath} is compared against.
	 * Optional. May be null.
	 */
	@Nullable
	private String againstClasspath;

	@Nullable
	private String outputFile;

	@Nullable
	private String outputType;

	/**
	 * Constructor, only {@code entryPoints} list is initialized to prevent a
	 * {@link NullPointerException} in the
	 * {@link DependencyDiscovererOptions#addEntryPoint(String)} method.
	 */
	public DependencyDiscovererOptions(){
		entryPoints = new ArrayList<>();
	}


	/**
	 * Adds the given entryPoint to the entryPoints list.
	 *
	 * @param name of the entryPoint to add
	 */
	public void addEntryPoint(String name){
		entryPoints.add(name);
	}

	/**
	 * Gets the entry points list.
	 *
	 * @return the entry points list.
	 */
	public List<String> getEntryPoints() {
		return entryPoints;
	}

	/**
	 *
	 * @param classpath        path to the directory containing the jars to tests.
	 * @param againstClasspath
	 * @param outputFile       name of the output file.
	 * @param outputType       type of the output file (list of accepted types in
	 *                         the CLI description).
	 * @param entryPoints      list of entry points, added to the current list.
	 */
	public void setOptions(String classpath, String againstClasspath, String outputFile, String outputType,
			String entryPoints) {
		this.classpath = classpath;
		this.againstClasspath = againstClasspath;
		this.outputFile = outputFile;
		this.outputType = outputType;
		addEntryPoint(entryPoints);
	}

	/**
	 * Gets the classpath.
	 *
	 * @return the classpath.
	 */
	public @Nullable String getClasspath() {
		return classpath;
	}

	/**
	 * Gets the againstClasspath.
	 *
	 * @return the againstClasspath.
	 */
	public @Nullable String getAgainstClasspath() {
		return againstClasspath;
	}

	/**
	 * Gets the outputFile.
	 *
	 * @return the outputFile.
	 */
	public @Nullable String getOutputFile() {
		return outputFile;
	}

	/**
	 * Gets the outputType.
	 *
	 * @return the outputType.
	 */
	public @Nullable String getOutputType() {
		return outputType;
	}

	/**
	 * Sets the classpath.
	 *
	 * @param classpath the classpath to set.
	 */
	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}

	/**
	 * Sets the againstClasspath.
	 *
	 * @param againstClasspath the againstClasspath to set.
	 */
	public void setAgainstClasspath(@Nullable String againstClasspath) {
		this.againstClasspath = againstClasspath;
	}

	/**
	 * Sets the outputFile.
	 *
	 * @param outputFile the outputFile to set.
	 */
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	/**
	 * Sets the outputType.
	 *
	 * @param outputType the outputType to set.
	 */
	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	@Override
	public String toString() {
		String res = "";
		res = res.concat("Classpath : " + this.classpath + "\n");
		res = res.concat("AgainstClasspath : " + this.againstClasspath + "\n");
		res = res.concat("Output file path : " + this.outputFile + "\n");
		res = res.concat("Output type : " + this.outputType + "\n");
		for (String entryPoint : this.entryPoints) {
			res = res.concat("EntryPoint : " + entryPoint + "\n");
		}
		assert (res != null);
		return res;
	}
}

