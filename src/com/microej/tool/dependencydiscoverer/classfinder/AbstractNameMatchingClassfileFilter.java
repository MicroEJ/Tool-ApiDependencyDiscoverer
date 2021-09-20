/*
 * Java
 *
 * Copyright 2013-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.classfinder;

import java.util.Arrays;

import org.eclipse.jdt.annotation.Nullable;

/**
 * <p>
 * Accept class files that match given classe names.
 * </p>
 */
public abstract class AbstractNameMatchingClassfileFilter implements IJavaClassfileFilter {

	private final String[] expectedRelativeClassfilenames;

	/**
	 * Classnames on the form a.b.C or a/b/C
	 *
	 * @param classnames
	 */
	public AbstractNameMatchingClassfileFilter(String[] classnames) {
		this.expectedRelativeClassfilenames = new String[classnames.length];
		int ptr = 0;
		//get class file path from class name
		for(String classname : classnames){
			classname = classname.replace('.', '/');
			expectedRelativeClassfilenames[ptr++] = classname + JavaClassfileNoFilter.CLASS_EXT;
		}
	}

	@Override
	public final boolean accept(String name) {
		for(String expectedRelativeClassfilename : expectedRelativeClassfilenames){
			String localExpectedRelativeClassfilename = expectedRelativeClassfilename;
			assert (localExpectedRelativeClassfilename != null);
			if (match(name, localExpectedRelativeClassfilename)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param name                          of the class
	 * @param expectedRelativeClassfilename , name of the class to compare with
	 *                                      without package informations
	 * @return {@code true} is the parameters match, {@code false} otherwise.
	 */
	protected abstract boolean match(String name, String expectedRelativeClassfilename);

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(expectedRelativeClassfilenames);
		return result;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AbstractNameMatchingClassfileFilter other = (AbstractNameMatchingClassfileFilter) obj;
		if (!Arrays.equals(expectedRelativeClassfilenames, other.expectedRelativeClassfilenames)) {
			return false;
		}
		return true;
	}

}