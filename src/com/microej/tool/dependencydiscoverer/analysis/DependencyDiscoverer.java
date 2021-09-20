/*
 * Java
 *
 * Copyright 2013-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.analysis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.microej.tool.dependencydiscoverer.DependencyDiscovererError;
import com.microej.tool.dependencydiscoverer.DependencyDiscovererOptions;
import com.microej.tool.dependencydiscoverer.classfinder.ExactClassfileFilter;
import com.microej.tool.dependencydiscoverer.classfinder.IJavaClassfileFilter;
import com.microej.tool.dependencydiscoverer.classfinder.JavaClassFinder;
import com.microej.tool.dependencydiscoverer.classfinder.WildCardClassfileFilter;
import com.microej.tool.dependencydiscoverer.error.ErrorHandler;
import com.microej.tool.dependencydiscoverer.writers.IDependencyWriter;
import com.microej.tool.dependencydiscoverer.writers.JsonDependencyWriter;
import com.microej.tool.dependencydiscoverer.writers.TextDependencyWriter;
import com.microej.tool.dependencydiscoverer.writers.XmlDependencyWriter;

/**
 * <p>
 * The DependencyDiscoverer API performs the analysis with the given options,
 * then sends the results to a DependencyWriter.
 * <p>
 * In case of an error, the logs are printed on {@link java.lang.System#out}
 *
 * @see TextDependencyWriter
 * @see XmlDependencyWriter
 */
public class DependencyDiscoverer {

	private static final String JAVA_LANG_OBJECT = "java/lang/Object";

	/**
	 * Application classpath
	 */
	protected File @Nullable [] classpath;

	/**
	 * Classpath on which the application is running against
	 */
	protected File @Nullable [] againstClasspath;

	//
	private final HashMap<String, TypeDependency> typesDependencies;
	private final HashMap<MethodReference, MethodDependency> methodsDependencies;
	private final HashMap<FieldReference, FieldDependency> fieldsDependencies;

	private final HashMap<String, AnalyzedClassfile> loadedClassNodes;
	private final ArrayList<AnalyzedMethod> analyzedMethods;

	private int nextMethodToAnalyzePtr;

	private DependencyDiscovererOptions options;

	private final ErrorHandler errorHandler;

	/**
	 * Create a {@link DependencyDiscoverer} object. Every fields are initialized
	 * with default values. Options need to be passed through either
	 * {@link DependencyDiscoverer#setOptions(DependencyDiscovererOptions)} or
	 * {@link DependencyDiscoverer#setOptions(String, String, String, String, String)}
	 * method.
	 */
	public DependencyDiscoverer() {
		typesDependencies = new HashMap<>();
		methodsDependencies = new HashMap<>();
		fieldsDependencies = new HashMap<>();
		loadedClassNodes = new HashMap<>();
		analyzedMethods = new ArrayList<>();
		nextMethodToAnalyzePtr = 0;
		options = new DependencyDiscovererOptions();
		errorHandler = new ErrorHandler();
	}

	/**
	 * Create a {@link DependencyDiscovererOptions} object with the given arguments
	 * and attribute it to the {@link DependencyDiscoverer} options field.
	 *
	 * @param classpath
	 * @param againstClasspath
	 * @param outputFile
	 * @param outputType
	 * @param entryPoints
	 */
	public void setOptions(String classpath, String againstClasspath, String outputFile, String outputType,
			String entryPoints) {
		this.options = newOptions();
		options.setOptions(classpath, againstClasspath, outputFile, outputType, entryPoints);
	}

	/**
	 * Sets the values of options from given {@link DependencyDiscovererOptions}
	 *
	 * @param options to set
	 */
	public void setOptions(DependencyDiscovererOptions options) {
		this.options = options;
	}

