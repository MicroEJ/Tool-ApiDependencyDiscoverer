/*
 * Java
 *
 * Copyright 2013-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.eclipse.jdt.annotation.Nullable;

import com.microej.tool.dependencydiscoverer.analysis.DependencyDiscoverer;
import com.microej.tool.dependencydiscoverer.filesystem.FileUtils;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Ansi.Style;
import picocli.CommandLine.Help.ColorScheme;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;


/**
 * Tool that downloads the latest MicroEJ repository and dumps all unresolved
 * dependencies of jars found in <code>classpath</code> directory .
 */
@Command(name = "microejdd", mixinStandardHelpOptions = true, versionProvider = DependencyDiscovererCLI.PropertiesVersionProvider.class, description = "\nMicroEJ Dependency Discoverer is a tool that lists unresolved dependencies (types, methods and fields) of a set of Java ARchives (JAR)  and .class files. By default, the tool is configured to download the latest MicroEJ Central Repository and then lists only the missing dependencies.\n\nOptions: \n",
footer = { "", "Project Path Structure (defaults to current directory)\n\n" + "  |\n" + "  +--classpath/",
				"  |  |", "  |  |--your JAR files to analyze.", "  |", "  +--providedClasspath/", "  |  |",
		"  |  |--your manual JAR files dependencies.", "  |",
"  +--result.txt (the output list of unresolved dependencies)\n" })
public class DependencyDiscovererCLI implements Callable<Integer> {


	private static final char[] SPECIAL_CHARACTERS = { '<', '>', ':', '/', '\\', '|', '?', '*' };
	private static final String REPOSITORY_URL = "https://repository.microej.com/microej-"
			+ DependencyDiscovererDefaultOptions.MICROEJ_VERSION + "-latest.zip";
	private static final String REPO_NAME = "microej-" + DependencyDiscovererDefaultOptions.MICROEJ_VERSION + "-repository";

	@Option(names = { "-c",
			"--classpath-dir" }, description = "Directory containing the JAR files and .class files to analyze (defaults to `[PROJECT_PATH]/"
			+ DependencyDiscovererDefaultOptions.CLASS_PATH + "/`).")
	private String classpathDir = "";

	@Option(names = { "--cache-dir" }, description = "Cache directory (defaults to `"
			+ DependencyDiscovererDefaultOptions.CACHE_DIR + "`).")
	private String cacheDir = DependencyDiscovererDefaultOptions.CACHE_DIR;

	@Option(names = { "--clean-cache" }, description = "Delete Dependency Discoverer cached files (defaults to "
			+ DependencyDiscovererDefaultOptions.CLEAN_CACHE + ")")
	private boolean cleanCache = DependencyDiscovererDefaultOptions.CLEAN_CACHE;

	@Option(names = { "-t",
	"--output-format" }, description = "Listing output format: `json`,`text`,`xml` (defaults to `text`).")
	private String type = DependencyDiscovererDefaultOptions.OUTPUT_TYPE;

	@Option(names = { "-D",
	"--project-dir" }, description = "Project base directory (defaults to current directory).")
	private String projectDir = DependencyDiscovererDefaultOptions.PROJECT_PATH;

	@Option(names = { "-p",
	"--provided-classpath-dir" }, description = "Directory containing the provided JAR files (defaults to `[PROJECT_PATH]/"
			+ DependencyDiscovererDefaultOptions.PROVIDED_CLASSPATH + "/`.")
	@Nullable
	private String providedClasspathDir;

	@Option(names = { "-r", "--result-file" }, description = "Path to the result file (defaults to `[PROJECT_PATH]/"
			+ DependencyDiscovererDefaultOptions.OUTPUT_RESULT_FILE + "`).")
	private String resultFile = DependencyDiscovererDefaultOptions.OUTPUT_RESULT_FILE;

	@ArgGroup(exclusive = true, multiplicity = "0..1")
	private RepositoryOptions repositoryOptions = new RepositoryOptions();

	static class RepositoryOptions {
		@Option(names = { "-u",
		"--repository-url" }, required = true, description = "Module repository URL to add to provided classpath or `none` to run with no provided repository (defaults to `"
				+ REPOSITORY_URL + "`).")
		@Nullable
		String repositoryUrl;

