/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

/**
 * Enumeration of the different kinds of Java imports that can appear in a compilation unit.
 *
 * <p>This enum represents both traditional package imports (from Java source files) and module
 * imports (from the Java Platform Module System, JPMS).
 *
 */
public enum CtImportKind {
	/**
	 * A single type import declaration.
	 *
	 * <p>Example: {@code import my.package.Type;}
	 *
	 * <p>This allows referencing the type by its simple name instead of its fully-qualified name.
	 */
	TYPE,

	/**
	 * An on-demand import declaration for all types in a package.
	 *
	 * <p>Example: {@code import my.package.*;}
	 *
	 * <p>This makes all public types in the specified package available using their simple names.
	 */
	ALL_TYPES,

	/**
	 * A static on-demand import declaration for all static members of a type.
	 *
	 * <p>Example: {@code import static my.package.Type.*;}
	 *
	 * <p>This makes all static members (fields, methods, and nested types) of the specified type
	 * available without qualifying them with the type name.
	 */
	ALL_STATIC_MEMBERS,

	/**
	 * A static import declaration for a single static field.
	 *
	 * <p>Example: {@code import static my.package.Type.f;}
	 *
	 * <p>This allows referencing the static field by its simple name within the importing
	 * compilation unit.
	 */
	FIELD,

	/**
	 * A static import declaration for a single static method.
	 *
	 * <p>Example: {@code import static my.package.Type.m;}
	 *
	 * <p>This allows calling the static method by its simple name within the importing
	 * compilation unit.
	 */
	METHOD,

	/**
	 * An import that cannot be resolved to a specific kind.
	 *
	 * <p>This kind is used when Spoon operates in no-classpath mode and the reference cannot be
	 * resolved. In such cases, the import is stored as a plain string and printed as-is during
	 * pretty printing. The original import statement is preserved without semantic analysis.
	 */
	UNRESOLVED,

	/**
	 * A module import declaration for all public types of a Java module.
	 *
	 * <p>Example: {@code import module java.base;}
	 *
	 */
	MODULE
}
