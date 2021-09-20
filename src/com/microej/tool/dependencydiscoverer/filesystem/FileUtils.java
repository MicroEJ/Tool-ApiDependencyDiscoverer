/*
 * Java
 *
 * Copyright 2016-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.filesystem;

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

import org.eclipse.jdt.annotation.Nullable;

/**
 * <p>
 * Contains File utilities such as {@link FileUtils#unZip(File, File)},
 * {@link FileUtils#mkDirs(File)},{@link FileUtils#mkDirs(String)} or
 * {@link FileUtils#mkDirs(File, String)}.
 */
public class FileUtils {
	private static final int BUFFER_SIZE = 4096;

	private FileUtils() {
		// empty private constructor to prevent instantiations
	}

	/**
	 * Extract a zip file to an output directory.
	 *
	 * @param zipfile     Input .zip file
	 * @param outdir      Output directory
	 * @return {@code true} if the zipfile as been unziped, {@code false} if an
	 *         error occured.
	 */
	public static boolean unZip(File zipfile, File outdir) {
		try (ZipInputStream in = new ZipInputStream(new FileInputStream(zipfile));) {
			ZipEntry entry;
			String fileName;
			String path;
			while ((entry = in.getNextEntry()) != null) {
				fileName = entry.getName();
				// Creates the architecture.
				if (entry.isDirectory()) {
					assert (fileName != null);
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
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Delete a folder and all its sub-folders even if non-empty.
	 *
	 * @param file the folder.
	 */
	public static void deleteFolder(File file) {
		try {
			FileVisitor<Path> visitor = new FileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(Path arg0, @Nullable IOException arg1)
						throws IOException {
					Files.delete(arg0);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes arg1)
						throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1)
						throws IOException {
					Files.delete(arg0);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path arg0, IOException arg1)
						throws IOException {
					return FileVisitResult.TERMINATE;
				}
			};
			Files.walkFileTree(file.toPath(), visitor);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void unzipFile(ZipInputStream in, File outdir, @Nullable String name) throws IOException {
		if (name != null) {
			byte[] buffer = new byte[BUFFER_SIZE];
			try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outdir, name)))) {
				int count = -1;
				while ((count = in.read(buffer)) != -1) {
					out.write(buffer, 0, count);
				}
			}
		}
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

	private static @Nullable String path(@Nullable String name) {
		if (name != null) {
			int s = name.lastIndexOf(File.separatorChar);
			return s == -1 ? null : name.substring(0, s);
		} else {
			return null;
		}
	}
}