		@Option(names = { "-d",
		"--repository-dir" }, required = true, description = "Module repository directory to add to provided classpath.")
		@Nullable
		String repositoryDir;

		@Option(names = { "-f",
		"--repository-file" }, required = true, description = "Module repository ZIP file to add to provided classpath.")
		@Nullable
		String repositoryFile;

	}

	@Option(names = { "-v", "--verbose" }, description = "Activate verbose information.")
	private boolean verbose = DependencyDiscovererDefaultOptions.VERBOSE;

	// see also CommandLine.Help.defaultColorScheme()
	@Nullable
	private static ColorScheme colorScheme;

	@Nullable
	private String tempPath;

	// From here global variables used by the test suite get the results back
	// may also be used to create a logger that writes to a file

	@Nullable
	private DependencyDiscovererOptions options;

	/**
	 * Main method that calls the picocli library to create a CLI interface.<br/>
	 * Contains the color scheme informations of the CLI's interface.
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		// Set color and style scheme of CLI outputs
		colorScheme = new ColorScheme.Builder().commands(Style.bold)
				.optionParams(Style.italic).stackTraces(Style.italic).build();

		// Execute the program
		int exitCode = new CommandLine(new DependencyDiscovererCLI())
				.setColorScheme(colorScheme).execute(args);

		// Return the exit code
		System.exit(exitCode);
	}

	@Override
	public Integer call() {

		// Dependency discoverer initializations
		log(Level.INFO, "Initializing Dependency Discoverer options...");

		String initbasedir = System.getProperty("user.dir");
		assert (initbasedir != null);

		// Cache declared by user
		cacheDir = getAbsoluteFile(cacheDir);

		tempPath = System.getProperty("java.io.tmpdir");
		assert (tempPath != null);

		String basedir = initializeFilePath(projectDir, DependencyDiscovererDefaultOptions.PROJECT_PATH, initbasedir,
				"").toString();
		assert (basedir != null);

		File classpathDirFile = initializePath(classpathDir, DependencyDiscovererDefaultOptions.CLASS_PATH, basedir);

		File providedClasspathDirFile = getProvidedClasspath(basedir);

		if (cleanCache) {
			cleanCache();
		}

		File repoDir = getRepository(basedir);

		String outputFile;
		if (resultFile.equals(DependencyDiscovererDefaultOptions.OUTPUT_RESULT_FILE)) {
			outputFile = (basedir + File.separatorChar + resultFile);
		} else {
			outputFile = resultFile;
		}

		ArrayList<String> classpathVect = new ArrayList<>();
		// add the classpath dir to analyze .class files
		classpathVect.add(classpathDirFile.getAbsolutePath());
		// add JARs files found in the classpath dir
		try {
			getJars(classpathVect, classpathDirFile);
		}catch(NullPointerException error) {
			printPathError(classpathDirFile);
		}

		ArrayList<String> providedClasspathVect = new ArrayList<>();
		if(providedClasspathDirFile!=null) {
			try {
				getJars(providedClasspathVect, providedClasspathDirFile);
			} catch (NullPointerException error) {
				printPathError(providedClasspathDirFile);
			}
		} else {
			log(Level.FINE, "Couln't load providedClasspath");
		}

		try {
			getJars(providedClasspathVect, repoDir);
		} catch (NullPointerException error) {
			printPathError(repoDir);
		}

		//DependencyDiscovererOptions
		if (options == null) {
			options = new DependencyDiscovererOptions();
		}

		assert (options != null);
		options.setOptions(String.join(File.pathSeparator, classpathVect),
				String.join(File.pathSeparator, providedClasspathVect),
				outputFile,
				type, "*");


		// Dependency discoverer

		log(Level.INFO, "Starting Dependency Discoverer analysis...");
		DependencyDiscoverer instanceDD = new DependencyDiscoverer();

		assert (options != null);
		instanceDD.setOptions(options);
		instanceDD.run();
		if (instanceDD.getErrorHandler().hasError()) {
			instanceDD.getErrorHandler().outputError();
			return 1;
		}else {
			log(Level.INFO, "Dependency Discoverer finished. Please see output at: " + outputFile + " .");
		}
		return 0;
	}

	/**
	 * Gets the absolute filename from a relative filename with support of the '~'
	 * replaced by user home.
	 *
	 * @param relativeFile the file to convert to an absolute filename
	 * @return an absolute filename
	 */
	private String getAbsoluteFile(String relativeFile) {
		String userHome = System.getProperty("user.home");
		assert (userHome != null);
		if (relativeFile.startsWith("~/")) {
			return userHome + relativeFile.substring(1);
		}
		else {
			return relativeFile;
		}
	}

