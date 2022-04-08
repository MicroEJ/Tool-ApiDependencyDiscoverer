/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * Define a dependency,it's callers and it's users.
 */
public class Dependency {

	/**
	 * The dependency has been found in the classpath
	 */
	public static final int STATE_FOUND_IN_CLASSPATH = 1;

	/**
	 * The dependency has been found in the againstClasspath
	 */
	public static final int STATE_FOUND_IN_AGAINST_CLASSPATH = 2;

	/**
	 * The dependency hasn't been found, it implies that the dependency must be
	 * printed.
	 */
	public static final int STATE_NOT_FOUND = 3;
	private final List<AnalyzedClassfile> users;
	private final List<AnalyzedMethod> callers;
	private int state;

	/**
	 * Initialize users and callers lists
	 */
	public Dependency(){
		users = new ArrayList<>();
		callers = new ArrayList<>();
	}

	/**
	 * @param classfile
	 */
	public void addUser(AnalyzedClassfile classfile) {
		users.add(classfile);
	}

	/**
	 * @param method
	 */
	public void addCaller(AnalyzedMethod method) {
		callers.add(method);
	}

	/**
	 * state of the dependency.
	 *
	 * @param state
	 * @see Dependency#STATE_FOUND_IN_AGAINST_CLASSPATH
	 * @see Dependency#STATE_FOUND_IN_CLASSPATH
	 * @see Dependency#STATE_NOT_FOUND
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * @return state
	 * @see Dependency#STATE_FOUND_IN_AGAINST_CLASSPATH
	 * @see Dependency#STATE_FOUND_IN_CLASSPATH
	 * @see Dependency#STATE_NOT_FOUND
	 */
	public int getState() {
		return state;
	}

	/**
	 * @return {@code true} if the state is not found, else {@code false}.
	 */
	public boolean isNotFound() {
		return state == STATE_NOT_FOUND;
	}

	/**
	 * @return {@code true} if the state is found in classpath, else {@code false}.
	 */
	public boolean isLoadedFromClasspath() {
		return state == STATE_FOUND_IN_CLASSPATH;
	}

}