	/**
	 * Launch the analysis, options must have been given pre-emptively through
	 * either the method
	 * {@link DependencyDiscoverer#setOptions(DependencyDiscovererOptions)} or
	 * {@link DependencyDiscoverer#setOptions(String, String, String, String, String)}
	 */
	public void run() {
		DependencyDiscovererOptions localOptions = this.options;
		String classpathStr = localOptions.getClasspath();

		if (classpathStr == null) {
			errorHandler.addNoFile(new DependencyDiscovererError().missingClasspath());
			return;
		}

		File[] splitAndCheck = splitAndCheck(classpathStr);
		classpath = splitAndCheck;
		againstClasspath = splitAndCheck(localOptions.getAgainstClasspath());

		if (errorHandler.hasError()) {
			return;
		}
		List<String> entryPoints = options.getEntryPoints();
		int nbEntryPoints = entryPoints.size();
		if (nbEntryPoints == 0) {
			errorHandler.addNoFile(new DependencyDiscovererError().missingEntryPoint());
			return;
		}

		// VERSION
		Map<String, ClassNode> allClassNodes = new HashMap<>();
		for (String entryPoint : entryPoints) {
			IJavaClassfileFilter filter;
			if (entryPoint.endsWith("*")) {
				filter = new WildCardClassfileFilter(new String[] { entryPoint });
			} else {
				filter = new ExactClassfileFilter(new String[] { entryPoint });
			}
			try {
				assert (null != classpath);
				File[] classpath2 = classpath;
				JavaClassFinder.find(allClassNodes, classpath2, filter, errorHandler);
			} catch (Exception e) {
				String message = e.getMessage();
				assert (message != null);
				errorHandler.addNoFile(new DependencyDiscovererError().unexpectedIOError(message));
				return;
			}
		}
		if (allClassNodes.size() == 0) {
			errorHandler.addNoFile(new DependencyDiscovererError()
					.noMatchingEntryPoints(String.join(",", entryPoints), classpathStr));
			return;
		}

		// Dependency search process:
		// bootstrap from root methods
		// then for each method search all its dependencies
		// Dependency discoverer dumps all dependencies that cannot be loaded

		for (ClassNode c : allClassNodes.values()) {
			AnalyzedClassfile loadedClassNode = loadedClassNodes.get(c.name);
			if (loadedClassNode != null) {
				continue; // duplicate entry point in command line
			}
			loadedClassNode = new AnalyzedClassfile(c);
			loadedClassNodes.put(c.name, loadedClassNode);

			// add hierarchy dependencies
			assert (c.superName != null);
			String superClassName = c.superName;

			List<@NonNull String> interfacesName = c.interfaces;
			assert (interfacesName != null);
			addTypeDependency(superClassName, loadedClassNode);
			for (String interfaceName : interfacesName) {
				addTypeDependency(interfaceName, loadedClassNode);
			}
			for (MethodNode md : c.methods) {
				MethodNode md2 = md;
				addMethodToAnalyze(new AnalyzedMethod(c, md2));
			}
		}

		DependencyOpcodeGenerator mdFinder = new DependencyOpcodeGenerator(this);

		// search loop
		while (nextMethodToAnalyzePtr < analyzedMethods.size()) {
			AnalyzedMethod method = getCurrentAnalyzedMethod();
			assert (method != null);
			MethodNode md = method.getMd();
			md.accept(mdFinder);

			// If in flags combinations with 'native method' flag
			if (Opcodes.ACC_NATIVE <= md.access && md.access < Opcodes.ACC_INTERFACE) {
				addNativeMethodDependency(method.getDeclaringType(), md);
			}

			if (errorHandler.hasError()) {
				return;
			}
			++nextMethodToAnalyzePtr;
		}

		ArrayList<@NonNull TypeDependency> typesDep = new ArrayList<>();
		for (TypeDependency dep : typesDependencies.values()) {
			typesDep.add(dep);
		}
		typesDep.sort(new Comparator<TypeDependency>() {// NOSONAR java 8 compliance except lambda
			@Override
			public int compare(TypeDependency arg0, TypeDependency arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}
		});

		ArrayList<@NonNull MethodDependency> methodsRef = new ArrayList<>();
		for (MethodDependency dep : methodsDependencies.values()) {
			methodsRef.add(dep);
		}
		methodsRef.sort(new Comparator<MethodDependency>() {// NOSONAR java 8 compliance except lambda

			@Override
			public int compare(MethodDependency arg0, MethodDependency arg1) {
				int typeCompareResult = arg0.getMethodRef().getOwner().compareTo(arg1.getMethodRef().getOwner());
				if (typeCompareResult != 0) {
					return typeCompareResult;
				}
				int methodNameCompareResult = arg0.getMethodRef().getName().compareTo(arg1.getMethodRef().getName());
				if (methodNameCompareResult != 0) {
					return methodNameCompareResult;
				}
				return arg0.getMethodRef().getDescriptor().compareTo(arg1.getMethodRef().getDescriptor());
			}
		});

		ArrayList<@NonNull FieldDependency> fieldsDep = new ArrayList<>();
		for (FieldDependency dep : fieldsDependencies.values()) {
			fieldsDep.add(dep);
		}
		fieldsDep.sort(new Comparator<FieldDependency>() {// NOSONAR java 8 compliance except lambda
			@Override
			public int compare(FieldDependency arg0, FieldDependency arg1) {
				int typeCompareResult = arg0.getFieldReference().getFieldType()
						.compareTo(arg1.getFieldReference().getFieldType());
				if (typeCompareResult != 0) {
					return typeCompareResult;
				}
				return arg0.getFieldReference().getFieldName().compareTo(arg1.getFieldReference().getFieldName());
			}
		});

		// print all dependencies sorted in alphabetical order
		PrintStream out;
		if (options.getOutputFile() != null) {
			try {
				out = new PrintStream(new FileOutputStream(options.getOutputFile()));
			} catch (IOException e) {
				// bad output file
				out = System.out;// NOSONAR fallback to default output
			}
		} else {
			out = System.out;// NOSONAR fallback to default output
		}

		IDependencyWriter writer = null;
		String outputType = options.getOutputType();
		if (outputType != null) {
			if (outputType.equals("text")) {
				writer = new TextDependencyWriter();
			} else if (outputType.equals("xml")) {
				writer = new XmlDependencyWriter();
			} else if (outputType.equals("json")) {
				writer = new JsonDependencyWriter();
			} else {
				writer = new TextDependencyWriter();
			}
		} else {
			writer = new TextDependencyWriter();
		}

		writer.setTypeDependencies(typesDep);
		writer.setMethodDependencies(methodsRef);
		writer.setFieldDependencies(fieldsDep);
		assert (out != null);
		writer.write(out);
		out.close();
	}

