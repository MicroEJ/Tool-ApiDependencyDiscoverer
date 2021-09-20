/*
 * Java
 *
 * Copyright 2008-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.error;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @see ErrorTaskContainer
 */
public class ErrorDescription {

	@Nullable
	private String errorName;

	private int kind;

	// a part of the printed message that should match (optional)
	@Nullable
	private String matchMessage;

	private boolean isWarning;

	/**
	 * Sets the kind of error
	 *
	 * @param kind
	 */
	public void setKind(int kind){
		this.kind = kind;
	}

	/**
	 * Sets matchMessage value
	 *
	 * @param matchMessage
	 */
	public void setMatchMessage(String matchMessage){
		this.matchMessage = matchMessage;
	}

	/**
	 * Sets errorName value
	 *
	 * @param errorName
	 */
	public void setErrorName(String errorName){
		this.errorName = errorName;
	}

	/**
	 * Sets isWarning value with warning value.
	 *
	 * @param warning
	 *
	 */
	public void setWarning(boolean warning){
		this.isWarning = warning;
	}

	/**
	 * Gets the errorName.
	 *
	 * @return the errorName.
	 */
	public @Nullable String getErrorName() {
		return errorName;
	}

	/**
	 * Gets the kind.
	 *
	 * @return the kind.
	 */
	public int getKind() {
		return kind;
	}

	/**
	 * Gets the matchMessage.
	 *
	 * @return the matchMessage.
	 */
	public @Nullable String getMatchMessage() {
		return matchMessage;
	}

	/**
	 * Gets the isWarning.
	 *
	 * @return the isWarning.
	 */
	public boolean isWarning() {
		return isWarning;
	}
}