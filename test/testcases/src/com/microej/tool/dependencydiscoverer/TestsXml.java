/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TestsXml {

	private static final String PACKAGE = "com.microej.tool.dependencydiscoverer.test.paths.";

	public String classpath;
	public String againstClasspath;

	private final static String NAME = "name";


	@Before
	public void setup(){
		classpath = System.getProperty("com.microej.tool.dependencydiscoverer.tests.classpath");
		if(classpath == null) {
			initClasspath();
		}
		againstClasspath = System.getProperty("com.microej.tool.dependencydiscoverer.tests.againstClasspath");
		if(againstClasspath == null) {
			againstClasspath = System.getProperty("user.dir") + "/../testpaths/bin-againstclasspath";
		}
		Assert.assertTrue(new File(classpath).exists());
		Assert.assertTrue(new File(againstClasspath).exists());
	}

	public void runDep(String ep, String[] expected){
		String depString = runDep(ep);
		String[] result = extractDependencies(depString);
		Assert.assertEquals(expected.length, result.length);
		nextExpectedDependency:for(int i=expected.length; --i>=0;){
			String expectedDependency = expected[i];
			for(int j=result.length; --j>=0;){
				String resultDependency = result[j];
				if(expectedDependency.equals(resultDependency)){
					continue nextExpectedDependency;
				}
			}
			Assert.fail("Expected "+expectedDependency);
		}
	}

	private String [] convertXmlToText (List<String> linesVect) {
		List<String> result = new ArrayList<String>();

		for (String line : linesVect) {
			int idx = line.indexOf(NAME);
			if (idx != -1){
				StringBuilder builder = new StringBuilder();
				idx += NAME.length() + 2 ; // skip '=' and '"' characters
				int length = line.length();
				while(idx < length) {
					char c = line.charAt(idx);
					//					System.out.println("Char " + c);
					if (c == '"') {
						break;
					}

					builder.append(c);
					idx ++;
				}

				result.add(builder.toString());
			}
		}
		String [] r = new String [result.size()];
		return result.toArray(r);
	}


	private String[] extractDependencies(String content) {
		ArrayList<String> linesVect = new ArrayList<String>();
		try{
			BufferedReader reader = new BufferedReader(new StringReader(content));
			String line;
			while((line=reader.readLine()) != null){
				linesVect.add(line);
			}

			return convertXmlToText(linesVect);
		}
		catch(IOException e){
			throw new AssertionError(e);
		}
	}

	public String runDep(String ep){
		File f;
		try {
			f = File.createTempFile("ddtests", ".output");
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		DependencyDiscovererTask dd = new DependencyDiscovererTask();
		dd.setOutputType("xml");
		dd.setOutputFile(f.getAbsolutePath());
		dd.setAgainstClasspath(againstClasspath);
		dd.setClasspath(classpath);
		dd.setEntryPoints(ep);
		dd.execute();

		FileInputStream fis = null;
		byte[] buffer = new byte[4024];
		StringBuilder sb = new StringBuilder();
		try{
			fis = new FileInputStream(f);
			while(true){
				int nbRead = fis.read(buffer);
				if(nbRead == -1) {
					break;
				}
				sb.append(new String(buffer, 0, nbRead, "UTF-8"));
			}
		}
		catch(IOException e){
			throw new AssertionError(e);
		}
		finally{
			try{ fis.close(); }
			catch(Throwable e){}
		}

		return sb.toString();

	}

	public void initClasspath() {

		File classpathFile = new File(System.getProperty("user.dir") + "/../testpaths/bin-classpath");

		File[] subFiles = classpathFile.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});

		if (subFiles != null && subFiles.length > 0) {
			StringBuilder classpathBuilder = new StringBuilder();
			for(File subFile:subFiles) {
				classpathBuilder
				.append((classpathBuilder.length() == 0 ? "" : File.pathSeparator) + subFile.getAbsolutePath());
			}
			classpath = classpathBuilder.toString();
		}else {
			classpath = classpathFile.getAbsolutePath();
		}
	}

	@Test
	public void testDep1(){
		runDep(PACKAGE+"Dep1", new String[]{"java.lang.Object", "java.lang.Object.Object()void"});
	}

	@Test
	public void testDep2(){
		runDep(PACKAGE+"Dep2", new String[]{"java.lang.Object", "java.lang.Object.Object()void", "java.lang.String", "java.lang.String.split(java.lang.String)java.lang.String[]"});
	}
	@Test
	public void testDep3(){
		runDep(PACKAGE+"Dep3", new String[]{"java.lang.Object", "java.lang.Object.Object()void", "java.lang.String", "java.lang.String.charAt(int)char"});
	}

	@Test
	public void testNativeListing() {
		runDep(PACKAGE + "TestNativeListing",
				new String[] { "java.lang.Object", "java.lang.Object.Object()void",
						"com.microej.tool.dependencydiscoverer.test.paths.TestNativeListing.callANativeDirectly()void",
						"com.microej.tool.dependencydiscoverer.test.paths.TestNativeListing.nativeEntryPoint()void",
		"com.microej.tool.dependencydiscoverer.test.paths.TestNativeListing2.callANativeIndirectly()void" });
	}

	/*
	@Test
	public void testDepDotClass(){
		runDep(PACKAGE+"DepDotClass", new String[]{"java.lang.Object", "java.lang.Object.Object()void", "java.util.Vector", "java.lang.Class"});
	}

	@Test
	public void testDepField1(){
		runDep(PACKAGE+"DepField1", new String[]{"java.lang.Object", "java.lang.Object.Object()void", "java.io.PrintStream", "java.io.InputStream", "java.lang.System", "java.lang.System.in", "java.lang.System.out"});
	}

	@Test
	public void testDepAbstract(){
		runDep(PACKAGE+"DepAbstract", new String[]{"java.lang.Object", "java.lang.Object.Object()void"});
	}

	@Test
	public void testDepStub(){
		runDep(PACKAGE+"DepStub", new String[]{"java.lang.Object", "java.lang.Object.Object()void"});
	}

	@Test
	public void testDepSuperClassField(){
		runDep(PACKAGE+"DepSuperClassField", new String[]{"java.lang.Object", "java.lang.Object.Object()void"});
	}

	@Test
	public void testDepSuperClassMethod(){
		runDep(PACKAGE+"DepSuperClassMethod", new String[]{"java.lang.Object", "java.lang.Object.Object()void"});
	}

	@Test
	public void testDepSuperInterfaceField(){
		runDep(PACKAGE+"DepSuperInterfaceField", new String[]{"java.lang.Object", "java.lang.Object.Object()void"});
	}

	@Test
	public void testDepSuperInterfaceMethod(){
		runDep(PACKAGE+"DepSuperInterfaceMethod", new String[]{"java.lang.Object", "java.lang.Object.Object()void"});
	}

	@Test
	public void testArrayMethod(){
		runDep(PACKAGE+"DepArrayMethod", new String[]{"java.lang.Object", "[I.clone()Ljava.lang.Object;","java.lang.Object.Object()void"});
	}*/
}
