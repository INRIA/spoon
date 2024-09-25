/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.api.parsing;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import spoon.JLSViolation;
import spoon.experimental.CtUnresolvedImport;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtImportKind;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * Resolves a string to a {@link CtReference}.
 */
class LinkResolver {

	private final CtElement context;
	private final Factory factory;

	/**
	 * @param context the annotated type
	 * @param factory the factory to use
	 */
	LinkResolver(CtElement context, Factory factory) {
		this.context = context;
		this.factory = factory;
	}

	/**
	 * Tries to resolve a string to a {@link CtReference}.
	 *
	 * @param string the content of a {@code @see} or {@code @link} tag
	 * @return the referenced element, if any
	 */
	public Optional<CtReference> resolve(String string) {
		// Format:
		//   <classname>
		//   <package name>
		//   <field name>
		//   <method name>
		//   <classname>#<field name>
		//   <classname>#<method name>
		//   <classname>#<constructor name>
		//   <classname>#<method name>()
		//   <classname>#<method name>(<param type>[,<param type>])
		//   <classname>#<method name>(<param type> [^,]*)
		//   module/package.class#member label
		String query = string;

		if (!query.contains("#")) {
			Optional<CtReference> existingTypePackageModule = resolveModulePackageOrClassRef(query);
			if (existingTypePackageModule.isPresent()) {
				return existingTypePackageModule;
			}
			// This is surely a module, no need to try as a member reference
			if (query.endsWith("/")) {
				return guessPackageOrModuleReferenceFromName(query);
			}
			// This contains a dot in the name (not a parameter), so this must be a type or module.
			// Do not try as local member reference.
			if (!query.contains("(") && query.contains(".")) {
				return guessPackageOrModuleReferenceFromName(query);
			}
			// If we did not find it, try our luck as a member reference
			query = "#" + query;
		}
		int fragmentIndex = query.indexOf('#');
		String modulePackage = query.substring(0, fragmentIndex);
		Optional<CtReference> contextRef = resolveModulePackageOrClassRef(modulePackage);

		// Fragment qualifier only works on types (Foo#bar)
		if (contextRef.isEmpty() || !(contextRef.get() instanceof CtTypeReference)) {
			return contextRef.or(() -> guessPackageOrModuleReferenceFromName(modulePackage));
		}

		CtType<?> outerType = ((CtTypeReference<?>) contextRef.get()).getTypeDeclaration();
		String fragment = query.substring(fragmentIndex + 1);

		return qualifyName(outerType, extractMemberName(fragment), extractParameters(fragment));
	}

	private String extractMemberName(String fragment) {
		if (fragment.contains("(")) {
			return fragment.substring(0, fragment.indexOf('('));
		}
		return fragment;
	}

	private List<Optional<CtTypeReference<?>>> extractParameters(String fragment) {
		int startIndex = fragment.indexOf('(') + 1;
		if (startIndex <= 0) {
			return List.of();
		}
		int endIndex = fragment.indexOf(')');
		if (endIndex < 0) {
			endIndex = fragment.length();
		}
		String raw = fragment.substring(startIndex, endIndex).replace(")", "").strip();

		if (raw.isEmpty()) {
			return List.of();
		}

		return Arrays.stream(raw.split(","))
			.map(it -> it.strip().split(" ")[0])
			.map(this::qualifyTypeName)
			.collect(Collectors.toList());
	}

	private Optional<CtReference> resolveModulePackageOrClassRef(String name) {
		if (name.contains("/") && !name.endsWith("/")) {
			// java.base/java.lang.String
			int moduleEndIndex = name.indexOf('/');
			String rest = name.substring(moduleEndIndex + 1);

			// TODO: This currently throws away module information when resolving the rest. This is likely
			//       fine for now, but not correct.
			return resolveTypePackageModuleAsIs(rest);
		}
		if (name.endsWith("/")) {
			// Format: "module/"
			Optional<CtReference> module = getModuleRef(name.replace("/", ""));
			if (module.isPresent()) {
				return module;
			}
		}

		return resolveTypePackageModuleAsIs(name);
	}

	private Optional<CtReference> resolveTypePackageModuleAsIs(String name) {
		return qualifyTypeName(name).map(it -> (CtReference) it)
			.or(() -> Optional.ofNullable(factory.Package().get(name)).map(CtPackage::getReference))
			.or(() -> getModuleRef(name));
	}

