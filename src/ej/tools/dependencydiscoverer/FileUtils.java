/*
 * Java
 *
 * Copyright 2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY. Use is subject to license terms.
 */
package ej.tools.dependencydiscoverer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {
	private static final int BUFFER_SIZE = 4096;

	/**
	 * Extract a zip file to an output directory.
	 *
	 * @param zipfile
	 *            Input .zip file
	 * @param outdir
	 *            Output directory
	 */
	public static void unZip(File zipfile, File outdir) {
		ZipInputStream in = null;
		try {
			ZipEntry entry;
			in = new ZipInputStream(new FileInputStream(zipfile));
			String fileName;
			String path;
			while ((entry = in.getNextEntry()) != null) {
				fileName = entry.getName();
				// Creates the architecture.
				if (entry.isDirectory()) {
					mkDirs(outdir, fileName);
					continue;
				}

				// Get the path to the file.
				path = path(fileName);
				if (path != null) {
					// Make sure its parent folders exists.
					mkDirs(outdir,path);
				}

				unzipFile(in, outdir, fileName);
			}
		} catch (IOException e) {
			System.err.println(zipfile.getAbsolutePath() + " could not be unzip. ");
			System.out.println("Cause:" + e.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Delete a folder and all its subfolders.
	 *
	 * @param file
	 *            the folder.
	 */
	public static void deleteFolder(File file) {
		try {
			Files.walkFileTree(file.toPath(), new FileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(Path arg0, IOException arg1) throws IOException {

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes arg1) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1) throws IOException {
					Files.delete(arg0);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path arg0, IOException arg1) throws IOException {
					return FileVisitResult.TERMINATE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void unzipFile(ZipInputStream in, File outdir, String name) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outdir, name)));
		int count = -1;
		while ((count = in.read(buffer)) != -1) {
			out.write(buffer, 0, count);
		}
		out.close();
	}

	/**
	 * Open a directory, creates the directory and parents if necessary.
	 *
	 * @param path
	 *            The path to the directory.
	 * @return The directory.
	 */
	public static File mkDirs(String path) {
		return mkDirs(new File(path));
	}

	/**
	 * Open a directory, creates the directory and parents if necessary.
	 *
	 * @param path
	 *            The path to the directory.
	 * @param outdir
	 *            The root of the directory.
	 * @return The directory.
	 */
	public static File mkDirs(File outdir, String path) {
		return mkDirs(new File(outdir, path));
	}

	/**
	 * Open a directory, creates the directory and parents if necessary.
	 *
	 * @param dir
	 *            The directory.
	 * @return The directory.
	 */
	public static File mkDirs(File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	private static String path(String name) {
		int s = name.lastIndexOf(File.separatorChar);
		return s == -1 ? null : name.substring(0, s);
	}
}