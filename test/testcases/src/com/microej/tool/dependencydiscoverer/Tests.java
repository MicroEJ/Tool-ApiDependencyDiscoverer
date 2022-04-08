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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.microej.tool.dependencydiscoverer.DependencyDiscovererError.ErrorMessageConstants;
import com.microej.tool.dependencydiscoverer.error.ErrorDescription;
import com.microej.tool.dependencydiscoverer.error.ErrorTaskContainer;


public class Tests {

	private static final String PACKAGE = "com.microej.tool.dependencydiscoverer.test.paths.";

	public String classpath;
	public String againstClasspath;

	@Before
	public void setup(){
		initClasspath();

		againstClasspath = System.getProperty("com.microej.tool.dependencydiscoverer.tests.againstClasspath");
		if(againstClasspath == null) {
			againstClasspath = System.getProperty("user.dir") + "/../testpaths/bin-againstclasspath";
		}
		Assert.assertTrue(new File(classpath).exists());
		Assert.assertTrue(new File(againstClasspath).exists());
	}

	/*
	 * Initialize the classpath, when tested with .class, the classpath is the path
	 * to the directory containing the class files. When tested with .jar, the
	 * classpath is composed of the paths to every jars in the directory
	 */
	private void initClasspath() {

		classpath = System.getProperty("com.microej.tool.dependencydiscoverer.tests.classpath");
		if (classpath == null) {
			classpath = System.getProperty("user.dir") + "/../testpaths/bin-classpath";
		}
		File classpathFile = new File(classpath);

		File[] subFiles = classpathFile.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});

		if (subFiles != null && subFiles.length > 0) {
			StringBuilder classpathBuilder = new StringBuilder();
			for (File subFile : subFiles) {
				classpathBuilder
				.append((classpathBuilder.length() == 0 ? "" : File.pathSeparator) + subFile.getAbsolutePath());
			}
			classpath = classpathBuilder.toString();
		}
	}

	@Test
	public void testMissingClasspath(){
		DependencyDiscovererTask dd = new DependencyDiscovererTask();
		ErrorTaskContainer error = new ErrorTaskContainer();
		dd.setErrorTaskContainer(error);
		ErrorDescription errorDesc = error.createErrorDescription();
		errorDesc.setKind(DependencyDiscovererError.ErrorMessageConstants.MISSING_CLASSPATH);
		dd.execute();
	}

	@Test
	public void testPathDoesNotExist(){
		testPathDoesNotExist("/aaa", "/aaa");
		testPathDoesNotExist(File.pathSeparator+"/aaa", "/aaa");
		testPathDoesNotExist(File.pathSeparator+"    "+File.pathSeparator+"/aaa"+File.pathSeparator, "/aaa");
		testPathDoesNotExist(File.pathSeparator+"    "+File.pathSeparator+"/aaa"+File.pathSeparator+"	"+File.pathSeparator, "/aaa");
	}

	protected void testPathDoesNotExist(String path, String pathThatDoesNotExist){
		DependencyDiscovererTask dd = new DependencyDiscovererTask();
		ErrorTaskContainer error = new ErrorTaskContainer();
		dd.setErrorTaskContainer(error);
		ErrorDescription errorDesc = error.createErrorDescription();
		errorDesc.setKind(DependencyDiscovererError.ErrorMessageConstants.PATH_DOES_NOT_EXIST);
		errorDesc.setMatchMessage(".*Path "+pathThatDoesNotExist+" does not exist.*");
		errorDesc.setWarning(true);

		dd.setClasspath(path);
		dd.execute();
	}

	public void testEntryPointDoesNotExist(String ep){
		DependencyDiscovererTask dd = new DependencyDiscovererTask();
		ErrorTaskContainer error = new ErrorTaskContainer();
		dd.setErrorTaskContainer(error);
		ErrorDescription errorDesc = error.createErrorDescription();
		errorDesc.setKind(ErrorMessageConstants.NO_MATCHING_ENTRYPOINT);

		dd.setClasspath(classpath);
		dd.setEntryPoints(ep);
		dd.execute();

	}

	@Test
	public void testEntryPointDoesNotExist(){
		testEntryPointDoesNotExist("a");
	}

	@Test
	public void testEntryPointDoesNotExistWildcard(){
		testEntryPointDoesNotExist("a*");
	}

	public void testEntryPointOK(String ep){
		runDep(ep);
	}

	public void runDep(String ep, String[] expected){
		String[] result = extractDependencies(runDep(ep));
		if (result.length != expected.length) {
			System.out.println();
			for (String e : result) {
				System.out.println(e);
			}
			System.out.println();
			for (String e : expected) {
				System.out.println(e);
			}
			System.out.println();
		}
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

	private String[] extractDependencies(String content) {
		ArrayList<String> linesVect = new ArrayList<String>();
		try{
			BufferedReader reader = new BufferedReader(new StringReader(content));
			String line;
			while((line=reader.readLine()) != null){
				linesVect.add(line);
			}
			String[] lines = new String[linesVect.size()];
			linesVect.toArray(lines);
			return lines;
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

	@Test
	public void testEntryPointOK(){
		testEntryPointOK(PACKAGE+"A");
	}


	@Test
	public void testEntryPointBOK(){
		testEntryPointOK(PACKAGE+"B");
	}

	@Test
	public void testEntryPointWildcardOK(){
		testEntryPointOK(PACKAGE+"*");
	}

	@Test
	public void testEntryPointWildcard2OK(){
		testEntryPointOK("*");
	}

	@Test
	public void testDep1(){
		runDep(PACKAGE+"Dep1", new String[]{"java/lang/Object", "java/lang/Object.<init>()V"});
	}

	@Test
	public void testDep2(){
		runDep(PACKAGE+"Dep2", new String[]{"java/lang/Object", "java/lang/Object.<init>()V", "java/lang/String", "java/lang/String.split(Ljava/lang/String;)[Ljava/lang/String;"});
	}

	@Test
	public void testDep3(){
		runDep(PACKAGE+"Dep3", new String[]{"java/lang/Object", "java/lang/Object.<init>()V", "java/lang/String", "java/lang/String.charAt(I)C"});
	}

	@Test
	public void testDepDotClass(){
		runDep(PACKAGE+"DepDotClass", new String[]{"java/lang/Object", "java/lang/Object.<init>()V", "java/util/Vector", "java/lang/Class"});
	}

	@Test
	public void testDepDotClass2() {
		runDep(PACKAGE + "DepDotClass2", new String[] { "java/lang/Object", "java/lang/Object.<init>()V",
				"java/util/Vector", "java/lang/Class" });
	}

	@Test
	public void testDepClone(){
		runDep(PACKAGE+"DepClone", new String[]{"java/lang/Object", "java/lang/Object.<init>()V", "java/lang/Object.clone()Ljava/lang/Object;"});
	}

	@Test
	public void testDepField1(){
		runDep(PACKAGE+"DepField1", new String[]{"java/lang/Object", "java/lang/Object.<init>()V", "java/io/PrintStream", "java/io/InputStream", "java/lang/System", "java/lang/System.in", "java/lang/System.out"});
	}

	@Test
	public void testDepArray(){
		//java/lang/Class, java/lang/NoClassDefFoundError, java/lang/Throwable, java/lang/Class.forName(Ljava/lang/String;)Ljava/lang/Class;, java/lang/NoClassDefFoundError.<init>(Ljava/lang/String;)V, java/lang/Object.<init>()V, java/lang/Throwable.getMessage()Ljava/lang/String;

		// Implementation dependent, make sure that at least
		// one of the tests work for any implementation
		try {
			// Workaround for ldc
			runDep(PACKAGE+"DepArray", new String[]{"java/lang/Object", "java/lang/Object.<init>()V", "java/lang/Class"});
		}
		catch (AssertionError err) {
			runDep(PACKAGE+"DepArray", new String[]{"java/lang/Object", "java/lang/Object.<init>()V", "java/lang/Class"});
		}
	}

	@Test
	public void testDepAbstract(){
		runDep(PACKAGE+"DepAbstract", new String[]{"java/lang/Object", "java/lang/Object.<init>()V"});
	}

	public static void main(String[] args) {
		Tests tests = new Tests();
		tests.setup();
		tests.testDepImplements();
	}

	@Test
	public void testDepImplementsAgainst() {
		runDep(PACKAGE + "DepImplementsAgainst", new String[] { "java/lang/Object", "java/lang/Object.<init>()V" });
	}

	@Test
	public void testDepImplementsKnown() {
		runDep(PACKAGE + "DepImplementsKnown", new String[] { "java/lang/Object", "java/lang/Object.<init>()V" });
	}

	@Test
	public void testDepImplements() {
		runDep(PACKAGE + "DepImplements", new String[] { "java/lang/Object", "java/lang/Object.<init>()V",
		"java/lang/Runnable" });
	}

	@Test
	public void testDepInvokeInterface() {
		runDep(PACKAGE + "DepInvokeInterface", new String[] { "java/lang/Object", "java/lang/Object.<init>()V",
				"java/util/List", "java/util/List.add(Ljava/lang/Object;)Z" });
	}

	@Test
	public void testDepOverride() {
		runDep(PACKAGE + "DepOverride", new String[] { "com/microej/tool/dependencydiscoverer/test/paths/Foo",
		"com/microej/tool/dependencydiscoverer/test/paths/Foo.<init>()V" });
	}

	@Category(IgnoredTest.class)
	@Test
	public void testDepOverride02() {
		// NOTE : this test has been added but has not yet been fixed so it currently
		// FAIL
		runDep(PACKAGE + "DepOverride02", new String[] { "java/lang/Object", "java/lang/Object.<init>()V",
				"java/lang/String", "java/lang/String.split(Ljava/lang/String;)[Ljava/lang/String;" });
	}

	@Category(IgnoredTest.class)
	@Test
	public void testDepOverride03() {
		// NOTE : this test has been added but has not yet been fixed so it currently
		// FAIL
		runDep(PACKAGE + "DepOverride03", new String[] { "java/lang/Object", "java/lang/Object.<init>()V",
				"java/lang/String", "java/lang/String.split(Ljava/lang/String;)[Ljava/lang/String;" });
	}

	@Category(IgnoredTest.class)
	@Test
	public void testDepOverride04() {
		// NOTE : this test has been added but has not yet been fixed so it currently
		// FAIL
		runDep(PACKAGE + "DepOverride04", new String[] { "java/lang/Object", "java/lang/Object.<init>()V",
				"java/lang/String", "java/lang/String.split(Ljava/lang/String;)[Ljava/lang/String;" });
	}

	@Category(IgnoredTest.class)
	@Test public void testDepOverride05() {
		// NOTE : this test has been added but has not yet been fixed so it currently
		// FAIL
		runDep(PACKAGE + "DepOverride05", new String[] { "java/lang/Object", "java/lang/Object.<init>()V",
				"java/lang/String", "java/lang/String.split(Ljava/lang/String;)[Ljava/lang/String;" });
	}

	@Category(IgnoredTest.class)
	@Test
	public void testDepOverride06() {
		// NOTE : May be useless as even if the call is done through an intermediary
		// method,this intermediary method might be analyzed later as the source one
		runDep(PACKAGE + "DepOverride06", new String[] { "java/lang/Object", "java/lang/Object.<init>()V",
				"java/lang/String", "java/lang/String.split(Ljava/lang/String;)[Ljava/lang/String;" });
	}

	@Test
	public void testDepStub() {
		runDep(PACKAGE + "DepStub", new String[] { "java/lang/Object", "java/lang/Object.<init>()V" });
	}

	@Test
	public void testDepSuperClassField() {
		runDep(PACKAGE + "DepSuperClassField", new String[] { "java/lang/Object", "java/lang/Object.<init>()V" });
	}

	@Test
	public void testDepSubclassWithoutOverride() {
		runDep(PACKAGE + "DepSubclassWithoutOverride", new String[] {
				"com/microej/tool/dependencydiscoverer/test/paths/Foo",
				"com/microej/tool/dependencydiscoverer/test/paths/Foo.<init>()V",
		"com/microej/tool/dependencydiscoverer/test/paths/Foo.bar()V" });
	}

	@Test
	public void testDepSubclassWithoutOverride2() {
		runDep(PACKAGE + "DepSubclassWithoutOverride2",
				new String[] { "com/microej/tool/dependencydiscoverer/test/paths/Foo",
						"com/microej/tool/dependencydiscoverer/test/paths/Foo.<init>()V",
		"com/microej/tool/dependencydiscoverer/test/paths/Foo.bar()V" });
	}

	@Test
	public void testDepSubclassWithoutOverride3() {
		runDep(PACKAGE + "DepSubclassWithoutOverride3",
				new String[] { "com/microej/tool/dependencydiscoverer/test/paths/Foo",
		"com/microej/tool/dependencydiscoverer/test/paths/Foo.<init>()V" });
	}

	@Test
	public void testDepSubclassWithoutOverride4() {
		runDep(PACKAGE + "DepSubclassWithoutOverride4",
				new String[] { "com/microej/tool/dependencydiscoverer/test/paths/Foo",
						"com/microej/tool/dependencydiscoverer/test/paths/Foo.<init>()V",
		"com/microej/tool/dependencydiscoverer/test/paths/Foo.bar2()V" });
	}

	@Test
	public void testDepSubclassWithoutOverride5() {
		runDep(PACKAGE + "DepSubclassWithoutOverride5",
				new String[] { "com/microej/tool/dependencydiscoverer/test/paths/Foo",
		"com/microej/tool/dependencydiscoverer/test/paths/Foo.bar()V" });
	}

	@Test
	public void testDepSubclassWithoutOverride6() {
		runDep(PACKAGE + "DepSubclassWithoutOverride6", new String[] {});
	}

	@Test
	public void testDepSuperClassMethod() {
		runDep(PACKAGE + "DepSuperClassMethod", new String[] { "java/lang/Object", "java/lang/Object.<init>()V" });
	}

	@Test
	public void testDepSuperInterfaceField() {
		runDep(PACKAGE + "DepSuperInterfaceField", new String[] { "java/lang/Object", "java/lang/Object.<init>()V" });
	}

	@Test
	public void testDepSuperInterfaceMethod() {
		runDep(PACKAGE + "DepSuperInterfaceMethod", new String[] { "java/lang/Object", "java/lang/Object.<init>()V" });
	}

	@Test
	public void testArrayMethod() {
		runDep(PACKAGE + "DepArrayMethod", new String[] { "java/lang/Object",
				"java/lang/Object.clone()Ljava/lang/Object;", "java/lang/Object.<init>()V" });
	}

	@Test
	public void testCatchedTypes() {
		runDep(PACKAGE + "CatchedType",
				new String[] { "java/lang/IllegalAccessError", "java/lang/Object", "java/lang/Object.<init>()V" });
	}

	@Test
	public void testNativeListing() {
		runDep(PACKAGE + "TestNativeListing",
				new String[] { "java/lang/Object", "java/lang/Object.<init>()V",
						"[NATIVE] com/microej/tool/dependencydiscoverer/test/paths/TestNativeListing.callANativeDirectly()V",
						"[NATIVE] com/microej/tool/dependencydiscoverer/test/paths/TestNativeListing.nativeEntryPoint()V",
		"[NATIVE] com/microej/tool/dependencydiscoverer/test/paths/TestNativeListing2.callANativeIndirectly()V" });
	}

	@Test
	public void testCatchAny() {
		runDep(PACKAGE + "TestCatchAny", new String[] { "java/lang/Object", "java/lang/Object.<init>()V" });
	}

	@Test
	public void testConstantDeclarations() {
		// test non-primitive and non-primitive tab constants
		runDep(PACKAGE + "DepConstant",
				new String[] { "java/lang/Class", "java/lang/Object", "java/lang/String", "java/util/List",
		"java/lang/Object.<init>()V" });
	}
}