	private TypeDependency addTypeDependency(String className, AnalyzedClassfile loadedClassfile) {
		TypeDependency dependency = addTypeDependency(className);
		dependency.addUser(loadedClassfile);
		return dependency;
	}

	/**
	 * Split classpath and check if classath elements exist.
	 *
	 * @param path May be null
	 */
	private File[] splitAndCheck(@Nullable String path) {
		if (path == null) {
			return new File[0];
		}
		String pathSeparator = File.pathSeparator;
		assert (pathSeparator != null);
		String[] splitStr = splitRemoveEmpty(path, pathSeparator);
		ArrayList<File> resultVect = new ArrayList<>();
		for (String pathStr : splitStr) {
			pathStr = pathStr.trim();
			assert (pathStr != null);
			File f = new File(pathStr);
			if (!f.exists()) {
				assert (pathStr != null);
				errorHandler.addNoFile(new DependencyDiscovererError().pathDoesNotExist(pathStr));
			}
			resultVect.add(f);
		}
		File[] array = resultVect.toArray(new File[resultVect.size()]);
		assert (array != null);
		return array;

	}

	/**
	 * Slit the given String with the given separator using
	 * {@link java.lang.String#split(String)} method, then remove empty strings.
	 *
	 * @param str
	 * @param separator
	 * @return array containing splited String values
	 */
	public static String[] splitRemoveEmpty(String str, String separator) {
		String[] splitStr = str.split(separator);
		ArrayList<String> resultVect = new ArrayList<>();
		for (String elementStr : splitStr) {
			elementStr = elementStr.trim();
			if (elementStr.length() == 0) {
				continue; // skip empty
			}
			resultVect.add(elementStr);
		}
		String[] array = resultVect.toArray(new String[resultVect.size()]);
		assert (array != null);
		return array;
	}

