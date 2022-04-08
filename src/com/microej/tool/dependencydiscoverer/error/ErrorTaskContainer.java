/*
 * Java
 *
 * Copyright 2008-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.error;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Internal task for testsuite purpose
 * Used to check errors
 * Usage
 * <linkerError>
 * 		linker options
 * 		<!-- Error messages in order -->
 * 		<errorDescription id="29"/>
 * 		<errorDescription id="10" matchMessage="aaa"/>
 * </linkerError>
 */
public class ErrorTaskContainer {

	private final ArrayList<ErrorDescription> errorDescriptions = new ArrayList<>();

	private boolean omitWarnings;

	/**
	 * Create a new ErrorDescription and adds it to the error list.
	 *
	 * @return error that have been created
	 */
	public ErrorDescription createErrorDescription(){
		ErrorDescription error = new ErrorDescription();
		errorDescriptions.add(error);
		return error;
	}

	/**
	 * Sets omitWarning value, when at {@code true}, fatal errors are printed at
	 * {@code false} will print every errors.
	 *
	 *
	 * @param value
	 */
	public void setOmitWarnings(boolean value){
		omitWarnings = value;
	}

	/**
	 * Output every error contained in the errorHandler given as parameters
	 * following omitWarning policy.
	 *
	 * @param errorHandler
	 * @throws RuntimeException if an error check occurs
	 * @see #setOmitWarnings(boolean)
	 */
	public void outputError(ErrorHandler errorHandler){
		ErrorDescription[] expectedErrors = new ErrorDescription[errorDescriptions.size()];
		errorDescriptions.toArray(expectedErrors);
		int expectedErrorPtr = -1;

		int nbErrors = errorHandler.currentNotification+1;
		List<ErrorNotification> notifications = errorHandler.allNotifications;
		PrintStream out = System.out;
		assert (out != null);

		for(int i=-1; ++i<nbErrors; ){
			ErrorNotification notification = notifications.get(i);
			assert (notification != null);
			Error error = notification.getError();
			String filename = notification.getFilename();
			char[] source = notification.getSource();
			ErrorDescription expectedError;
			try{ expectedError = expectedErrors[++expectedErrorPtr]; }
			catch(ArrayIndexOutOfBoundsException e){
				if(omitWarnings){
					for(int ptr=nbErrors; --ptr>=expectedErrorPtr;){
						ErrorNotification notificationSearch = notifications.get(ptr);
						assert (notificationSearch != null);
						Error errorSearch = notificationSearch.getError();
						if (errorSearch.isFatal()) {
							throw new RuntimeException("Not enough declared expected errors");
						}
					}
					// here, some warnings has been omitted, this is not an error
					return;
				} else {
					throw new RuntimeException("Not enough declared expected errors");
				}
			}

			if (expectedError.getKind() != error.kind) {
				if(omitWarnings && error.isWarning()){
					// warnings is allowed to be omitted
					--expectedErrorPtr;
					continue;
				}
				throw new RuntimeException(
						"Invalid Error (" + error.kind + ", expected " + expectedError.getKind() + ")");
			}

			if (expectedError.getErrorName() != null
					&& error.outputName().equalsIgnoreCase(expectedError.getErrorName())) {// dont care about case
				throw new RuntimeException(
						"Invalid error name: " + expectedError.getErrorName() + ", expected " + error.outputName());
			}

			if (expectedError.getMatchMessage() != null) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
				PrintStream ps = new PrintStream(stream);
				errorHandler.dump(ps, error, i, source, filename);
				Pattern p = Pattern.compile(expectedError.getMatchMessage(), Pattern.MULTILINE | Pattern.DOTALL);
				byte[] byteArray = stream.toByteArray();
				assert (byteArray != null);
				Matcher m = p.matcher(new String(byteArray));
				if(!m.matches()){
					errorHandler.dump(out, error, i, source, filename);
					throw new RuntimeException("Previous error does not match: " + expectedError.getMatchMessage());
				}
			}

			// dump the error for verbose infos
			errorHandler.dump(out, error, i, source, filename);
		}

		if(expectedErrorPtr+1 != expectedErrors.length){
			throw new RuntimeException("Too many declared expected errors");
		}
	}
}

