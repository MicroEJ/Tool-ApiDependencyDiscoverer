/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.classfinder;



/**
 * <p>
 * Accept only class files that match a.b.*.
 * </p>
 */
public class WildCardClassfileFilter extends AbstractNameMatchingClassfileFilter {

	/**
	 *
	 * @param classnames
	 */
	public WildCardClassfileFilter(String[] classnames){
		super(classnames);
	}

	@Override
	protected boolean match(String name, String expectedRelativeClassfilename) {
		// remove *.class
		String substring = expectedRelativeClassfilename.substring(0, expectedRelativeClassfilename.length()-(JavaClassfileNoFilter.CLASS_EXT.length()+1));
		assert (substring != null);
		expectedRelativeClassfilename = substring;
		return name.startsWith(expectedRelativeClassfilename) && name.endsWith(JavaClassfileNoFilter.CLASS_EXT);
	}

}
