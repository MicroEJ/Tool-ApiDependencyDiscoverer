/*
 * Java
 *
 * Copyright 2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.objectweb.asm.tree.ClassNode;

import com.microej.tool.dependencydiscoverer.classfinder.ExactClassfileFilter;
import com.microej.tool.dependencydiscoverer.classfinder.IJavaClassfileFilter;
import com.microej.tool.dependencydiscoverer.classfinder.JavaClassFinder;
import com.microej.tool.dependencydiscoverer.classfinder.WildCardClassfileFilter;
import com.microej.tool.dependencydiscoverer.error.ErrorHandler;
import com.microej.tool.dependencydiscoverer.filesystem.FileUtils;

public class TestsClassfinder {

	private static final String PACKAGE_NAME = "com/microej/tool/dependencydiscoverer/test/paths/";
	private static final String HOME_TO_TEST_CLASSES = "/testPaths/bin-classpath/com/microej/tool/dependencydiscoverer/test/paths";

	private String ddHome = "";
	private String testDirPath;

	private ErrorHandler errorHandler;

	@Rule
	public TestName name = new TestName();

	private void performAssertion(Map<String, ClassNode> allClassNodes, String[] expectedClasses) {

		ArrayList<String> resultClasses = new ArrayList<String>();

		for (int i = 0; i < expectedClasses.length; i++) {
			expectedClasses[i] = PACKAGE_NAME + expectedClasses[i];
		}

		System.out.println(" EXPECTED :");
		displayString(expectedClasses);

		for (Entry<String, ClassNode> cn : allClassNodes.entrySet()) {
			if(cn != null) {
				resultClasses.add(cn.getValue().name);
			}
		}
		System.out.println(" RESULT :");
		displayString(resultClasses);
		assertOnArrays(expectedClasses, resultClasses);
	}

	private void assertOnArrays(String[] expectedClasses, ArrayList<String> resultClasses) {
		assert (expectedClasses.length == resultClasses.size());
		for (int i = 0; i < resultClasses.size(); i++) {
			assert (expectedClasses[i]).equals(resultClasses.get(i));
		}
	}

	private void runTestEntryPoint(String entryPoint, File[] classpath, String[] expectedClasses) {
		printTestHeader();
		printHeader("Test on entryPoint", '-');
		Map<String, ClassNode> allClassNodes = new HashMap<String, ClassNode>();

		try {
			JavaClassFinder.find(allClassNodes, classpath, getFilter(entryPoint), errorHandler);
		} catch (IOException e) {

		}
		performAssertion(allClassNodes, expectedClasses);
	}


	private void runTestEntryPoints(HashMap<String, String[]> entriesAndExpected, File[] classpath) {
		printTestHeader();
		printHeader("Test on entryPoints", '-');
		Map<String, ClassNode> allClassNodes;
		for (Entry<String, String[]> entry : entriesAndExpected.entrySet()) {
			allClassNodes = new HashMap<String, ClassNode>();
			try {
				JavaClassFinder.find(allClassNodes, classpath, getFilter(entry.getKey()), errorHandler);
			} catch (IOException e) {
				throw new AssertionError(e);
			}
			performAssertion(allClassNodes, entry.getValue());
		}
	}

	// FROM HERE, TESTS

	@Before
	public void setup() {
		errorHandler = new ErrorHandler();
		ddHome = new File(System.getProperty("user.dir")).getParent();

		File testDir = newTmpDir();
		testDirPath = testDir.getAbsolutePath();

		File providedClasspathDir = new File(testDir, "classpath1");
		File classpathDir = new File(testDir, "classpath2");
		File fileClasspathToJar = new File(ddHome + HOME_TO_TEST_CLASSES + "/B.class");
		File cToJar = new File(ddHome + HOME_TO_TEST_CLASSES + "/C.class");
		File fileProvidedClasspathToJar = new File(ddHome + HOME_TO_TEST_CLASSES + "/A.class");
		File classpathJarInDir = new File(classpathDir, "B.jar");
		File cJarInDir = new File(classpathDir, "C.jar");
		File providedClasspathJarInDir = new File(providedClasspathDir, "A.jar");


		providedClasspathDir.mkdirs();
		classpathDir.mkdir();

		assert (wrapInZip(fileProvidedClasspathToJar, providedClasspathJarInDir));
		assert (wrapInZip(fileClasspathToJar, classpathJarInDir));
		assert (wrapInZip(cToJar, cJarInDir));
	}

	@Test
	public void testWildFilter() {
		File classpath1 = new File(testDirPath + "/classpath1/A.jar");

		String[] expectedClasses = new String[] { "A" };

		File[] classpaths = new File[] { classpath1 };
		runTestEntryPoint("*", classpaths, expectedClasses);
	}

	@Test
	public void testWildFilterMultipleClasspaths() {
		File classpath1 = new File(testDirPath + "/classpath1/A.jar");
		File classpath2 = new File(testDirPath + "/classpath2/B.jar");

		HashMap<String, String[]> entriesAndExpected = new HashMap<String, String[]>();

		String[] expectedClasses = new String[] { "A", "B" };
		entriesAndExpected.put("*", expectedClasses);

		File[] classpaths = new File[] { classpath1, classpath2 };
		runTestEntryPoints(entriesAndExpected, classpaths);
	}

	@Test
	public void testExactFilter() {
		File classpath1 = new File(testDirPath + "/classpath1/A.jar");

		HashMap<String, String[]> entriesAndExpected = new HashMap<String, String[]>();

		String[] expectedClasses = new String[] { "A" };

		entriesAndExpected.put("A", expectedClasses);
		File[] classpaths = new File[] { classpath1 };

		runTestEntryPoints(entriesAndExpected, classpaths);
	}

	@Test
	public void testExactFiltersMultipleClasspaths() {
		File classpath1 = new File(testDirPath + "/classpath1/A.jar");
		File classpath2 = new File(testDirPath + "/classpath2/B.jar");
		File classpath3 = new File(testDirPath + "/classpath2/C.jar");

		HashMap<String, String[]> entriesAndExpected = new HashMap<String, String[]>();
		entriesAndExpected.put("B", new String[] { "B" });
		entriesAndExpected.put("C", new String[] { "C" });
		entriesAndExpected.put("A", new String[] { "A" });
		File[] classpaths = new File[] { classpath1, classpath2, classpath3 };

		runTestEntryPoints(entriesAndExpected, classpaths);
	}

	/**
	 * Performed directly into the testpath were are contained compiled .class test
	 * files
	 */
	@Test
	public void testComplexeWildFilterDirClasspath() {
		File classpath1 = new File(ddHome + HOME_TO_TEST_CLASSES);

		HashMap<String, String[]> entriesAndExpected = new HashMap<String, String[]>();

		entriesAndExpected.put("A*", new String[] { "AbstractA", "A" });
		File[] classpaths = new File[] { classpath1 };

		runTestEntryPoints(entriesAndExpected, classpaths);
	}

	@After
	public void cleanup() {

		StringBuilder bar = new StringBuilder();
		String cleanup;
		FileUtils.deleteFolder(new File(testDirPath));
		if (new File(testDirPath).exists()) {
			cleanup = " Cleanup done ";
		} else {
			cleanup = " Cleanup not done : error";
		}
		int i = -1;
		while (i++ < (cleanup.length() + 6)) {
			bar.append("-");
		}
		printHeader(cleanup, '-');
	}

	// FROM HERE, HELPER METHODS

	private File newTmpDir() {
		File tmpFile;
		try {
			tmpFile = Files.createTempDirectory("ddClassFinderTests").toFile();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		tmpFile.mkdirs();
		return tmpFile;
	}

	private IJavaClassfileFilter getFilter(String entryPoint) {
		if (entryPoint.endsWith("*")) {
			return new WildCardClassfileFilter(new String[] { entryPoint });
		} else {
			return new ExactClassfileFilter(new String[] { entryPoint });
		}
	}

	private void displayString(ArrayList<String> tab) {
		if (tab.size() > 0) {
			StringBuilder stringTab = new StringBuilder();
			stringTab.append("[");

			for (int i = 0; i < tab.size(); i++) {
				if (tab.get(i) == null) {
					stringTab.append("\"null");
				} else {
					stringTab
					.append("\"" + (tab.get(i).length() < 200 ? tab.get(i) : tab.get(i).substring(0, 199) + "..."));
				}
				stringTab.append("\"" + (i + 1 < tab.size() ? "," : "]"));
			}
			System.out.println(stringTab);
		} else {
			System.out.println("empty...");
		}
	}

	private void displayString(String[] tab) {
		StringBuilder stringTab = new StringBuilder("[");
		for (int i = 0; i < tab.length; i++) {
			if (tab[i] == null) {
				stringTab.append("\"null");
			} else {
				stringTab.append("\"" + (tab[i].length() < 200 ? tab[i] : tab[i].substring(0, 199) + "..."));
			}
			stringTab.append("\"" + (i + 1 < tab.length ? "," : "]"));
		}
		System.out.println(stringTab);
	}

	private boolean wrapInZip(File fileToZip, File targetFile) {

		try {
			assert (targetFile.createNewFile());
			FileInputStream fis = new FileInputStream(fileToZip);
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targetFile));
			ZipEntry e = new ZipEntry(fileToZip.getName());
			out.putNextEntry(e);

			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				out.write(bytes, 0, length);
			}
			out.closeEntry();
			out.close();
			fis.close();

		} catch (IOException e) {
			return false;
		}
		return true;
	}


	private void printTestHeader() {
		printHeader("Launching " + name.getMethodName(), '=');
	}

	private void printHeader(String testName, char lineSeparator) {
		StringBuilder bar = new StringBuilder();
		int i = -1;
		while (i++ < (testName.length() + 6)) {
			bar.append(lineSeparator);
		}
		System.out.println(bar);
		System.out.println("   " + testName + "   ");
		System.out.println(bar);
	}
}