	private Optional<CtReference> getModuleRef(String name) {
		CtModule module = factory.Module().getModule(name);
		if (module != null) {
			return Optional.of(module.getReference());
		}
		ModuleLayer layer = factory.getEnvironment()
			.getInputClassLoader()
			.getUnnamedModule()
			.getLayer();
		if (layer == null) {
			layer = ModuleLayer.boot();
		}
		Optional<Module> javaModule = layer.findModule(name);

		return javaModule.map(it -> factory.Module().getOrCreate(it.getName()).getReference());
	}

	private Optional<CtReference> guessPackageOrModuleReferenceFromName(String name) {
		// Upper case letters indicate this is no package or module and we just don't understand it
		if (!name.toLowerCase(Locale.ROOT).equals(name)) {
			return Optional.empty();
		}

		try {
			if (name.endsWith("/")) {
				return Optional.of(
					factory.Core().createModuleReference().setSimpleName(name.replace("/", ""))
				);
			}
			if (name.contains("/")) {
				// We have something like java.base/java.lang.String but we do not know java.base/
				// We can't properly handle this, return nothing and keep it as text.
				return Optional.empty();
			}
			return Optional.of(factory.Package().createReference(name));
		} catch (JLSViolation ignored) {
			// Looks like that name wasn't quite valid...
			return Optional.empty();
		}
	}

	private Optional<CtReference> qualifyName(
		CtType<?> enclosingType,
		String memberName,
		List<Optional<CtTypeReference<?>>> parameters
	) {
		if (parameters.isEmpty()) {
			CtType<?> next = enclosingType;
			while (next != null) {
				Optional<CtReference> field = qualifyTypeNameForField(enclosingType, memberName);
				if (field.isPresent()) {
					return field;
				}
				next = next.getParent(CtType.class);
			}

			// Try again as an executable
			return qualifyTypeNameForExecutable(memberName, List.of(), enclosingType);
		}

		return qualifyTypeNameForExecutable(memberName, parameters, enclosingType);
	}

	private Optional<CtReference> qualifyTypeNameForField(CtType<?> enclosingType, String memberName) {
		if (enclosingType instanceof CtEnum<?>) {
			Optional<CtReference> enumRef = ((CtEnum<?>) enclosingType).getEnumValues()
				.stream()
				.filter(it -> it.getSimpleName().equals(memberName))
				.<CtReference>map(CtField::getReference)
				.findFirst();

			if (enumRef.isPresent()) {
				return enumRef;
			}
		}
		return enclosingType.getAllFields()
			.stream()
			.filter(it -> it.getSimpleName().equals(memberName))
			.map(it -> (CtReference) it)
			.findFirst();
	}

	private Optional<CtReference> qualifyTypeNameForExecutable(
		String elementName,
		List<Optional<CtTypeReference<?>>> parameters,
		CtType<?> type
	) {
		// References in Javadoc for inner classes can just "#name" reference elements of the enclosing class
		CtType<?> next = type;
		while (next != null) {
			Optional<CtReference> ref = qualifyTypeNameForExecutableForExactType(elementName, parameters, next);
			if (ref.isPresent()) {
				return ref;
			}
			next = next.getParent(CtType.class);
		}

		return Optional.empty();
	}

	private Optional<CtReference> qualifyTypeNameForExecutableForExactType(
		String elementName,
		List<Optional<CtTypeReference<?>>> parameters,
		CtType<?> type
	) {
		List<CtExecutableReference<?>> possibleMethods = type.getAllExecutables()
			.stream()
			.filter(it -> executableNameMatches(elementName, it))
			.collect(Collectors.toList());

		Optional<CtReference> relevantMethod;
		if (possibleMethods.size() == 1) {
			relevantMethod = Optional.of(possibleMethods.get(0));
		} else {
			relevantMethod = possibleMethods
				.stream()
				.filter(it -> it.getParameters().size() == parameters.size())
				.filter(it -> parameterTypesMatch(it.getParameters(), parameters))
				.map(it -> (CtReference) it)
				.findFirst();
		}

		return relevantMethod;
	}

	private static boolean executableNameMatches(String elementName, CtExecutableReference<?> it) {
		if (it.getSimpleName().equals(elementName)) {
			return true;
		}
		return it.isConstructor() && it.getDeclaringType().getSimpleName().equals(elementName);
	}

	private boolean parameterTypesMatch(
		List<CtTypeReference<?>> actualParams,
		List<Optional<CtTypeReference<?>>> expectedParameters
	) {
		for (int i = 0; i < expectedParameters.size(); i++) {
			// We don't know all parameters (incomplete classpath?) so just assume they'd match
			if (expectedParameters.get(i).isEmpty()) {
				continue;
			}

			String actualName = actualParams.get(i).getQualifiedName();
			if (!actualName.equals(expectedParameters.get(i).get().getQualifiedName())) {
				return false;
			}
		}
		return true;
	}