	/**
	 * Delete the cache directory content, if the directory doesn't exist it's
	 * created.
	 */
	private void cleanCache() {
		File cacheFileDir = new File(cacheDir);
		log(Level.FINE, "Cleaning cache...");
		if (cacheFileDir.exists()) {
			FileUtils.deleteFolder(cacheFileDir);
		}
		FileUtils.mkDirs(cacheFileDir);
		log(Level.FINE, "Cache cleaned.");
	}

	/**
	 * @param basedir     path to the directory where the analyze is done
	 * @param errorStream
	 * @param infoStream
	 * @return the path to providedClasspath
	 */
	@Nullable
	private File getProvidedClasspath(String basedir) {

		String path = (providedClasspathDir == null ? "" : providedClasspathDir);
		assert (path != null);
		File providedClasspathDirFile = null;

		if (path.endsWith(".zip")) {
			log(Level.FINE, "Loading 'providedClasspath' from zip file" + path + " ...");
			providedClasspathDirFile = unzipFromPath(path);
			if (providedClasspathDirFile == null) {
				log(Level.FINE, path + " could not be unzip. ");
				return null;
			}
		} else {
			providedClasspathDirFile = initializePath(path, DependencyDiscovererDefaultOptions.PROVIDED_CLASSPATH,
					basedir);
		}


		log(Level.FINE, "'providedClasspath' directory located at: " + providedClasspathDirFile.toString());

		return providedClasspathDirFile;
	}


	private File getRepository(String baseDir) {

		// Because of picocli exclusion at most one of these options is not null
		// (they can all be null)
		String pathUrl = repositoryOptions.repositoryUrl;
		String pathFile = repositoryOptions.repositoryFile;
		String pathDir = repositoryOptions.repositoryDir;

		File repoFile = null;
		File repoDir = null;

		if (isEmptyOptionOrNotSet(pathUrl) && isEmptyOptionOrNotSet(pathFile) && isEmptyOptionOrNotSet(pathDir)) {
			// By default, use the latest online repository
			pathUrl = REPOSITORY_URL;
		}


		if (!isEmptyOptionOrNotSet(pathUrl)) {
			assert pathUrl != null;
			if (pathUrl.equals("none")) {
				// If the keyword none is used, sets the repoDir to a File pointing to nothing
				repoDir = new File("");
			} else {
				// Download from URL handling
				log(Level.FINE, "Loading repository from URL " + pathUrl + " ...");
				repoFile = downloadRepository(pathUrl);
			}
		}
		else if (!isEmptyOptionOrNotSet(pathFile)) {
			// zip loading handling
			assert pathFile != null;
			// a zip file path is given or a zip has been downloaded
			repoFile = new File(pathFile);
		}

		// Unzip handling
		if (repoFile != null) {
			log(Level.FINE, "Loading repository from zip file " + repoFile + " ...");
			repoDir = unzipFromFile(repoFile);
		}

		// Load from directory handling
		// if pathDir is null then the 2 other options are null
		if (pathDir != null) {
			log(Level.FINE, "Loading repository from directory...");
			repoDir = new File(pathDir);
		}

		// Fallback to default dir
		if (repoDir == null) {
			log(Level.FINE, "Loading repository from default local zip file");
			repoDir = new File(baseDir + File.separatorChar + REPO_NAME);
			if (!repoDir.exists()) {
				FileUtils.mkDirs(repoDir);
			}
		}
		log(Level.FINE, "Repository directory located at: " + repoDir.toString());
		return repoDir;
	}