	@Nullable
	private AnalyzedClassfile loadClassfile(File[] classpath, String typeName) {

		AnalyzedClassfile loadedClassfile = loadedClassNodes.get(typeName);
		if (loadedClassfile != null) {
			return loadedClassfile; // already loaded
		}
		AnalyzedClassfile c = loadClassfilePart2(classpath, new ExactClassfileFilter(new String[] { typeName }));

		if (c != null) {
			loadedClassNodes.put(typeName, c);
		}
		return c;
	}

	private @Nullable AnalyzedClassfile loadClassfilePart2(File[] classpath,
			ExactClassfileFilter filter) {
		HashMap<String, ClassNode> classFileContainer = new HashMap<>();
		try {
			JavaClassFinder.find(classFileContainer, classpath, filter, errorHandler);
		} catch (IOException e) {
			String message = e.getMessage();
			assert (message != null);
			errorHandler.addNoFile(new DependencyDiscovererError().unexpectedIOError(message));
			return null;
		}
		if (classFileContainer.size() == 0) {
			return null;
		} else {
			ClassNode classfile = (ClassNode) classFileContainer.values().toArray()[0];
			assert (classfile != null);
			return new AnalyzedClassfile(classfile);
		}
	}

	/**
	 * Check if a dependency with the same {@link TypeDependency} has already been
	 * added to the list, if not, create a dependence and adds it. In case of a
	 * local field, the method currently being analyzed is also added.
	 *
	 * @param type to add.
	 * @return the created or recoverer dependency
	 */
	public TypeDependency addTypeDependency(String type) {
		TypeDependency dep = typesDependencies.get(type);
		if (dep == null) {
			dep = newTypeDependency(type);
		}
		addCurrentMethodDependency(dep);
		return dep;
	}

	private TypeDependency newTypeDependency(String type) {
		TypeDependency dep;
		dep = new TypeDependency(type);
		typesDependencies.put(type, dep);

		// find type
		File[] localClasspath = this.classpath;
		assert (localClasspath != null);
		AnalyzedClassfile classfile = loadClassfile(localClasspath, type);
		if (classfile == null) {
			// type not found
			// try to load it from againstClasspath
			assert (againstClasspath != null);
			classfile = loadClassfile(againstClasspath, type);
			if (classfile == null) {
				dep.setState(Dependency.STATE_NOT_FOUND);
			} else {
				dep.classfile = classfile.getClassfile();
				dep.setState(Dependency.STATE_FOUND_IN_AGAINST_CLASSPATH);
			}
		} else {
			dep.classfile = classfile.getClassfile();
			dep.setState(Dependency.STATE_FOUND_IN_CLASSPATH);
		}
		return dep;
	}

	private void addCurrentMethodDependency(Dependency dep) {
		AnalyzedMethod currentMethod = getCurrentAnalyzedMethod();
		if (currentMethod != null) {
			dep.addCaller(currentMethod);
		}
	}

	private @Nullable AnalyzedMethod getCurrentAnalyzedMethod() {
		try {
			return analyzedMethods.get(nextMethodToAnalyzePtr);
		} catch (Exception e) {
			// not yet initialized, or something else?
			return null;
		}
	}

	/**
	 * Add a dependency on the current method being analyzed
	 *
	 * @param ref , method reference.
	 */
	public void addMethodDependency(MethodReference ref) {
		MethodDependency dep = addMethodDependency0(ref);
		addCurrentMethodDependency(dep);
	}