	private Optional<CtTypeReference<?>> qualifyTypeName(String name) {
		Optional<CtTypeReference<?>> qualifiedNameOpt = qualifyTypeNameNoArray(
			name.replace("[]", "").replace("...", "")
		);

		if (qualifiedNameOpt.isEmpty()) {
			return Optional.empty();
		}
		CtTypeReference<?> qualifiedName = qualifiedNameOpt.get();

		if (!name.contains("[]") && !name.endsWith("...")) {
			return Optional.of(qualifiedName);
		}

		int arrayDepth = 0;
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) == '[') {
				arrayDepth++;
			}
		}
		if (name.endsWith("...")) {
			arrayDepth++;
		}

		for (int i = 0; i < arrayDepth; i++) {
			qualifiedName = factory.createArrayReference(qualifiedName);
		}

		return Optional.of(qualifiedName);
	}

	private Optional<CtTypeReference<?>> qualifyTypeNameNoArray(String name) {
		return qualifyType(name).map(CtType::getReference);
	}

	private Optional<CtType<?>> qualifyType(String name) {
		CtType<?> contextType = context instanceof CtType ? (CtType<?>) context : context.getParent(CtType.class);

		if (contextType != null && !name.isBlank()) {
			Optional<CtTypeReference<?>> type = contextType.getReferencedTypes()
				.stream()
				.filter(it -> it.getSimpleName().equals(name) || it.getQualifiedName().equals(name))
				.findAny();
			if (type.isPresent()) {
				return Optional.ofNullable(type.get().getTypeDeclaration());
			}

			CtPackage contextPackage = contextType.getPackage();
			if (contextPackage == null && contextType.getDeclaringType() != null) {
				contextPackage = contextType.getDeclaringType().getPackage();
			}
			if (contextPackage != null) {
				CtType<?> siblingType = contextPackage.getType(name);
				if (siblingType != null) {
					return Optional.of(siblingType);
				}
			}
		}
		if (contextType != null && name.isBlank()) {
			return Optional.of(contextType);
		}

		CtCompilationUnit parentUnit = context.getPosition().getCompilationUnit();
		Optional<CtType<?>> importedType = getImportedType(name, parentUnit);
		if (importedType.isPresent()) {
			return importedType;
		}

		// The classes are not imported and not referenced if they are only used in javadoc...
		if (name.startsWith("java.lang")) {
			return tryLoadModelOrReflection(name);
		}

		CtType<?> directLookupType = factory.Type().get(name);
		if (directLookupType != null) {
			return Optional.of(directLookupType);
		}

		return tryLoadModelOrReflection(name)
			.or(() -> tryLoadModelOrReflection("java.lang." + name));
	}

	private Optional<CtType<?>> getImportedType(String name, CtCompilationUnit parentUnit) {
		Optional<CtType<?>> referencedImportedType = parentUnit.getImports()
			.stream()
			.filter(it -> it.getImportKind() != CtImportKind.UNRESOLVED)
			.filter(it -> it.getReference().getSimpleName().equals(name))
			.findAny()
			.flatMap(ctImport ->
				ctImport.getReferencedTypes()
					.stream()
					.filter(it -> it.getSimpleName().equals(name))
					.findFirst()
					.map(CtTypeReference::getTypeDeclaration)
			);

		if (referencedImportedType.isPresent()) {
			return referencedImportedType;
		}

		return parentUnit.getImports()
			.stream()
			.filter(it -> it.getImportKind() == CtImportKind.UNRESOLVED)
			.filter(it -> ((CtUnresolvedImport) it).getUnresolvedReference().endsWith("*"))
			.flatMap(it -> {
				String reference = ((CtUnresolvedImport) it).getUnresolvedReference();
				reference = reference.substring(0, reference.length() - 1);

				return tryLoadModelOrReflection(reference + name).stream();
			})
			.findFirst();
	}

	private Optional<CtType<?>> tryLoadModelOrReflection(String name) {
		CtType<?> inModel = factory.Type().get(name);
		if (inModel != null) {
			return Optional.of(inModel);
		}
		return tryLoadClass(name).map(factory.Type()::get);
	}

	private Optional<Class<?>> tryLoadClass(String name) {
		try {
			return Optional.of(Class.forName(name));
		} catch (ClassNotFoundException e) {
			return Optional.empty();
		}
	}
}
