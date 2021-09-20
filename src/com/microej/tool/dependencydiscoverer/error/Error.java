/*
 * Java
 *
 * Copyright 2008-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */

package com.microej.tool.dependencydiscoverer.error;

/**
 * A Generic error is composed of a kind of error
 * and some characters parts that serve to compute the error message
 */
public abstract class Error extends RuntimeException {

	private static final long serialVersionUID = -1836671964897865547L;

	/**
	 * The kind of error
	 */
	protected int kind; // NOSONAR accessed in child classes

	/**
	 * Error description
	 */
	protected char[][] parts = { {} }; // NOSONAR accessed in child classes // Initialize for null analysis

	/**
	 *
	 * @return a formatted error message
	 */
	public String getErrorMessage(){
		return messageAt(kind, parts, getMessages());
	}

	/**
	 * @return all messages for this error category Messages are on the form
	 *         .*\\i.*\\j where i,j are indexes of characters parts that should be
	 *         inserted
	 *
	 */
	public abstract String[] getMessages();


	/**
	 * @return {@code true}
	 */
	public boolean isFatal(){
		//default behavior is to be a fatal error !
		return true;
	}

	/**
	 * @return {@code false}
	 */
	public boolean isWarning(){
		//default behavior is to be an error !
		return false;
	}

	/**
	 * @return a printable name that represent the error category To redefine in
	 *         subclasses to specify group of error
	 *
	 */
	public String outputName(){
		return "ERROR"; // NOSONAR will be overridden
	}

	/**
	 * @return the start position (0 based) of the error, -1 for errors that have no
	 *         source attachment Default behavior : return -1
	 */
	public int startPosition(){
		return -1;
	}

	/**
	 * @return the stop position (0 based) of the error, -1 for errors that have no
	 *         source attachment Default behavior : return -1
	 */
	public int stopPosition(){
		return -1;
	}

	/**
	 * @param index
	 * @param parts
	 * @param messages list where to insert the new error message
	 * @return the buffered error message located at index using parts at place
	 *         where an escape sequence appears. only 10 escape sequences are
	 *         available, numbered from 0 to 9 inclusive. append the error
	 *         code-[e10]
	 */
	public static String messageAt(int index, char[][] parts, String[] messages) {
		char[] message ;
		try{ message = messages[index].toCharArray() ; }
		catch(ArrayIndexOutOfBoundsException ex){
			return "--error--";
		}

		int i = 0;
		int partI = -1;
		char[] part;
		int length = message.length;
		int partLength;
		char[] newMessage;
		while (i<length){
			if (message[i]=='\\'){
				// insert part into the message
				try{ partI = Integer.parseInt(new String(new char[]{message[i+1]})); }
				catch(NumberFormatException ex){ partI = 999999; }
				try {
					part = parts[partI];
				}
				catch(ArrayIndexOutOfBoundsException ex){ part = new char[]{'?','?'}; }
				assert (part != null);
				partLength = part.length;
				newMessage = new char[length - 2 + partLength];
				//beginning
				System.arraycopy(message, 0, newMessage, 0, i);
				//part
				System.arraycopy(part,0,newMessage,i,partLength);
				//ending
				System.arraycopy(message,i+2,newMessage,i+partLength,newMessage.length-i-partLength);
				//updating
				length=length-2+partLength;
				i=i+partLength;
				message=newMessage;
			}
			else{
				i++;
			}
		}
		return "[M"+index+"] - "+String.valueOf(message);
	}
}
