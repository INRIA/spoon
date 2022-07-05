package spoon.support.compiler.jdt;

import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.ExtraCompilerModifiers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import spoon.generating.jdt.ModifierConstantsCollector;
import spoon.reflect.declaration.ModifierKind;
import spoon.support.reflect.CtExtendedModifier;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class JDTTreeBuilderQueryTest {

	private static Map<Field, ModifierKind> modifierMap;
	private static List<FieldData> data;
	private static Map<ModifierKind, Set<ModifierTarget>> targets;
	private static Map<Integer, List<Field>> rawModifiers;

	@BeforeAll
	static void setUp() {
		// first, read possible modifier bits
		ModifierConstantsCollector collector = new ModifierConstantsCollector(
				ClassFileConstants.class,
				ExtraCompilerModifiers.class
		);
		rawModifiers = collector.processClasses();
		// special cases of modifiers that are not passed to spoon
		rawModifiers.get(ClassFileConstants.AccSuper).remove(classFileConstant("AccSuper"));
		rawModifiers.get(ClassFileConstants.AccBridge).remove(classFileConstant("AccBridge"));

		data = rawModifiers
				.entrySet().stream()
				.flatMap(entry -> entry.getValue().stream().map(f -> new FieldData(entry.getKey(), f)))
				.collect(Collectors.toList());

		// this map must contain all constants from ModifierKind and its correct field from JDT
		modifierMap = new HashMap<>();
		modifierMap.put(classFileConstant("AccPublic"), ModifierKind.PUBLIC);
		modifierMap.put(classFileConstant("AccProtected"), ModifierKind.PROTECTED);
		modifierMap.put(classFileConstant("AccPrivate"), ModifierKind.PRIVATE);
		modifierMap.put(classFileConstant("AccAbstract"), ModifierKind.ABSTRACT);
		modifierMap.put(classFileConstant("AccStatic"), ModifierKind.STATIC);
		modifierMap.put(classFileConstant("AccFinal"), ModifierKind.FINAL);
		modifierMap.put(classFileConstant("AccTransient"), ModifierKind.TRANSIENT);
		modifierMap.put(classFileConstant("AccVolatile"), ModifierKind.VOLATILE);
		modifierMap.put(classFileConstant("AccSynchronized"), ModifierKind.SYNCHRONIZED);
		modifierMap.put(classFileConstant("AccNative"), ModifierKind.NATIVE);
		modifierMap.put(classFileConstant("AccStrictfp"), ModifierKind.STRICTFP);
		modifierMap.put(extraCompilerModifier("AccSealed"), ModifierKind.SEALED);
		modifierMap.put(extraCompilerModifier("AccNonSealed"), ModifierKind.NON_SEALED);
		List<ModifierKind> remaining = Arrays.stream(ModifierKind.values())
				.filter(k -> !modifierMap.containsValue(k))
				.collect(Collectors.toList());
		// check if all modifiers are present
		if (!remaining.isEmpty()) {
			fail("Following ModifierKinds need to be added to the modifierMap above: " + remaining);
		}
		ModifierTarget[] values = ModifierTarget.values();
		targets = Arrays.stream(ModifierKind.values())
				.collect(Collectors.toMap(
								Function.identity(),
								kind -> Arrays.stream(values)
										.filter(target -> target.getAllowedKinds().contains(kind))
										.collect(Collectors.toCollection(HashSet::new)),
								(a, b) -> {
									a.addAll(b);
									return a;
								}
						)
				);
	}

	private static Field classFileConstant(String name) {
		try {
			return ClassFileConstants.class.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			fail(e);
			return null;
		}
	}

	private static Field extraCompilerModifier(String name) {
		try {
			return ExtraCompilerModifiers.class.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			fail(e);
			return null;
		}
	}

	@Test
	void testTargetsNotEmpty() {
		// contract: all ModifierKinds need to be an allowed kind of at least one ModifierTargets
		List<ModifierKind> list = targets.entrySet().stream()
				.filter(entry -> entry.getValue().isEmpty())
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
		assertTrue(list.isEmpty(), list + " are not associated with any ModifierTargets");
	}

	@ParameterizedTest
	@ArgumentsSource(FieldDataProvider.class)
	void testGetModifiers(FieldData datum) {
		// contract: all spoon modifiers are read correctly from the JDT bitfield
		ModifierKind modifierKind = modifierMap.get(datum.field);
		if (modifierKind == null) {
			return;
		}
		Set<ModifierKind> modifiers = JDTTreeBuilderQuery.getModifiers(datum.modifier, false, targets.get(modifierKind))
				.stream()
				.map(CtExtendedModifier::getKind).collect(Collectors.toSet());
		assertThat("The modifier " + modifierKind + " was not extracted from the bitfield",
				firstOrNull(modifiers), is(modifierKind));

	}

	@ParameterizedTest
	@ArgumentsSource(AmbiguousFieldDataProvider.class)
	void testNoWrongModifiers(FieldData datum) {
		// contract: contextual modifiers are not added if JDT reuses bit flags in different contexts.
		// for example, AccVarArgs == AccTransient in JDT, but ModifierKind.TRANSIENT should only be
		// extracted if the target is EXECUTABLE, as methods/constructors aren't transient

		List<Field> fields = rawModifiers.get(datum.modifier);
		// we want the modifier kind of the "right" field
		ModifierKind modifierKind = modifierMap.get(datum.field);
		for (Field wrongField : fields) {
			// only process the "wrong" targets
			if (!wrongField.equals(datum.field)) {
				// we get the "wrong" kind
				ModifierKind wrongKind = modifierMap.get(wrongField);
				// if the wrong field has no ModifierKind in spoon, nothing should be extracted
				// we use ModifierTarget.NONE, as Modifiers should be guarded with
				// their appropriate target if JDT reuses the flag
				Set<ModifierTarget> target = targets.getOrDefault(wrongKind, ModifierTarget.NONE);
				// extract modifiers when passing the wrong target
				Set<ModifierKind> modifiers = JDTTreeBuilderQuery.getModifiers(datum.modifier, false, target)
						.stream()
						.map(CtExtendedModifier::getKind)
						.collect(Collectors.toSet());
				// and expect it to *not* contain the "right" modifier (means contain the wrong modifier
				// or is empty if no wrong modifier exists)
				// NOTE this might fail unexpectedly if testGetModifiers() fails too
				assertTrue((wrongKind == null && modifiers.isEmpty()) || modifiers.contains(wrongKind),
						"Modifier " + datum.field.getName() + " (0x" + Integer.toHexString(datum.modifier)
								+ ") is ambiguous with " + wrongField.getName() + ". Only apply "
								+ wrongKind + " if target is one of " + target
								+ ". Was applied with target " + targets.get(modifierKind)
				);
			}
		}
	}

	private static <T> T firstOrNull(Iterable<T> iterable) {
		for (T t : iterable) {
			return t;
		}
		return null;
	}

	private static final class FieldData {
		private final int modifier;
		private final Field field;

		private FieldData(int modifier, Field field) {
			this.modifier = modifier;
			this.field = field;
		}

		@Override
		public String toString() {
			return "FieldData("
					+ field.getDeclaringClass().getSimpleName() + "." + field.getName()
					+ " = 0x" + Integer.toHexString(modifier) + ')';
		}
	}

	public static final class FieldDataProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return data.stream().map(Arguments::of);
		}
	}

	// only provides FieldData for bits that aren't unique already
	public static final class AmbiguousFieldDataProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return data.stream()
					.filter(fieldData -> rawModifiers.get(fieldData.modifier).size() > 1)
					.map(Arguments::of);
		}
	}
}
