/*
 * Java
 *
 * Copyright 2008-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.error;

import org.eclipse.jdt.annotation.Nullable;


class ErrorNotification {

	private char @Nullable [] source;
	private @Nullable String filename;
	private Error error;

	public ErrorNotification(char @Nullable [] source, @Nullable String filename, Error error) {
		this.source = source;
		this.filename = filename;
		this.error = error;
	}

	public char @Nullable [] getSource() {
		return source;
	}

	@Nullable
	public String getFilename() {
		return filename;
	}

	public Error getError() {
		return error;
	}

	public void setSource(char[] source) {
		this.source = source;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public boolean isFatal() {
		return error.isFatal();
	}
}