	/**
	 * Create a {@link MethodDependency} object from parameters and sets it to
	 * native. Then add it to the method dependency list.
	 *
	 * @param declaringType {@link ClassNode} containing the method.
	 * @param md            the {@link MethodNode}.
	 */
	public void addNativeMethodDependency(ClassNode declaringType, MethodNode md) {
		String desc = md.desc;
		String name = md.name;
		String owner = declaringType.name;
		MethodReference ref = new MethodReference(owner, name, desc);
		MethodDependency dep = addMethodDependency0(ref);
		dep.setNative();

	}

	private MethodDependency addMethodDependency0(MethodReference ref) {

		MethodDependency dep = methodsDependencies.get(ref);
		if (dep == null) {
			dep = newMethodDependency(ref);
		}
		return dep;
	}

	private MethodDependency newMethodDependency(MethodReference ref) {// NOSONAR keep legacy processing function
		if (isArray(ref.getOwner())) {
			// method call on an array is on java.lang.Object for sure
			String typeDependencyChars = JAVA_LANG_OBJECT;
			ref = new MethodReference(typeDependencyChars, ref.getName(), ref.getDescriptor(), ref.isInterface());
		}

		MethodDependency dep = new MethodDependency(ref);
		methodsDependencies.put(ref, dep);

		TypeDependency typeDep = addTypeDependency(ref.getOwner());
		if (typeDep.isNotFound()) {
			dep.setState(Dependency.STATE_NOT_FOUND);
		} else {
			// here, classfile has been found
			// find method
			ClassNode classfileDep = typeDep.classfile;
			assert (classfileDep != null);
			MethodNode md = getMethodInHierarchy(classfileDep, ref);
			if (md == null) {
				// type is in the classpath (as long as it is found just before!),
				// consider it is a invoke virtual of a not redefined method from the first not
				// in classpath
				// superclass
				TypeDependency currentTypeDependency = typeDep;
				do { // NOSONAR keep legacy processing function
					ClassNode classfile = currentTypeDependency.classfile;
					if (classfile == null) {
						// first not in classpath superclass
						dep.setState(typeDep.getState());
						String classValue = currentTypeDependency.getName();
						MethodReference methodrefValue = new MethodReference(classValue, dep.getMethodRef().getName(),
								dep.getMethodRef().getDescriptor());
						addMethodDependency(methodrefValue);
						break;
					}
					String superclass = classfile.superName;
					if (superclass == null) {
						dep.setState(Dependency.STATE_NOT_FOUND);
						break;
					}
					TypeDependency superTypeDependency = addTypeDependency(superclass);
					currentTypeDependency = superTypeDependency;
				} while (true);
			} else {
				dep.setState(typeDep.getState());
				// If in flags combinations range with 'native method' flag
				if (Opcodes.ACC_NATIVE <= md.access && md.access < Opcodes.ACC_INTERFACE) {
					dep.setNative();
				}
				if (typeDep.isLoadedFromClasspath()) {
					// transitive dependency search
					ClassNode classfile = typeDep.classfile;
					assert (classfile != null);
					addMethodToAnalyze(new AnalyzedMethod(classfile, md));
				}
			}
		}
		return dep;
	}

	private void addMethodToAnalyze(AnalyzedMethod analyzedMethod) {
		analyzedMethods.add(analyzedMethod);
	}


	private boolean isArray(String type) {
		return type.indexOf('[') == 0;
	}

	/**
	 * Check if a dependency with the same {@link FieldReference} has already been
	 * added to the list, if not, create a dependence and adds it. In case of a
	 * local field, the method currently being analyzed is also added.
	 *
	 * @param ref the field reference to add
	 */
	public void addFieldDependency(FieldReference ref) {
		FieldDependency dep = fieldsDependencies.get(ref);
		if (dep == null) {
			dep = newFieldDependency(ref);
		}
		addCurrentMethodDependency(dep);
	}

