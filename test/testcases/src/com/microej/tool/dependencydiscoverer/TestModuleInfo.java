/*
 * Java
 *
 * Copyright 2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.microej.tool.dependencydiscoverer.analysis.DependencyDiscoverer;

/**
 *
 */
public class TestModuleInfo {

	private static final String HOME_TO_TEST_JAR = "/../testpaths/jar-classpath/test-java11-ModuleInfo/";

	private static final String TEST_JAR_MODULE_INFO = "/test.jar";

	private static final String ENTRY_POINTS = "*";
	private static final String OUTPUT_TYPE = "txt";

	private String jarDirPath;
	private DependencyDiscoverer dd;

	// Results

	private static final String SEPARATOR = System.lineSeparator();
	private static final String TEST_MODULE_INFO_RESULT = "java/io/PrintStream" + SEPARATOR + "java/lang/Object"
			+ SEPARATOR + "java/lang/System" + SEPARATOR + "java/util/Calendar" + SEPARATOR + "java/lang/System.out"
			+ SEPARATOR + "java/io/PrintStream.println(Ljava/lang/String;)V" + SEPARATOR + "java/lang/Object.<init>()V"
			+ SEPARATOR + "java/util/Calendar.getCalendarType()Ljava/lang/String;" + SEPARATOR
			+ "java/util/Calendar.getInstance()Ljava/util/Calendar;" + SEPARATOR;

	@Before
	public void setup() {
		jarDirPath = new File(System.getProperty("user.dir") + HOME_TO_TEST_JAR).getAbsolutePath();
		Assert.assertTrue(jarDirPath + " not found", new File(jarDirPath).exists());
		dd = new DependencyDiscoverer();
	}

	@Test
	public void testModuleInfo() {
		String classpath = jarDirPath + TEST_JAR_MODULE_INFO;
		Assert.assertTrue(new File(classpath).exists());
		dd.setOptions(classpath, null, null, OUTPUT_TYPE, ENTRY_POINTS);
		ByteArrayOutputStream ddOut = new java.io.ByteArrayOutputStream(); // our out
		PrintStream sOut = System.out; // system default out
		System.setOut(new java.io.PrintStream(ddOut));
		dd.run();
		System.setOut(new java.io.PrintStream(sOut));
		Assert.assertEquals("Output doesn't match expected", TEST_MODULE_INFO_RESULT, ddOut.toString());
	}

}
