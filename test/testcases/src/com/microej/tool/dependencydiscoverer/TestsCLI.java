/*
 * Java
 *
 * Copyright 2021-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.security.Permission;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.microej.tool.dependencydiscoverer.filesystem.FileUtils;

import picocli.CommandLine;

public class TestsCLI {

	private static final char SEPARATOR = File.separatorChar;
	private static final String HOME_TO_TEST_CLASSES = "/testpaths/bin-classpath/com/microej/tool/dependencydiscoverer/test/paths";
	private static final String DEFAULT_RESULT = "result.txt";
	private static final boolean DD_LOG_SYSTEM_HIDDEN = false;

	private static final String REPOSITORY_URL = "--repository-url";
	private static final String REPOSITORY_DIR = "--repository-dir";
	private static final String CACHE_DIR = "--cache-dir";

	private ByteArrayOutputStream ddOut;

	private String ddHome;
	private String cacheDirPath;
	private File testRootDir;
	private String projectDirPath;

	private DependencyDiscovererCLI ddCLI;
	private File projectDir;
	private File cacheDir;

	@Rule
	public TestName name = new TestName();

	private int runTest(String[] arguments) {
		return new CommandLine(ddCLI).execute(arguments);
	}

	private int runTestRedirectedLogs(String[] arguments) {
		return runTestRedirectedLogs(arguments, DD_LOG_SYSTEM_HIDDEN);
	}

	private int runTestRedirectedLogs(String[] arguments, boolean localLog) {
		int exit = 0;
		if (localLog) {
			ddOut = new java.io.ByteArrayOutputStream(); // our out
			PrintStream sout = System.out; // system default out
			System.setOut(new java.io.PrintStream(ddOut));
			exit = runTest(arguments);
			System.setOut(new java.io.PrintStream(sout));

		} else {
			exit = runTest(arguments);
		}
		return exit;
	}

	private void performAssertion(String[] expected, String[] result) {
		System.out.println(" EXPECTED :");
		displayStringTab(expected);
		System.out.println(" RESULT :");
		displayStringTab(result);
		for (String e : expected) {
			System.out.println(e);
		}
		for (String r : result) {
			System.out.println(r);
		}
		Assert.assertEquals("Expected and result don't have the same length", expected.length , result.length);
		for (int i = 0; i < expected.length; i++) {
			Assert.assertEquals("Expected and result don't match", expected[i], result[i]);
		}
	}

	private void runTestOnOptions(String[] arguments, String[] expectedOptions) {
		runTestRedirectedLogs(arguments);
		printHeader("Assert on options", '-');
		String[] optionTab = optionsToStringTab(ddCLI.getOptions());
		performAssertion(expectedOptions, optionTab);
	}

	private void runTestOnOutput(String[] arguments, String expectedOutput) {

		runTestRedirectedLogs(arguments, true);
		printHeader("Assert on dd logs", '-');

		String[] optionTab = new String[] { ddOut.toString() };
		performAssertion(new String[] { expectedOutput }, optionTab);
	}

	// FROM HERE, TESTS

	@Before
	public void setup() {
		ddCLI = new DependencyDiscovererCLI();
		ddHome = new File(System.getProperty("user.dir")).getParent();
		testRootDir = newTmpDir();
		projectDirPath = testRootDir.getAbsolutePath() + SEPARATOR + "projectpath";
		projectDir = FileUtils.mkDirs(projectDirPath);
		cacheDir = newTmpDir();
		cacheDirPath = cacheDir.getAbsolutePath();
		printTestHeader();
	}

	private File newTmpDir() {
		File tmpFile;
		try {
			tmpFile = Files.createTempDirectory("ddCLITest").toFile();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		tmpFile.mkdirs();
		return tmpFile;
	}

	@Test
	public void testResultFile() {
		String resultPath = projectDirPath + SEPARATOR + "resultTest.txt";
		String[] arguments = new String[] { REPOSITORY_URL, "none", "-D", projectDirPath, "-r", resultPath };
		String[] optionsList = { "", projectDirPath + SEPARATOR + "classpath", resultPath, "text", "[*]" };
		runTestOnOptions(arguments, optionsList);
	}

	@Test
	public void testRepositoryURLNone() {
		String[] arguments = new String[] { REPOSITORY_URL, "none", "-D", projectDirPath };
		String[] optionsList = { "", projectDirPath + SEPARATOR + "classpath",
				projectDirPath + SEPARATOR + DEFAULT_RESULT, "text", "[*]" };
		runTestOnOptions(arguments, optionsList);
	}

	@Test
	public void testOptionsOutputType() {
		String[] arguments = new String[] { REPOSITORY_URL, "none", "-D", projectDirPath, "-t", "xml" };
		String[] optionsList = { "", projectDirPath + SEPARATOR + "classpath",
				projectDirPath + SEPARATOR + DEFAULT_RESULT, "xml", "[*]" };
		runTestOnOptions(arguments, optionsList);
	}

	@Test
	public void cleanCacheOption() {
		new File(cacheDirPath).mkdirs();
		String[] arguments = new String[] { REPOSITORY_URL, "none", "-D", projectDirPath, "--clean-cache",
				CACHE_DIR, cacheDirPath };

		runTestRedirectedLogs(arguments);
		performAssertion(new String[] { cacheDirPath }, new String[] { ddCLI.getCacheDir() });
	}

	@Test
	public void cleanCache() {
		File testFile = new File(cacheDir, "testFile.txt");
		File testDir = new File(cacheDir, "testDir");
		File testFileInDir = new File(testDir, "testFileInDir.txt");
		testDir.mkdir();
		try {
			Assert.assertTrue(testFile.createNewFile());
			Assert.assertTrue(testFileInDir.createNewFile());
		} catch (IOException e) {
			e.printStackTrace();
		}

		String[] arguments = new String[] { REPOSITORY_URL, "none", "-D", projectDirPath, "--clean-cache",
				CACHE_DIR, cacheDirPath };

		runTestRedirectedLogs(arguments);
		printHeader("Assert on cache content", '-');
		Assert.assertTrue(new File(cacheDirPath).listFiles().length == 0);
		System.out.println("The provided cache directory has been emptied");
	}

	@Test
	public void useProvidedRepoZip() {
		File testDir = new File(cacheDirPath, "testDir");
		File testJarInDir = new File(testDir, "testZip.jar");
		File testZipInDir = new File(testDir, "testZip.zip");
		File fileToZip = new File(ddHome + HOME_TO_TEST_CLASSES + "/B.class");

		Assert.assertTrue(testDir.mkdir());
		Assert.assertTrue(wrapInZip(fileToZip, testJarInDir));
		Assert.assertTrue(wrapInZip(testJarInDir, testZipInDir));

		String[] arguments = new String[] { "-D", projectDirPath, CACHE_DIR, testDir.getPath(),
				"--repository-file",
				testZipInDir.getPath() };

		runTestRedirectedLogs(arguments);
		performAssertion(new String[] { "testZip.jar" },
				new String[] { new File(ddCLI.getOptions().getAgainstClasspath()).getName() });
	}


	@Test
	public void useProvidedRepoDir() {
		File testRepoDir = new File(cacheDir, "testDir");
		File testJarInDir = new File(testRepoDir, "testZip.jar");
		File fileToZip = new File(ddHome + HOME_TO_TEST_CLASSES + "/B.class");
		testRepoDir.mkdir();

		Assert.assertTrue(wrapInZip(fileToZip, testJarInDir));

		String[] arguments = new String[] { "-D", projectDirPath, CACHE_DIR, cacheDirPath, REPOSITORY_DIR,
				testRepoDir.toString() };

		runTestRedirectedLogs(arguments);
		printHeader("Assert on againstClassPath (provided + repo)", '-');
		performAssertion(new String[] { testJarInDir.getPath() },
				new String[] { ddCLI.getOptions().getAgainstClasspath() });

	}

	/**
	 * Uses default classpath and providedClasspath from provided projectPath. Uses
	 * repoDir. A,B and C classes are used B extends C and contains an instance of
	 * A.
	 */
	@Test
	public void testDefaultProjectPathsProvidedRepoDir() {
		String resultPath = projectDirPath + SEPARATOR + "result.txt";
		File cacheDirFile = new File(cacheDirPath + "/testDir");
		File providedClasspathDir = new File(projectDirPath + "/providedClasspath");
		File classpathDir = new File(projectDirPath + "/classpath");
		File fileClasspathToJar = new File(ddHome + HOME_TO_TEST_CLASSES + "/B.class");
		File fileProvidedClasspathToJar = new File(ddHome + HOME_TO_TEST_CLASSES + "/A.class");
		File fileRepoClasspathToJar = new File(ddHome + HOME_TO_TEST_CLASSES + "/C.class");
		File classpathJarInDir = new File(classpathDir, "/B.jar");
		File providedClasspathJarInDir = new File(providedClasspathDir, "/A.jar");
		File repoClasspathJarInDir = new File(cacheDirFile, "/C.jar");

		providedClasspathDir.mkdirs();
		classpathDir.mkdir();
		cacheDirFile.mkdirs();

		Assert.assertTrue(wrapInZip(fileProvidedClasspathToJar, providedClasspathJarInDir));
		Assert.assertTrue(wrapInZip(fileClasspathToJar, classpathJarInDir));
		Assert.assertTrue(wrapInZip(fileRepoClasspathToJar, repoClasspathJarInDir));

		String[] arguments = new String[] { "-D", projectDirPath, REPOSITORY_DIR, cacheDirFile.toString() };
		String[] optionsList = new String[] { providedClasspathJarInDir + File.pathSeparator + repoClasspathJarInDir,
				classpathDir.toPath() + File.pathSeparator + classpathJarInDir.toPath().toString(), resultPath, "text",
		"[*]" };
		runTestOnOptions(arguments, optionsList);
	}

	/**
	 * Uses default provided projectPath , classpath , providedClasspath and
	 * repoDir. A,B and C classes are used in the analysis B extends C and contains
	 * an instance of A.
	 */
	@Test
	public void testDefaultProjectPathsEveryDirPaths() {
		String resultPath = projectDirPath + SEPARATOR + "resultProvided.txt";
		File providedClasspathDir = new File(projectDir, "nonDefaultProvidedClasspath");
		File classpathDir = new File(projectDir, "nonDefaultClasspath");
		File fileClasspathToJar = new File(ddHome + HOME_TO_TEST_CLASSES + "/B.class");
		File fileProvidedClasspathToJar = new File(ddHome + HOME_TO_TEST_CLASSES + "/A.class");
		File fileRepoClasspathToJar = new File(ddHome + HOME_TO_TEST_CLASSES + "/C.class");
		File classpathJarInDir = new File(classpathDir, "B.jar");
		File providedClasspathJarInDir = new File(providedClasspathDir, "A.jar");
		File repoClasspathJarInDir = new File(cacheDir, "C.jar");

		providedClasspathDir.mkdirs();
		classpathDir.mkdir();

		Assert.assertTrue(wrapInZip(fileProvidedClasspathToJar, providedClasspathJarInDir));
		Assert.assertTrue(wrapInZip(fileClasspathToJar, classpathJarInDir));
		Assert.assertTrue(wrapInZip(fileRepoClasspathToJar, repoClasspathJarInDir));

		String[] arguments = new String[] { "-D", projectDirPath, REPOSITORY_DIR,
				cacheDir.getAbsolutePath(), "-c",
				classpathDir.getPath(), "-p", providedClasspathDir.getPath(), "-r", resultPath };
		String[] optionsList = new String[] { providedClasspathJarInDir + File.pathSeparator + repoClasspathJarInDir,
				classpathDir.toPath() + File.pathSeparator + classpathJarInDir.toPath().toString(), resultPath, "text",
		"[*]" };
		runTestOnOptions(arguments, optionsList);
	}

	/**
	 * Launch the dependency discoverer with a wrong result path (the directory
	 * hierarchy does't exist). The result have to be printed on the logger output
	 * by default {@link System#out}.
	 */
	@Test
	public void testResultFallback() {
		String resultPath = projectDirPath + SEPARATOR + "thisDirDoesntExist" + SEPARATOR + "resultProvided.txt";
		File classpathDir = new File(projectDir, "nonDefaultClasspath");
		File fileClasspathToJar = new File(ddHome + HOME_TO_TEST_CLASSES + "/C.class");
		File classpathJarInDir = new File(classpathDir, "C.jar");

		classpathDir.mkdirs();

		Assert.assertTrue(wrapInZip(fileClasspathToJar, classpathJarInDir));

		System.out.println("jar exist " + classpathJarInDir.exists());

		String[] arguments = new String[] { REPOSITORY_URL, "none", "-D", projectDirPath, "-c",
				classpathDir.getPath(), "-r", resultPath };
		String expected = "INFO: Initializing Dependency Discoverer options...\r\n"
				+ "INFO: Starting Dependency Discoverer analysis...\r\n"
				+ "com/microej/tool/dependencydiscoverer/test/paths/I\r\n" + "java/lang/Object\r\n"
				+ "java/lang/Object.<init>()V\r\n";
		runTestOnOutput(arguments, expected);
	}

	@Test
	public void testConvertSpecialCharacters() {
		printHeader("Single method result", '-');
		String url = "https://docs.microej.com/en/latest.../Platform\\DeveloperGuide/external........ResourceLoader.html#dependencies.";
		String expectedResult = "https%58%47%47docs.microej.com%47en%47latest!%47Platform%92DeveloperGuide%47external!ResourceLoader.html#dependencies!";
		String res = ddCLI.convertForbiddenCharacters(url);
		performAssertion(new String[] { expectedResult }, new String[] { res });
	}

	@Test
	public void testExitOK() {
		testExit(new String[] { REPOSITORY_URL, "none", "-c",
				new File(ddHome + "/testpaths/bin-classpath/").getAbsolutePath(), "-r",
				cacheDirPath + SEPARATOR + "result.txt" }, 0);
	}

	@Test
	public void testExitKO() {
		testExit(new String[] {}, 1);
	}

	private void testExit(String[] args, int expectedExitCode) {
		printHeader("Assert on exit status", '-');
		TestSecurityManager t = new TestSecurityManager();
		System.setSecurityManager(t);
		try {
			DependencyDiscovererCLI.main(args);
			Assert.fail();
		} catch (SecurityException e) {
			System.setSecurityManager(null);
			org.junit.Assert.assertEquals(expectedExitCode, t.exitStatus);
		}
	}

	private static class TestSecurityManager extends SecurityManager {

		int exitStatus;

		@Override
		public void checkExit(int status) {
			this.exitStatus = status;
			throw new SecurityException();
		}

		@Override
		public void checkPermission(Permission perm) {

		}
	}

	@After
	public void cleanup() {
		StringBuilder bar = new StringBuilder();
		String cleanup;

		FileUtils.deleteFolder(testRootDir);
		FileUtils.deleteFolder(cacheDir);
		if (!projectDir.exists() && !cacheDir.exists()) {
			cleanup = " Cleanup done ";
		} else {
			cleanup = " Cleanup not done : " + "error";
		}
		int i = -1;
		while (i++ < (cleanup.length() + 6)) {
			bar.append("-");
		}
		printHeader(cleanup, '-');
	}

	// FROM HERE, HELPER METHODS

	private void displayStringTab(String[] tab) {
		StringBuilder stringTab = new StringBuilder("[");
		for (int i = 0; i < tab.length; i++) {
			stringTab.append("\"" + (tab[i].length() < 200 ? tab[i] : tab[i].substring(0, 199) + "..."));
			stringTab.append("\"" + (i + 1 < tab.length ? "," : "]"));
		}
		System.out.println(stringTab);
	}

	private boolean wrapInZip(File fileToZip, File targetFile) {

		try {
			Assert.assertTrue(targetFile.createNewFile());
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

	private String[] optionsToStringTab(DependencyDiscovererOptions options) {
		String[] optionsList = { "", "", "", "", "" };
		optionsList[0] = options.getAgainstClasspath();
		optionsList[1] = options.getClasspath();
		optionsList[2] = options.getOutputFile();
		optionsList[3] = options.getOutputType();
		optionsList[4] = options.getEntryPoints().toString();
		return optionsList;
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