	private FieldDependency newFieldDependency(FieldReference ref) {
		FieldDependency dep;
		dep = new FieldDependency(ref);
		fieldsDependencies.put(ref, dep);

		TypeDependency typeDep = addTypeDependency(ref.getTypeName());
		if (typeDep.isNotFound()) {
			dep.setState(Dependency.STATE_NOT_FOUND);
		} else {
			// here, classfile has been found
			// find field
			ClassNode classfile = typeDep.classfile;
			assert (classfile != null);
			FieldNode fd = getFieldInHierarchy(classfile, ref);
			if (fd == null) {
				dep.setState(Dependency.STATE_NOT_FOUND);
			} else {
				dep.setState(typeDep.getState());
			}
		}
		return dep;
	}

	/**
	 * @return null if not found
	 */
	private static @Nullable MethodNode getMethod(ClassNode classfile, MethodReference ref) {
		MethodNode[] methods = new MethodNode[classfile.methods.size()];
		classfile.methods.toArray(methods);
		for (int i = methods.length; --i >= 0;) {

			MethodNode md = methods[i];

			if (md.desc.equals(ref.getDescriptor()) && md.name.equals(ref.getName())) {
				return md;
			}
		}
		return null;
	}

	private @Nullable MethodNode getMethodInHierarchy(ClassNode classfile, MethodReference ref) {
		MethodNode m = getMethod(classfile, ref);
		if (m != null) {
			return m;
		}
		String superclass = classfile.superName;
		if (superclass == null) {
			return null;
		}
		TypeDependency superTypeDependency = addTypeDependency(superclass);
		if (superTypeDependency.classfile != null) {
			m = getMethodInHierarchy(superTypeDependency.classfile, ref);
			if (m != null) {
				return m;
			}
		}
		String[] interfaces = new String[classfile.interfaces.size()];
		classfile.interfaces.toArray(interfaces);
		for (int i = interfaces.length; --i >= 0;) {
			String type = interfaces[i];
			assert (type != null);
			TypeDependency superInterfaceDependency = addTypeDependency(type);
			if (superInterfaceDependency.classfile != null) {
				m = getMethodInHierarchy(superInterfaceDependency.classfile, ref);
				if (m != null) {
					return m;
				}
			}
		}
		return null;
	}

	private @Nullable FieldNode getFieldInHierarchy(ClassNode classfile, FieldReference ref) {
		FieldNode f = getField(classfile, ref);
		if (f != null) {
			return f;
		}
		String superclass = classfile.superName;
		if (superclass == null) {
			return null;
		}
		TypeDependency superTypeDependency = addTypeDependency(superclass);
		if (superTypeDependency.classfile != null) {
			f = getFieldInHierarchy(superTypeDependency.classfile, ref);
			if (f != null) {
				return f;
			}
		}
		String[] interfaces = new String[classfile.interfaces.size()];
		classfile.interfaces.toArray(interfaces);
		for (int i = interfaces.length; --i >= 0;) {
			String type = interfaces[i];
			assert (type != null);
			TypeDependency superInterfaceDependency = addTypeDependency(type);
			if (superInterfaceDependency.classfile != null) {
				f = getFieldInHierarchy(superInterfaceDependency.classfile, ref);
				if (f != null) {
					return f;
				}
			}
		}
		return null;
	}

	/**
	 * @return null if not found
	 */
	private static @Nullable FieldNode getField(ClassNode classfile, FieldReference ref) {
		FieldNode[] fields = new FieldNode[classfile.fields.size()];
		classfile.fields.toArray(fields);
		for (int i = fields.length; --i >= 0;) {
			FieldNode fd = fields[i];
			if (fd.desc.equals(ref.getFieldType()) && fd.name.equals(ref.getFieldName())) {
				return fd;
			}
		}
		return null;
	}

	/**
	 *
	 * @return a new instance of
	 *         {@link com.microej.tool.dependencydiscoverer.DependencyDiscovererOptions}
	 */
	public DependencyDiscovererOptions newOptions() {
		return new DependencyDiscovererOptions();
	}

	/**
	 *
	 * @return {@link DependencyDiscoverer}'s error handler
	 */
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

}
