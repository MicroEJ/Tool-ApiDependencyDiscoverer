/*
 * Java
 *
 * Copyright 2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.microej.tool.dependencydiscoverer.analysis.Dependency;
import com.microej.tool.dependencydiscoverer.analysis.FieldDependency;
import com.microej.tool.dependencydiscoverer.analysis.FieldReference;
import com.microej.tool.dependencydiscoverer.analysis.MethodDependency;
import com.microej.tool.dependencydiscoverer.analysis.MethodReference;
import com.microej.tool.dependencydiscoverer.analysis.TypeDependency;
import com.microej.tool.dependencydiscoverer.writers.JsonDependencyWriter;

public class TestsJsonWriter {

	private JsonDependencyWriter writer;
	private ByteArrayOutputStream outStream;
	private PrintStream out;
	private ArrayList<TypeDependency> typesDep;
	private ArrayList<FieldDependency> fieldsDep;
	private ArrayList<MethodDependency> methodsDep;

	private static final String FIELD = "\"field\"";
	private static final String TYPE = "\"type\"";
	private static final String METHOD = "\"method\"";
	private static final String NATIVE_METHOD = "\"method native\"";

	private static final String OBJECT_TYPE_DECODE = Object.class.getName();
	private static final String OBJECT_TYPE = dotNametoSlashName(OBJECT_TYPE_DECODE);

	private static final String STRING_TYPE_DECODE = String.class.getName();
	private static final String STRING_TYPE = dotNametoSlashName(STRING_TYPE_DECODE);

	private static final String SYSTEM_TYPE_DECODE = System.class.getName();
	private static final String SYSTEM_TYPE = dotNametoSlashName(SYSTEM_TYPE_DECODE);

	private static final String PRINT_STREAM_TYPE_DECODE = PrintStream.class.getName();
	private static final String PRINT_STREAM_TYPE = dotNametoSlashName(PRINT_STREAM_TYPE_DECODE);

	private static final String JSON_START = "{\r\n\t\"require\":{\r\n";
	private static final String JSON_END = "\t}\r\n}";

	private static final String JSON_DEPENDENCIES_END = "\t\t],\r\n";
	private static final String JSON_DEPENDENCIES_END_LAST = "\t\t]\r\n";

	private static String jsonDependenciesStart(String type) {
		return "\t\t" + type + ":[\r\n";
	}

	private static String jsonDependency(String depName, boolean last) {
		return "\t\t\t{\r\n\t\t\t\t\"name\":\"" + depName + "\"\r\n\t\t\t}" + (last ? "\r\n" : ",\r\n");
	}

	private static String jsonDependency(String depName) {
		return jsonDependency(depName, true);
	}

	private static String dotNametoSlashName(String dotName) {
		return dotName.replace('.', '/');
	}

	private void writeDependencies() {
		writer.setTypeDependencies(typesDep);
		writer.setFieldDependencies(fieldsDep);
		writer.setMethodDependencies(methodsDep);
		writer.write(out);
	}

	private TypeDependency createTypeDependency(String typeName) {
		TypeDependency dep = new TypeDependency(typeName);
		dep.setState(Dependency.STATE_NOT_FOUND);
		return dep;
	}

	private FieldDependency createFieldDependency(String typeName, String fieldName, String fieldType) {
		FieldReference refValue = new FieldReference(typeName, fieldType, fieldName);
		FieldDependency dep = new FieldDependency(refValue);
		dep.setState(Dependency.STATE_NOT_FOUND);
		return dep;
	}

	private MethodDependency createMethodDependency(String typeName, String methodName, String methodType) {
		MethodReference refValue = new MethodReference(typeName, methodName, methodType);
		MethodDependency dep = new MethodDependency(refValue);
		dep.setState(Dependency.STATE_NOT_FOUND);
		return dep;
	}

	// FROM HERE, TESTS

	@Before
	public void setUp() {
		writer = new JsonDependencyWriter();
		outStream = new ByteArrayOutputStream();
		out = new PrintStream(outStream);
		typesDep = new ArrayList<TypeDependency>();
		fieldsDep = new ArrayList<FieldDependency>();
		methodsDep = new ArrayList<MethodDependency>();
	}

	@Test
	public void testSimpleTypeDep() {
		typesDep.add(createTypeDependency(OBJECT_TYPE));
		writeDependencies();

		String typeName = OBJECT_TYPE_DECODE;
		String deps = JSON_START + jsonDependenciesStart(TYPE) + jsonDependency(typeName) + JSON_DEPENDENCIES_END_LAST
				+ JSON_END;
		assertJsonOutput(deps);
	}

	@Test
	public void testSimpleFieldDep() {
		fieldsDep.add(createFieldDependency(SYSTEM_TYPE, "out", PRINT_STREAM_TYPE));
		writeDependencies();

		String fieldName = SYSTEM_TYPE_DECODE + '.' + "out";
		String deps = JSON_START + jsonDependenciesStart(FIELD) + jsonDependency(fieldName) + JSON_DEPENDENCIES_END_LAST
				+ JSON_END;
		assertJsonOutput(deps);
	}

	@Test
	public void testSimpleMethodDep() {
		String mName = "hashCode";
		methodsDep.add(createMethodDependency(STRING_TYPE, mName, "()I"));
		writeDependencies();

		String methodName = STRING_TYPE_DECODE + "." + mName + "()int";
		String deps = JSON_START + jsonDependenciesStart(METHOD)
		+ jsonDependency(methodName)
		+ JSON_DEPENDENCIES_END_LAST
		+ JSON_END;
		assertJsonOutput(deps);
	}

	@Test
	public void testTypeAndMethodDep() {
		String mName = "hashCode";
		methodsDep.add(createMethodDependency(STRING_TYPE, mName, "()I"));
		typesDep.add(createTypeDependency(OBJECT_TYPE));
		writeDependencies();

		String typeName = OBJECT_TYPE_DECODE;

		String methodName = STRING_TYPE_DECODE + "." + mName + "()int";
		String deps = JSON_START + jsonDependenciesStart(TYPE) + jsonDependency(typeName) + JSON_DEPENDENCIES_END
				+ jsonDependenciesStart(METHOD) + jsonDependency(methodName) + JSON_DEPENDENCIES_END_LAST
				+ JSON_END;
		assertJsonOutput(deps);
	}

	@Test
	public void testMultipleTypeDep() {
		typesDep.add(createTypeDependency(OBJECT_TYPE));
		typesDep.add(createTypeDependency(SYSTEM_TYPE));
		typesDep.add(createTypeDependency(STRING_TYPE));
		writeDependencies();

		String deps = JSON_START + jsonDependenciesStart(TYPE) + jsonDependency(OBJECT_TYPE_DECODE, false)
		+ jsonDependency(SYSTEM_TYPE_DECODE, false) + jsonDependency(STRING_TYPE_DECODE)
		+ JSON_DEPENDENCIES_END_LAST
		+ JSON_END;
		assertJsonOutput(deps);
	}

	// FROM HERE, HELPER METHODS

	private void assertJsonOutput(String expected) {
		String actual = new String(outStream.toByteArray());
		printStrings(expected, actual);
		assert (expected.equals(actual));
	}


	private void printStrings(String expected, String actual) {
		System.out.println("================= Expected ==================\n");
		System.out.println(expected);
		System.out.println("=============================================\n");
		System.out.println("================= Actual ====================\n");
		System.out.println(actual);
		System.out.println("=============================================\n");
	}
}
