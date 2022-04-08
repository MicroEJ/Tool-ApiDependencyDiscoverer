/*
 * Java
 *
 * Copyright 2004-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer.error;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Generic error handler
 * 3 kind of errors
 * - Global errors
 * - Errors on a file
 * - Errors on a source file
 * Subclasses may redefine the dump method
 */
public class ErrorHandler {

	// MilitsaErrorNotification

	List<ErrorNotification> allNotifications;

	int currentNotification; // -1 is none


	/**
	 * Instantiate an Error handler
	 */
	public ErrorHandler(){
		//zero based (i.e. -1 is for no elt)
		allNotifications = new ArrayList<>();
		currentNotification = -1;
	}

	/**
	 * Add an error and the analyzed file when it occurred without specifying its
	 * source.
	 *
	 * @param filename
	 * @param ex       the error
	 */
	public void addErrorOnFile(String filename, Error ex){
		addErrorOnSource(null, filename, ex);
	}

	/**
	 * Add an error, the analyzed file when it occurred and its source.
	 *
	 * @param source
	 * @param filename
	 * @param ex       the error
	 */
	public void addErrorOnSource(char @Nullable [] source, @Nullable String filename, Error ex) {
		allNotifications.add(new ErrorNotification(source, filename, ex));
		currentNotification++;
	}

	/**
	 * Add an error without specifying its source nor a current analyzed file.
	 *
	 * @param ex the error
	 */
	public void addNoFile(Error ex){
		addErrorOnSource(null, null, ex);
	}

	/**
	 * Convert output to a printstream, call dump.
	 *
	 * @see #ErrorHandler.dump(PrintStream, Error, int, char[], String)
	 *
	 * @param output
	 * @param ex
	 * @param errorIndex
	 * @param source
	 * @param filename
	 */
	public void dump(OutputStream output,Error ex, int errorIndex, char[] source,String filename){
		dump(new PrintStream(output), ex, errorIndex, source, filename);
	}

	/**
	 * Print the error on the output Subclasses could override this method source
	 * and filename may be null according to the accuracy of the position the error
	 *
	 * @param output
	 * @param ex
	 * @param errorIndex
	 * @param source
	 * @param filename
	 */
	public void dump(PrintStream output, Error ex, int errorIndex, char @Nullable [] source, // NOSONAR source can be
			// used in override
			// methods
			@Nullable String filename) {
		output.println(errorIndex+" : "+ex.outputName()+" :");
		output.println(ex.getErrorMessage());
	}

	/**
	 * Calls an output of errors on the console.
	 *
	 * @return outputError call's result
	 *
	 * @see #outputError(PrintStream)
	 *
	 */
	public boolean outputError(){
		//return true only on severe fatal errors
		//side effect on some standard output.
		PrintStream output = System.out;// NOSONAR Method meant to print on console
		assert (output != null);
		return outputError(output);
	}

	/**
	 * Output errors on the given PrintStream
	 *
	 * @param out PrintStream on which to print
	 * @return <CODE>true</CODE> if the error handler contains
	 *         errors,<CODE>false</CODE> otherwise
	 */
	public boolean outputError(PrintStream out){
		//return true only on severe fatal errors
		//side effect on some standard output.

		if (currentNotification == -1)
		{
			return false; //no error
		}

		boolean fatal = false;
		int dumpIndex = 0;
		//some side effects on System.out ....
		for (int i = 0; i <= currentNotification; i++) {
			ErrorNotification notification = allNotifications.get(i);
			assert (notification != null);
			dump(out, notification.getError(), ++dumpIndex, notification.getSource(), notification.getFilename());
			fatal |= notification.isFatal();
		}
		// Remove Warnings
		currentNotification = -1;
		return fatal;
	}

	/**
	 *
	 * @return {@code true} if the error handler contains fatals
	 *         errors,{@code false} otherwise
	 */
	public boolean hasError(){
		for (int i=currentNotification+1; --i>=0;){
			ErrorNotification elem = allNotifications.get(i);
			assert (elem != null);
			if (elem.isFatal()) {
				return true;
			}
		}
		return false;
	}
}
