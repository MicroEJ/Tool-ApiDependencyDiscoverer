/*
 * Java
 *
 * Copyright 2011-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
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
