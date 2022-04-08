/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.jdt.annotation.Nullable;

import com.microej.tool.dependencydiscoverer.analysis.DependencyDiscoverer;
import com.microej.tool.dependencydiscoverer.error.ErrorTaskContainer;

/**
 * Wrapper to test the dependency discoverer
 */
public class DependencyDiscovererTask extends Task{

	/**
	 * Dependency discoverer on which to perform the tests
	 */
	protected DependencyDiscoverer dd;

	/**
	 * Dependency discoverer options to use during the test
	 */
	protected DependencyDiscovererOptions options;
	private @Nullable ErrorTaskContainer errorTask;

	/**
	 * Create a {@code DependencyDiscovererTask} object, instantiate a
	 * {@link DependencyDiscoverer} object to test and a default
	 * {@link DependencyDiscovererOptions}.
	 */
	public DependencyDiscovererTask(){
		dd = new DependencyDiscoverer();
		this.options = dd.newOptions();
		dd.setOptions(this.options);
	}

	/**
	 * Sets the classpath in the {@link DependencyDiscovererOptions} options of the
	 * tested dependency discoverer instance.
	 *
	 * @param path to classpath directory.
	 *
	 * @see DependencyDiscovererOptions#setClasspath(String)
	 */
	public void setClasspath(String path){
		options.setClasspath(path);
	}

	/**
	 * Sets the output result type in the {@link DependencyDiscovererOptions}
	 * options of the tested dependency discoverer instance.
	 *
	 * @param type
	 *
	 * @see DependencyDiscovererOptions#setOutputType(String)
	 */
	public void setOutputType(String type){
		options.setOutputType(type);
	}

	/**
	 * Sets the output file in the {@link DependencyDiscovererOptions} options of
	 * the tested dependency discoverer instance.
	 *
	 * @param path to output file.
	 *
	 * @see DependencyDiscovererOptions#setClasspath(String)
	 */
	public void setOutputFile(String path) {
		options.setOutputFile(path);
	}


	/**
	 * Sets the againstClasspath in the {@link DependencyDiscovererOptions} options
	 * of the tested dependency discoverer instance.
	 *
	 * @param path to againstClasspath directory.
	 *
	 * @see DependencyDiscovererOptions#setClasspath(String)
	 */
	public void setAgainstClasspath(@Nullable String path) {
		options.setAgainstClasspath(path);
	}


	/**
	 * Sets the error task container
	 *
	 * @param errorTask to set
	 */
	public void setErrorTaskContainer(ErrorTaskContainer errorTask){
		this.errorTask = errorTask;
	}

	/**
	 * Sets entry points to test
	 *
	 * @param entryPoints
	 * @see DependencyDiscovererOptions#addEntryPoint(String)
	 */
	public void setEntryPoints(String entryPoints){
		String[] elements = DependencyDiscoverer.splitRemoveEmpty(entryPoints, ",");
		for(String e: elements){
			if (e != null) {
				options.addEntryPoint(e);
			}
		}
	}

	@Override
	public void execute() throws BuildException {// NOSONAR keep build exception for documentation
		dd.run();
		if(errorTask != null){
			errorTask.outputError(dd.getErrorHandler());
		}
		else{
			boolean hasErrors = dd.getErrorHandler().hasError();
			dd.getErrorHandler().outputError();
			if(hasErrors) {
				throw new BuildException("Ends with errors");
			}
		}
	}

}