	private boolean isEmptyOptionOrNotSet(@Nullable String opt) {
		return opt == null || opt.isEmpty();
	}

	@Nullable
	private File unzipFromPath(String zipFilePath) {
		File zipFile = new File(zipFilePath);
		return unzipFromFile(zipFile);
	}

	@Nullable
	private File unzipFromFile(File zipFile) {
		if (!zipFile.exists()) {
			log(Level.FINE, "File doesn't exist: " + zipFile.getPath());
			return null;
		}
		File unzipDir = new File(tempPath + File.separatorChar + "dd-" + sha1sum(zipFile));
		File unzipCacheDir = new File(cacheDir, unzipDir.getName());
		if (unzipCacheDir.exists()) {
			log(Level.FINE, "Zip already unzipped in cache: " + unzipCacheDir.getPath());
			return unzipCacheDir;
		} else if (zipFile.isFile()) {
			unzipDir.mkdirs();
			log(Level.FINE, "Unzip from file " + zipFile.getName());
			if (!FileUtils.unZip(zipFile, unzipDir)) {
				return null;
			}
			//when unzipped, moved to cache dir
			if (moveFile(unzipDir, unzipCacheDir)) {
				return unzipCacheDir;
			} else {
				log(Level.FINE, "Couln't move file " + unzipDir.getPath() + " to " + unzipCacheDir.getPath());
				return null;
			}
		}else {
			return null;
		}

	}

	private @Nullable String sha1sum(File zipFile) {
		MessageDigest digest = null;
		String sha1sum = null;

		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return sha1sum;
		}

		try (InputStream input = new FileInputStream(zipFile)) {
			byte[] buffer = new byte[8192];
			int len = input.read(buffer);
			while (len != -1) {
				digest.update(buffer, 0, len);
				len = input.read(buffer);
			}
			// Get the hash's bytes
			byte[] bytes = digest.digest();

			// Convert bytes to hexadecimal format
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			sha1sum = sb.toString();

		} catch (IOException e) {
			return null;
		}

