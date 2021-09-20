/*
 * Java
 *
 * Copyright 2013-2021 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.tool.dependencydiscoverer.analysis;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

class DependencyOpcodeGenerator extends MethodVisitor {

	private final DependencyDiscoverer dd;

	public DependencyOpcodeGenerator(DependencyDiscoverer dd) {
		super(org.objectweb.asm.Opcodes.ASM9);
		this.dd = dd;
	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		super.visitTypeInsn(opcode, type);
		addFilteredSimpleType(type);
	}

	@Override
	public void visitLdcInsn(Object cst) {
		// uses an incomplete implementation given in ASM Javadoc
		// the program doesn't process primitive types

		String type = null;

		if (cst instanceof Type) {
			int sort = ((Type) cst).getSort();
			if (sort == Type.OBJECT) {
				type = ((Type) cst).getDescriptor();
			} else if (sort == Type.ARRAY) {
				type = ((Type) cst).getElementType().getDescriptor();
			}
		}

		if (type != null) {
			addFilteredSimpleType(type);
		}
	}

	private void addFilteredSimpleType(String type) {
		// only keep the type name with single type input
		// ('[Lmypackage/myclass;' -> won't be processed correctly)
		if (type.length() > 0 && type.charAt(type.length() - 1) == ';') {
			String typeFiltered = type.substring(type.indexOf("L") + 1, type.length() - 1);
			assert (typeFiltered != null);
			dd.addTypeDependency(typeFiltered);
		}
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String descriptor,
			boolean isInterface) {
		dd.addMethodDependency(new MethodReference(owner, name, descriptor, isInterface));
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
		addFilteredSimpleType(descriptor);
		dd.addFieldDependency(new FieldReference(owner, descriptor, name));
	}

	@Override
	public void visitLocalVariable(String name, String descriptor, String signature,
			@Nullable Label start, @Nullable Label end,
			int index) {
		super.visitLocalVariable(name, descriptor, signature, start, end, index);
		@NonNull
		String descriptorBuff = descriptor.replace("[", "");
		if (descriptorBuff.indexOf("L") == 0) {
			// we remove the L at the beginning and the ; at the end of the descriptor so
			// that it can be compared
			// to a type name
			int endIndex = descriptorBuff.indexOf(';');

			String substring = descriptorBuff.substring(1, endIndex);
			assert (substring != null);
			dd.addTypeDependency(substring);
		}
	}

	@Override
	public void visitTryCatchBlock(@Nullable Label start, @Nullable Label end, @Nullable Label handler,
			@Nullable String type) {
		if (type != null) {
			dd.addTypeDependency(type);
		}
	}

}
