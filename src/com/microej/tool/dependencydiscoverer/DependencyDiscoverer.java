/*
 * Java
 *
 * Copyright 2013-2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY. Use is subject to license terms.
 */
package com.microej.tool.dependencydiscoverer;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.is2t.dd.DependencyDiscovererBatch;



/**
 * Tool that download the latest MicroEJ repository and dumps all unresolved dependencies of jars found in
 * <code>classpath</code> directory.
 */
public class DependencyDiscoverer {

	private static final String REPOSITORY_URL = "http://developer.microej.com/" + DependencyDiscovererOptions.MICROEJ_VERSION + "/ivy/microej-"
			+ DependencyDiscovererOptions.MICROEJ_VERSION + "-latest.zip";
	private static final String REPO_NAME = "microej-" + DependencyDiscovererOptions.MICROEJ_VERSION + "-repository";
	private static final String REPO_FILE_EXTENTION = ".zip";

	/**
	 * EntryPoint for the Dependency Discoverer.
	 *
	 * @param args
	 *            not used.
	 */
	public static void main(String[] args) {
		String basedir = System.getProperty("user.dir");
		File classpathDir = FileUtils.mkDirs(basedir + File.separatorChar + "classpath");
		File againstClasspathDir = FileUtils.mkDirs(basedir + File.separatorChar + "againstClasspath");
		File repoDir = FileUtils.mkDirs(basedir + File.separatorChar + REPO_NAME);
		String outputFile = basedir+File.separatorChar+"result.txt";

		if (!DependencyDiscovererOptions.OFFLINE) {
			downloadRepository(repoDir);
		}

		System.out.println("Starting dependency discoverer.");
		DependencyDiscovererBatch.main(new String[]{
				"-classpath",
				getJars(classpathDir),
				"-againstClasspath",
				getJars(againstClasspathDir) + " " + getJars(repoDir),
				"-outputFile",
				outputFile,
				"*"
		});
		System.out.println("Dependency discoverer finished, please see output at " + outputFile);
	}

	private static void downloadRepository(File repoDir) {
		String repoFullName = REPO_NAME + REPO_FILE_EXTENTION;
		File repo = loadRepo(repoDir, repoFullName);
		if (repo != null) {
			File unzipDir = new File(repoDir, "unzip");
			if (unzipDir.exists()) {
				FileUtils.deleteFolder(unzipDir);
			}
			unzipDir.mkdirs();
			System.out.println("Unzip repository.");
			FileUtils.unZip(repo, unzipDir);
		}
	}

	private static File loadRepo(File repoDir, String repoName) {
		System.out.println("Downloading latest repository for MicroEJ " + DependencyDiscovererOptions.MICROEJ_VERSION + ".");
		InputStream in = null;
		File repo = null;
		try {
			repo = new File(repoDir, repoName);
			URL repositryUrl = new URL(REPOSITORY_URL);
			in = repositryUrl.openStream();
			Files.copy(in, repo.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			repo = null;
			System.err.println("Failed to download repository.");
			System.out.println("Cause:" + e.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return repo;
	}

	private static String getJars(File dir) {
		StringBuilder classpathVect = new StringBuilder();

		// Add jars
		File[] jars = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith(".jar");
			}
		});
		for (int i = jars.length; --i >= 0;) {
			classpathVect.append(jars[i].getAbsolutePath()).append(File.pathSeparatorChar);
		}

		// Add subfolders
		File[] subfolders = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File arg0) {
				return arg0.isDirectory();
			}

		});
		classpathVect.append(" ");
		for (File directory : subfolders) {
			classpathVect.append(getJars(directory));
		}

		return classpathVect.toString();
	}
}
