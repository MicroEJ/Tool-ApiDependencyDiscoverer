/*
 * Java
 *
 * Copyright 2011-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.classfinder;


/**
 * <p>
 * Accept only class files that exactly match given classe names.
 * </p>
 */
public class ExactClassfileFilter extends AbstractNameMatchingClassfileFilter {

	/**
	 * Instantiate an exact class filter, the filter is case sensitive.
	 *
	 * @param classnames list of the classes names to be filtered
	 */
	public ExactClassfileFilter(String[] classnames){
		super(classnames);
	}

	@Override
	protected boolean match(String name, String expectedRelativeClassfilename) {
		return name.equals(expectedRelativeClassfilename);
	}

}