		return sha1sum;
	}

	private void printPathError(File path) {
		log(Level.FINE, "Classpath '" + path + "' doesn't exist.");
	}

	/**
	 * <p>
	 * In case of an empty path, create the defaultSubDir directory in the baseDir.
	 * For instance if the program is launch without options and with default paths
	 * empty it will create every sub-directory needed inside the working directory.
	 * <p>
	 * In case of path non empty and equals to defaultPath, the directory is also
	 * created if it's not already existing.
	 * <p>
	 * In case of path non empty and different to defaultPath, the user as given a
	 * custom path from where to load the resource, hence the directory isn't
	 * created in case of a mistake.
	 *
	 * @param path
	 * @param defaultPath
	 * @param baseDir
	 * @param defaultSubDir
	 * @return the file pointing to the directory initialized
	 */
	private File initializeFilePath(@Nullable String path, String defaultPath, String baseDir, String defaultSubDir) {
		File file;
		if (path == null) {
			path = "";
		}
		if (path.equals("")) {
			file = new File(baseDir + File.separatorChar + defaultSubDir);
			if (!file.exists()) {
				FileUtils.mkDirs(file);
			}
		} else {
			file = new File(path);
			if (path.equals(defaultPath) && !file.exists()) {
				FileUtils.mkDirs(file);
			}
		}
		return file;
	}

	private File initializePath(String path, String defaultSubDir, String baseDir) {
		File file;
		if (path.equals("")) {
			file = new File(baseDir + File.separatorChar + defaultSubDir);
		} else {
			file = new File(path);
		}
		if (!file.exists()) {
			log(Level.FINE, "Directory " + file.getPath() + " doesn't exist.");
		}
		return file;
	}

	@Nullable
	private File downloadRepository(String repoURL) {
		String repoFullName = "dd-" + namifyURL(repoURL);
		return loadUrl(repoFullName, repoURL);
	}


	@Nullable
	private File loadUrl(@Nullable String ddlFileName, String urlString) {
		InputStream in = null;
		File ddlFile;
		File cacheFile = new File(cacheDir, ddlFileName);
		if (!cacheFile.exists()) {
			try {
				log(Level.INFO, "Downloading repository at: " + urlString + " ...");
				URL url = new URL(urlString);
				ddlFile = new File(tempPath, ddlFileName);
				in = url.openStream();
				Files.copy(in, ddlFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				log(Level.INFO, "Repository downloaded.");
			} catch (IOException e) {
				ddlFile = null;
				log(Level.INFO, "Failed to download repository:" + e.getMessage());
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			// After successful downloading in temporary directory moves file to cache
			return (moveFile(ddlFile, cacheFile) ? cacheFile : null);
		} else {
			log(Level.FINE, "Repository already downloaded in cache: " + cacheFile.getPath());
			return cacheFile;
		}
	}

	private boolean moveFile(@Nullable File location, File destination) {
		if (location != null) {
			try {
				Path locPath = location.toPath();
				Path destPath = destination.toPath();
				File parentFile = destination.getParentFile();
				assert (parentFile != null);
				if (!parentFile.exists()) {
					FileUtils.mkDirs(parentFile);
				}
				if (destination.exists()) {
					FileUtils.deleteFolder(destination);
				}
				Files.move(locPath, destPath, StandardCopyOption.REPLACE_EXISTING);
				return true;
			} catch (IOException e) {
				return false;
			}
		}
		return false;
	}
	private static void getJars(List<String> classpathVect, File dir) {

		// Add jars
		File[] jars = dir.listFiles(new FilenameFilter() {// NOSONAR java 8 compliance except lambda

			@Override
			public boolean accept(@Nullable File arg0, @Nullable String arg1) {
				if (arg1 != null) {
					return arg1.endsWith(".jar");
				} else {
					return false;
				}
			}
		});
		for (int i = jars.length; --i >= 0;) {
			classpathVect.add(jars[i].getAbsolutePath());
		}

		// Add subfolders
		File[] subfolders = dir.listFiles(new FileFilter() {// NOSONAR java 8 compliance except lambda

			@Override
			public boolean accept(@Nullable File arg0) {
				if (arg0 != null) {
					return arg0.isDirectory();
				} else {
					return false;
				}
			}

		});
		for (File directory : subfolders) {
			assert (directory != null);
			getJars(classpathVect, directory);
		}
	}

	/**
	 * Gets the options.
	 *
	 * @return the options.
	 */
	@Nullable
	public DependencyDiscovererOptions getOptions() {
		return options;
	}

	/**
	 * Sets the options.
	 *
	 * @param options the options to set.
	 */
	public void setOptions(DependencyDiscovererOptions options) {
		this.options = options;
	}

	private String namifyURL(String urlString) {
		String urlNamifyed = "";
		try {
			URL url = new URL(urlString);
			urlNamifyed = url.getAuthority() + url.getPath();
			urlNamifyed = convertForbiddenCharacters(urlNamifyed);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		assert (urlNamifyed != null);
		return urlNamifyed;
	}

	/**
	 * <p>
	 * This method replace special characters for files and directories names. These
	 * characters are the non printable 0-31 ASCII characters and the following
	 * list:{ '<', '>', ':', '/', '\', '|', '?', '*' }
	 *
	 * <p>
	 * It will also replace dots sequences and dot at the end of the name by the
	 * character '!'.
	 *
	 * <p>
	 * {@code This method has been created for a specific usage where a suffix is added to
	 * the converted String before using it to create a directory/file, thus, reserved
	 * Windows directory names aren't replaced!}
	 *
	 * @param str
	 * @return the String without special characters
	 */
	public String convertForbiddenCharacters(String str) {
		int i;
		boolean isSpecial;
		char charValue;
		int indexCharacter = 0;
		while (indexCharacter < str.length()) {
			charValue = str.charAt(indexCharacter);
			isSpecial = false;
			i = 0;
			// Convert Unix and Windows forbidden characters
			while (i < SPECIAL_CHARACTERS.length && !isSpecial) {
				isSpecial = (charValue == SPECIAL_CHARACTERS[i]);
				i++;
			}
			// Convert non-printable characters
			if (charValue < 31 || isSpecial) {
				str = str.substring(0, indexCharacter) + "%" + ((int) charValue) + str.substring(indexCharacter + 1);
				indexCharacter += String.valueOf((int) charValue).length();
			} else if (charValue == '.') {
				// Convert dot sequences
				str = convertDotSequence(str, indexCharacter);
			}
			indexCharacter++;
		}
		// Convert dot at the end of the name (Windows special rule)
		if (str.endsWith(".")) {
			str = str.substring(0, str.length() - 1) + "!";
		}
		return str;
	}

	private String convertDotSequence(String str, int indexStart) {
		int extraDots = 0;
		while (((indexStart + extraDots + 1) < str.length()) && str.charAt(indexStart + extraDots + 1) == '.') {
			extraDots++;
		}
		if (extraDots > 0) {
			str = str.substring(0, indexStart) + "!" + str.substring(indexStart + extraDots + 1);
		}
		return str;
	}

	/**
	 * Sets the resultFile.
	 *
	 * @param resultFile the resultFile to set.
	 */
	public void setResultFile(String resultFile) {
		this.resultFile = resultFile;
	}


	/**
	 * Sets the providedClasspath.
	 *
	 * @param providedClasspath the providedClasspath to set.
	 */
	public void setProvidedClasspath(String providedClasspath) {
		this.providedClasspathDir = providedClasspath;
	}


	/**
	 * Sets the classpath.
	 *
	 * @param classpath the classpath to set.
	 */
	public void setClasspath(String classpath) {
		this.classpathDir = classpath;
	}


	/**
	 * Sets the cleanCache.
	 *
	 * @param cleanCache the cleanCache to set.
	 */
	public void setCleanCache(boolean cleanCache) {
		this.cleanCache = cleanCache;
	}


	/**
	 * Sets the outputType.
	 *
	 * @param outputType the outputType to set.
	 */
	public void setOutputType(String outputType) {
		this.type = outputType;
	}

	/**
	 * Sets the providedCachePath.
	 *
	 * @param providedCachePath the providedCachePath to set.
	 */
	public void setProvidedCachePath(String providedCachePath) {
		this.cacheDir = providedCachePath;
	}

	/**
	 * Sets the verbose.
	 *
	 * @param verbose the verbose to set.
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Sets the projectPath.
	 *
	 * @param projectPath the projectPath to set.
	 */
	public void setProjectPath(String projectPath) {
		this.projectDir = projectPath;
	}

	/**
	 * Sets the repositoryOptions.
	 *
	 * @param repositoryOptions the repositoryOptions to set.
	 */
	public void setRepositoryOptions(RepositoryOptions repositoryOptions) {
		this.repositoryOptions = repositoryOptions;
	}

	/**
	 * Gets the cacheDir.
	 *
	 * @return the cacheDir.
	 */
	@Nullable
	public String getCacheDir() {
		return cacheDir;
	}

	private void log(Level level, String message) {
		if (verbose || level.intValue() >= Level.INFO.intValue()) {
			System.out.println(level.getName() + ": " + message);
		}
	}

	/**
	 * {@link IVersionProvider} implementation that returns version information from
	 * the picocli-x.x.jar file's {@code /resources/infos.properties} file.
	 */
	static class PropertiesVersionProvider implements IVersionProvider {

		static Properties prop = new Properties();

		/**
		 * @return version of microejdd, {@code null} if not found.
		 * @throws Exception if the properties aren't loaded.
		 */
		@Override
		public String[] getVersion() throws Exception {
			return new String[] { "Tool-ApiDependencyDiscoverer - microejdd " + prop.getProperty("version") };
		}

		static {
			InputStream is = null;
			ClassLoader cl = null;
			try {
				cl = DependencyDiscovererCLI.class.getClassLoader();
				if (cl != null) {
					is = cl.getResourceAsStream("infos.properties");
					if (is != null) {
						prop.load(is);
					} else {
						System.out.println("resource stream is null");// NOSONAR static logs on console
					}
				} else {
					System.out.println("loader is null");// NOSONAR static logs on console
				}
			} catch (IOException e) {
				System.out.println("fail to load infos file");// NOSONAR static logs on console
			}
		}
	}
}