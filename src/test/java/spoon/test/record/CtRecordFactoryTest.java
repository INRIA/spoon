package spoon.test.record;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtRecord;
import spoon.reflect.declaration.CtRecordComponent;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.CtScanner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.testing.assertions.SpoonAssertions.assertThat;

/**
 * This class implements tests on records created with the Factory API.
 */
public class CtRecordFactoryTest {

	@Test
	public void testSingleParameterRecord() {
		// contract: a record with a single parameter should be created as a class
		// with a single field and a constructor with a single parameter of the same type as the field.
		Factory factory = new Launcher().getFactory();
		CtRecord record = factory.createRecord()
			.<CtRecord>setSimpleName("SingleParameterRecord")
			.<CtRecord>addModifier(ModifierKind.PUBLIC)
			.addRecordComponent(
				factory.createRecordComponent()
					.<CtRecordComponent>setType(factory.Type().integerPrimitiveType())
					.setSimpleName("value")
			)
			.createCanonicalConstructorIfMissing();

		assertThat(record).getFields().hasSize(1);

		CtField<?> field = getFirst(record.getFields());
		assertThat(field).isImplicit();
		assertThat(field).getSimpleName().isEqualTo("value");

		CtMethod<?> method = getFirst(record.getMethods());
		assertThat(method).isImplicit();
		assertThat(method).getSimpleName().isEqualTo("value");

		CtConstructor<?> constructor = getFirst(record.getConstructors());
		assertThat(constructor).isImplicit();
		assertThat(constructor).getParameters().hasSize(1);
		assertThat(field.getType()).isEqualTo(constructor.getParameters().get(0).getType());
	}

	@Test
	public void testMultipleParameterRecord() {
		// contract: a record with 2 parameters should be created as a class with 2 fields and a constructor with 2 parameters
		Factory factory = new Launcher().getFactory();
		CtRecord record = factory.createRecord()
			.<CtRecord>setSimpleName("MultiParameter")
			.<CtRecord>addModifier(ModifierKind.PUBLIC)
			.addRecordComponent(
				factory.createRecordComponent()
					.<CtRecordComponent>setType(factory.Type().integerPrimitiveType())
					.setSimpleName("first")
			)
			.addRecordComponent(
				factory.createRecordComponent()
					.<CtRecordComponent>setType(factory.Type().floatPrimitiveType())
					.setSimpleName("second")
			)
			.createCanonicalConstructorIfMissing();

		assertEquals("public record MultiParameter(int first, float second) {}", record.toString());

		// Make them explicit so we can print them (but assert they were implicit initially)
		assertThat(record).getFields().allSatisfy(CtElement::isImplicit);
		record.getFields().forEach(it -> it.accept(new CtScanner() {
			@Override
			protected void enter(CtElement e) {
				e.setImplicit(false);
			}
		}));
		record.getFields().forEach(f->f.getExtendedModifiers().forEach(em -> em.setImplicit(false)));

		// test fields
		assertEquals(
			Arrays.asList(
				"private final int first;",
				"private final float second;"
			),
			record.getFields().stream().map(String::valueOf).collect(Collectors.toList())
		);

		// Make them explicit so we can print them (but assert they were implicit initially)
		assertThat(record).getMethods().allSatisfy(CtElement::isImplicit);
		record.getMethods().forEach(it -> it.accept(new CtScanner() {
			@Override
			protected void enter(CtElement e) {
				e.setImplicit(false);
			}
		}));

		// test methods
		assertEquals(
			List.of(
				"""
					int first() {
					    return this.first;
					}""",
				"""
					float second() {
					    return this.second;
					}"""
			),
			record.getMethods().stream()
				.map(String::valueOf)
				.map(s -> s.replaceAll("\\R", "\n")) // fix newlines on Windows
				.collect(Collectors.toList())
		);


		// Make them explicit so we can print them (but assert they were implicit initially)
		assertThat(record).getConstructors().allSatisfy(CtElement::isImplicit);
		record.getConstructors().forEach(it -> it.accept(new CtScanner() {
			@Override
			protected void enter(CtElement e) {
				e.setImplicit(false);
			}
		}));

		// test canonical constructor
		assertEquals(
			List.of(
			"""
				MultiParameter(int first, float second) {
				    this.first = first;
				    this.second = second;
				}"""
			),
			record.getConstructors().stream()
				.map(String::valueOf)
				.map(s -> s.replaceAll("\\R", "\n")) // fix newlines on Windows
				.collect(Collectors.toList())
		);
	}

	@Test
	void testMultipleConstructors() {
		// contract: we can have an explicit constructor delegating to the implicit canonical constructor
		// Arrange
		Factory factory = new Launcher().getFactory();
		CtRecord firstRecord = factory.createRecord()
			.<CtRecord>setSimpleName("MultipleConstructors")
			.<CtRecord>addModifier(ModifierKind.PUBLIC)
			.addRecordComponent(
				factory.createRecordComponent()
					.<CtRecordComponent>setType(factory.Type().integerPrimitiveType())
					.setSimpleName("i")
			)
			.createCanonicalConstructorIfMissing()
			.addConstructor(
				factory.createConstructor()
					.addParameter(
						factory.createParameter()
							.<CtParameter<?>>setType(factory.Type().stringType())
							.setSimpleName("s")
					)
			);

		// Act
		Set<CtConstructor<Object>> recordConstructors = firstRecord.getConstructors();
		assertEquals(2, recordConstructors.size());
		CtConstructor<?>[] sortedConstructors = recordConstructors.toArray(CtConstructor[]::new);

		// Sorting the constructors with implicit ones last
		Arrays.sort(sortedConstructors, Comparator.comparing(CtConstructor::isImplicit));

		// Assert
		assertFalse(sortedConstructors[0].isImplicit());
		assertEquals("s", sortedConstructors[0].getParameters().get(0).getSimpleName());
		assertFalse(sortedConstructors[0].isCompactConstructor());

		assertTrue(sortedConstructors[1].isImplicit());
		assertEquals("i", sortedConstructors[1].getParameters().get(0).getSimpleName());
		assertFalse(sortedConstructors[1].isCompactConstructor());
	}

	private <T> T getFirst(Collection<T> collection) {
		return collection.iterator().next();
	}
}
