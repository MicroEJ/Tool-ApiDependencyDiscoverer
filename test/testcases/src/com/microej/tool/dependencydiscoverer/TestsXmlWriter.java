/*
 * Java
 *
 * Copyright 2014-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.microej.tool.dependencydiscoverer.analysis.Dependency;
import com.microej.tool.dependencydiscoverer.analysis.FieldDependency;
import com.microej.tool.dependencydiscoverer.analysis.FieldReference;
import com.microej.tool.dependencydiscoverer.analysis.MethodDependency;
import com.microej.tool.dependencydiscoverer.analysis.MethodReference;
import com.microej.tool.dependencydiscoverer.analysis.TypeDependency;
import com.microej.tool.dependencydiscoverer.writers.XmlDependencyWriter;

public class TestsXmlWriter {
	private XmlDependencyWriter writer;
	private ByteArrayOutputStream outStream;
	private PrintStream out;
	private ArrayList<TypeDependency> typesDep;
	private ArrayList<FieldDependency> fieldsDep;
	private ArrayList<MethodDependency> methodsDep;

	private static final String OBJECT_TYPE_DECODE = Object.class.getName();
	private static final String OBJECT_TYPE        = dotNametoSlashName(OBJECT_TYPE_DECODE);

	private static final String STRING_TYPE_DECODE = String.class.getName();
	private static final String STRING_TYPE        = dotNametoSlashName(STRING_TYPE_DECODE);

	private static final String SYSTEM_TYPE_DECODE = System.class.getName();
	private static final String SYSTEM_TYPE        = dotNametoSlashName(SYSTEM_TYPE_DECODE);

	private static final String PRINT_STREAM_TYPE_DECODE = PrintStream.class.getName();
	private static final String PRINT_STREAM_TYPE       = dotNametoSlashName(PRINT_STREAM_TYPE_DECODE);

	private final static String REQUIRES = "require" ;
	private final static String PROLOGUE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" ;

	private static final String LINE_SEP = System.getProperty("line.separator");

	private static String dotNametoSlashName (String dotName) {
		return dotName.replace('.', '/');
	}

	@Before
	public void setUp () {
		writer = new XmlDependencyWriter();
		outStream = new ByteArrayOutputStream();
		out = new PrintStream(outStream);
		typesDep = new ArrayList<TypeDependency>();
		fieldsDep = new ArrayList<FieldDependency>();
		methodsDep = new ArrayList<MethodDependency>();
	}

	private void writeDependencies () {
		writer.setTypeDependencies(typesDep);
		writer.setFieldDependencies(fieldsDep);
		writer.setMethodDependencies(methodsDep);
		writer.write(out);
	}

	private TypeDependency createTypeDependency (String typeName) {
		TypeDependency dep = new TypeDependency(typeName);
		dep.setState(Dependency.STATE_NOT_FOUND);
		return dep;
	}

	private FieldDependency createFieldDependency (String typeName, String fieldName, String fieldType) {
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

	@Test
	public void testSimpleTypeDep () {
		typesDep.add(createTypeDependency(OBJECT_TYPE));
		writeDependencies();

		List<String> deps = new ArrayList<String>();
		deps.add(String.format("<type name=\"%s\"/>"+LINE_SEP, OBJECT_TYPE_DECODE));
		assertXmlOutPutList("", deps);
	}

	@Test
	public void testTypeArrayDep () {
		typesDep.add(createTypeDependency("[" + OBJECT_TYPE));
		writeDependencies();

		List<String> deps = new ArrayList<String>();
		deps.add(String.format("<type name=\"%s\"/>"+LINE_SEP, OBJECT_TYPE_DECODE + "[]"));
		assertXmlOutPutList("", deps);
	}

	@Test
	public void testTypeIntegerArrayDep () {
		typesDep.add(createTypeDependency("[I"));
		writeDependencies();

		List<String> deps = new ArrayList<String>();
		deps.add(String.format("<type name=\"%s\"/>"+LINE_SEP, "int[]"));
		assertXmlOutPutList("", deps);
	}

	@Test
	public void testSimpleFieldDep () {
		fieldsDep.add(createFieldDependency(SYSTEM_TYPE, "out", PRINT_STREAM_TYPE));
		writeDependencies();

		List<String> deps = new ArrayList<String>();
		String fieldName = SYSTEM_TYPE_DECODE+ '.' + "out";
		deps.add(String.format("<field name=\"%s\"/>"+LINE_SEP, fieldName));
		assertXmlOutPutList("", deps);
	}

	@Test
	public void testSimpleMethodDepRetInt () {
		String mName = "hashCode";

		methodsDep.add(createMethodDependency(STRING_TYPE, mName, "()I"));
		writeDependencies();

		String methodName = STRING_TYPE_DECODE + '.' + mName + "()int";
		List<String> deps = new ArrayList<String>();
		deps.add(String.format("<method name=\"%s\"/>"+LINE_SEP, methodName));
		assertXmlOutPutList("", deps);
	}

	@Test
	public void testSimpleMethodDepRetVoid () {
		String mName = "hashCode";

		methodsDep.add(createMethodDependency(STRING_TYPE, mName, "()V"));
		writeDependencies();

		String methodName = STRING_TYPE_DECODE + '.' + mName + "()void";
		List<String> deps = new ArrayList<String>();
		deps.add(String.format("<method name=\"%s\"/>"+LINE_SEP, methodName));
		assertXmlOutPutList("", deps);
	}

	@Test
	public void testSimpleMethodDepArgIntRetVoid () {
		String mName = "hashCode";

		methodsDep.add(createMethodDependency(STRING_TYPE, mName, "(I)V"));
		writeDependencies();

		String methodName = STRING_TYPE_DECODE + '.' + mName + "(int)void";
		List<String> deps = new ArrayList<String>();
		deps.add(String.format("<method name=\"%s\"/>"+LINE_SEP, methodName));
		assertXmlOutPutList("", deps);
	}

	@Test
	public void testSimpleMethodDepArgLongDoubleRetFloat () {
		String mName = "hashCode";

		methodsDep.add(createMethodDependency(STRING_TYPE, mName, "(JD)F"));
		writeDependencies();

		String methodName = STRING_TYPE_DECODE + '.' + mName + "(long,double)float";
		List<String> deps = new ArrayList<String>();
		deps.add(String.format("<method name=\"%s\"/>"+LINE_SEP, methodName));
		assertXmlOutPutList("", deps);
	}

	@Test
	public void testSimpleMethodDepArgStringRetVoid () {
		String mName = "hashCode";

		methodsDep.add(createMethodDependency(STRING_TYPE, mName, "(Ljava/lang/String;)V"));
		writeDependencies();

		String methodName = STRING_TYPE_DECODE + '.' + mName + "(java.lang.String)void";
		List<String> deps = new ArrayList<String>();
		deps.add(String.format("<method name=\"%s\"/>"+LINE_SEP, methodName));
		assertXmlOutPutList("", deps);
	}

	@Test
	public void testSimpleMethodDepArgObjectRetObject () {
		String mName = "hashCode";

		methodsDep.add(createMethodDependency(STRING_TYPE, mName, "(Ljava/lang/Object;)Ljava/lang/Object;"));
		writeDependencies();

		String methodName = STRING_TYPE_DECODE + '.' + mName + "(java.lang.Object)java.lang.Object";
		List<String> deps = new ArrayList<String>();
		deps.add(String.format("<method name=\"%s\"/>"+LINE_SEP, methodName));
		assertXmlOutPutList("", deps);
	}

	@Test
	public void testSimpleMethodDepTwoArgObjectRetObject () {
		String mName = "hashCode";

		methodsDep.add(
				createMethodDependency(STRING_TYPE, mName, "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
		writeDependencies();

		String methodName = STRING_TYPE_DECODE + '.' + mName + "(java.lang.Object,java.lang.Object)java.lang.Object";
		List<String> deps = new ArrayList<String>();
		deps.add(String.format("<method name=\"%s\"/>"+LINE_SEP, methodName));
		assertXmlOutPutList("", deps);
	}

	@Test
	public void  testMethodDepReturnArrayPrimitivetype() {
		String mName = "m";
		methodsDep.add(createMethodDependency(STRING_TYPE, mName, "()[I"));
		writeDependencies();

		String methodName = STRING_TYPE_DECODE + '.' + mName + "()" + "int[]";
		List<String> deps = new ArrayList<String>();
		deps.add(String.format("<method name=\"%s\"/>"+LINE_SEP, methodName));
		assertXmlOutPutList("", deps);
	}

	@Test
	public void  testMethodDepArgArrayObjectReturnArrayPrimitivetype() {
		String mName = "m";
		methodsDep.add(createMethodDependency(STRING_TYPE, mName, "([Ljava/lang/String;)[I"));
		writeDependencies();

		String methodName = STRING_TYPE_DECODE + '.' + mName + "(java.lang.String[])" + "int[]";
		List<String> deps = new ArrayList<String>();
		deps.add(String.format("<method name=\"%s\"/>"+LINE_SEP, methodName));
		assertXmlOutPutList("", deps);
	}

	@Test
	public void  testMethodDepTwoArgsArrayOfArrayObjectArrayOfArrayofArrayObjectReturnArrayPrimitivetype() {
		String mName = "m";
		methodsDep.add(createMethodDependency(STRING_TYPE, mName, "([[Ljava/lang/String;[[[Ljava/lang/String;)[I"));
		writeDependencies();

		String methodName = STRING_TYPE_DECODE + '.' + mName + "(java.lang.String[][],java.lang.String[][][])" + "int[]";
		List<String> deps = new ArrayList<String>();
		deps.add(String.format("<method name=\"%s\"/>" + LINE_SEP, methodName));
		assertXmlOutPutList("", deps);
	}

	@Test
	public void testMethodDepTwoArgsArrayOfPrimitiveTypePrimitiveType() {
		String mName = "hashCode";
		methodsDep.add(createMethodDependency(STRING_TYPE, mName, "([II)"));
		writeDependencies();

		String methodName = STRING_TYPE_DECODE + '.' + mName + "(int[],int)";
		List<String> deps = new ArrayList<String>();
		deps.add(String.format("<method name=\"%s\"/>" + LINE_SEP, methodName));
		assertXmlOutPutList("", deps);
	}


	private String getExpected (List<String> deps) {
		StringBuilder builder = new StringBuilder();
		builder.append(PROLOGUE).append(LINE_SEP).append('<').append(REQUIRES).append('>').append(LINE_SEP);
		if (deps != null) {
			for (String dep : deps) {
				builder.append('\t').append(dep);
			}
		}
		builder.append("</").append(REQUIRES).append(">").append(LINE_SEP);
		return builder.toString();
	}

	private void assertXmlOutPutList (String message, List<String> expectedDeps) {
		assertXmlOutPut(message, getExpected(expectedDeps));
	}

	private void assertXmlOutPut (String message, String expected) {
		String actual = new String (outStream.toByteArray());
		printStrings(expected, actual);
		Assert.assertEquals(message, expected, actual);
	}


	private void printStrings (String expected, String actual) {
		System.out.println("================= Expected ==================");
		System.out.println(expected);
		System.out.println("=============================================");
		System.out.println("================= Actual ====================");
		System.out.println(actual);
		System.out.println("=============================================");
	}

}
