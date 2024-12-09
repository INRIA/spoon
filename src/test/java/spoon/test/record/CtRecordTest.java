package spoon.test.record;

import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.testing.assertions.SpoonAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

import org.assertj.core.api.InstanceOfAssertFactory;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtRecord;
import spoon.reflect.declaration.CtRecordComponent;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.testing.assertions.SpoonAssertions;
import spoon.testing.utils.ModelTest;

public class CtRecordTest {

	@Test
	void testEmptyRecord() {
		// contract: empty record should be created with no fields and no methods
		String code = "src/test/resources/records/EmptyRecord.java";
		CtModel model = createModelFromPath(code);
		Collection<CtType<?>> records = model.getAllTypes();
		assertEquals(1, records.size());
		assertEquals(0, head(records).getFields().size());
		assertEquals(0, head(records).getMethods().size());
	}

	@Test
	public void testSingleParameterRecord() {
		// contract: a record with a single parameter should be created as a class
		// with a single field and a constructor with a single parameter of the same type as the field.
		String code = "src/test/resources/records/SingleParameter.java";
		CtModel model = createModelFromPath(code);
		assertEquals(1, model.getAllTypes().size());
		Collection<CtRecord> records = model.getElements(new TypeFilter<>(CtRecord.class));
		assertEquals(1, head(records).getFields().size());
		assertEquals(1, head(records).getMethods().size());

		CtField<?> field = head(head(records).getFields());
		assertTrue(field.isImplicit());
		assertEquals("value", field.getSimpleName());

		CtMethod<?> method = head(head(records).getMethods());
		assertTrue(method.isImplicit());
		assertEquals("value", method.getSimpleName());

		CtConstructor<?> constructor = head(head(records).getConstructors());
		assertTrue(constructor.isImplicit());
		assertEquals(1, constructor.getParameters().size());
		assertEquals(field.getType(), head(constructor.getParameters()).getType());
	}

	@Test
	public void testMultipleParameterRecord() {
		// contract: a record with 2 parameters should be created as a class with 2 fields and a constructor with 2 parameters
		String code = "src/test/resources/records/MultiParameter.java";
		CtModel model = createModelFromPath(code);
		assertEquals(1, model.getAllTypes().size());
		Collection<CtRecord> records = model.getElements(new TypeFilter<>(CtRecord.class));

		assertEquals(1, records.size());
		assertEquals("public record MultiParameter(int first, float second) {}", head(records).toString());

		// test fields
		assertEquals(
				Arrays.asList(
						"private final int first;",
						"private final float second;"
				),
				head(records).getFields().stream().map(String::valueOf).collect(Collectors.toList())
		);

		// Make them explicit so we can print them (but assert they were implicit initially)
		assertThat(head(records)).getMethods().allSatisfy(CtElement::isImplicit);
		head(records).getMethods().forEach(it -> it.accept(new CtScanner() {
			@Override
			protected void enter(CtElement e) {
				e.setImplicit(false);
			}
		}));

		// test methods
		assertEquals(
				Arrays.asList(
						"int first() {\n" +
					    "    return this.first;\n" +
					    "}",
						"float second() {\n" +
					    "    return this.second;\n" +
					    "}"
				),
				head(records).getMethods().stream()
						.map(String::valueOf)
						.map(s -> s.replaceAll("\\R", "\n")) // fix newlines on windows
						.collect(Collectors.toList())
		);
	}

	@Test
	public void testExplicitParameterAccessor() {
		// contract: no implicit parameter accessor are created if an explicit accessor is defined
		String code = "src/test/resources/records/ExplicitAccessor.java";
		CtModel model = createModelFromPath(code);
		assertEquals(1, model.getAllTypes().size());
		Collection<CtRecord> records = model.getElements(new TypeFilter<>(CtRecord.class));
		assertFalse(head(head(records).getMethods()).isImplicit());
		assertEquals(1, head(records).getMethods().size());
	}

	@Test
	void testNoClasspathAnnotations() {
		// contract: annotations that are no in the classpath should be ignored for record fields, methods and parameters.
		String code = "src/test/resources/records/NoClasspathAnnotations.java";
		CtModel model = createModelFromPath(code);
		assertEquals(1, model.getAllTypes().size());
		Collection<CtRecord> records = model.getElements(new TypeFilter<>(CtRecord.class));
		assertEquals(2, head(head(records).getRecordComponents()).getAnnotations().size());
		assertEquals(0, head(head(records).getFields()).getAnnotations().size());
		assertEquals(0, head(head(records).getMethods()).getAnnotations().size());
		assertEquals(0, head(head(head(records).getConstructors()).getParameters()).getAnnotations().size());
	}

	@Test
	void testCompactConstructor() {
		// contract: compact constructor is printed without parameters.
		String code = "src/test/resources/records/CompactConstructor.java";
		CtModel model = createModelFromPath(code);
		assertEquals(1, model.getAllTypes().size());
		Collection<CtRecord> records = model.getElements(new TypeFilter<>(CtRecord.class));
		Set<CtConstructor<Object>> constructors = head(records).getConstructors();
		assertEquals(1, constructors.size());
		CtConstructor<Object> constructor = head(constructors);
		assertTrue(constructor.isCompactConstructor());
		assertFalse(constructor.isImplicit());
		String correctConstructor =
			"public Rational {" + lineSeparator()
		+	"    int gcd = records.Rational.gcd(num, denom);" + lineSeparator()
		+	"    num /= gcd;" + lineSeparator()
		+	"    denom /= gcd;" + lineSeparator()
		+	"}";
		assertEquals(correctConstructor, constructor.toString());
	}

	@Test
	void testCompactConstructor2() {
		// contract: compact constructor is printed correctly for a compilable Java file (issue 4377).
		String code = "src/test/resources/records/CompactConstructor2.java";
		CtModel model = createModelFromPath(code);
		assertEquals(1, model.getAllTypes().size());
		Collection<CtRecord> records = model.getElements(new TypeFilter<>(CtRecord.class));
		Set<CtConstructor<Object>> constructors = head(records).getConstructors();
		assertEquals(1, constructors.size());
		CtConstructor<Object> constructor = head(constructors);
		assertTrue(constructor.isCompactConstructor());
		assertFalse(constructor.isImplicit());
		String correctConstructor =
			"public CompactConstructor2 {" + lineSeparator()
		+	"    int gcd = records.CompactConstructor2.gcd(num, denom);" + lineSeparator()
		+	"    num /= gcd;" + lineSeparator()
		+	"    denom /= gcd;" + lineSeparator()
		+	"}";
		assertEquals(correctConstructor, constructor.toString());
	}

	@Test
	void testDeriveAnnotation() {
		// contract: annotations are inherited from the record component.
		String code = "src/test/resources/records/DeriveAnnotations.java";
		CtModel model = createModelFromPath(code);
		Collection<CtRecord> records = model.getElements(new TypeFilter<>(CtRecord.class));
		CtConstructor<?> constructor = head(head(records).getConstructors());
		assertTrue(head(constructor.getParameters()).hasAnnotation(NotNull.class));
		assertTrue(head(head(records).getFields()).hasAnnotation(NotNull.class));
		assertTrue(head(head(records).getMethods()).hasAnnotation(NotNull.class));
	}
	@Test
	void testPartiallyAnnotations() {
		// contract: annotations are inherited from the record component if applicable.
		String code = "src/test/resources/records/PartiallyAnnotations.java";
		CtModel model = createModelFromPath(code);
		Collection<CtRecord> records = model.getElements(new TypeFilter<>(CtRecord.class));
		CtConstructor<?> constructor = head(head(records).getConstructors());
		assertFalse(head(constructor.getParameters()).hasAnnotation(Override.class));
		assertFalse(head(head(records).getFields()).hasAnnotation(Override.class));
		assertTrue(head(head(records).getMethods()).hasAnnotation(Override.class));
	}

	@Test
	void printRecordWithInterface() {
		// a record with an interface should be printed like a class with an interface
		String code = "src/test/resources/records/RecordWithInterface.java";
		CtModel model = createModelFromPath(code);
		Collection<CtRecord> records = model.getElements(new TypeFilter<>(CtRecord.class));
		CtRecord record = head(records);
		assertTrue(record.toString().contains("implements records.Supplier"));
	}
	private CtModel createModelFromPath(String code) {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(16);
		launcher.addInputResource(code);
		CtModel model = launcher.buildModel();
		return model;
	}

	@Test
	void testGenericTypeParametersArePrintedBeforeTheFunctionParameters() {
		// contract: a record with generic type arguments should be printed correctly
		String code = "src/test/resources/records/GenericRecord.java";
		CtModel model = createModelFromPath(code);
		Collection<CtRecord> records = model.getElements(new TypeFilter<>(CtRecord.class));
		CtRecord record = head(records);
		assertEquals("public record GenericRecord<T>(T a, T b) {}", record.toString());
	}

	@Test
	void testBuildRecordModelWithStaticInitializer() {
		// contract: a record can have static initializers
		String code = "src/test/resources/records/WithStaticInitializer.java";
		CtModel model = assertDoesNotThrow(() -> createModelFromPath(code));
		List<CtAnonymousExecutable> execs = model.getElements(new TypeFilter<>(CtAnonymousExecutable.class));
		assertThat(execs).hasSize(2);
	}

	@ModelTest(value = "./src/test/resources/records/MultipleConstructors.java", complianceLevel = 16)
	void testMultipleConstructors(Factory factory) {
		// contract: we can have an explicit constructor delegating to the implicit canonical constructor
		// Arrange
		CtModel model = factory.getModel();
		int totalTypes = model.getAllTypes().size();
		assertEquals(1, totalTypes);
		CtRecord firstRecord = model.getElements(new TypeFilter<>(CtRecord.class)).get(0);

		// Act
		Set<CtConstructor<Object>> recordConstructors = firstRecord.getConstructors();
		assertEquals(2, recordConstructors.size());
		CtConstructor<?>[] sortedConstructors = recordConstructors.toArray(CtConstructor[]::new);

		// Sorting the constructors with implicit ones last
		Arrays.sort(sortedConstructors, Comparator.comparing(CtConstructor::isImplicit));

		// Assert
		assertFalse(sortedConstructors[0].isImplicit());
		assertEquals(sortedConstructors[0].getParameters().get(0).getSimpleName(), "s");
		assertFalse(sortedConstructors[0].isCompactConstructor());

		assertTrue(sortedConstructors[1].isImplicit());
		assertEquals(sortedConstructors[1].getParameters().get(0).getSimpleName(), "i");
		assertFalse(sortedConstructors[1].isCompactConstructor());
	}

	@ModelTest(value = "./src/test/resources/records/NonCompactCanonicalConstructor.java", complianceLevel = 16)
	void testNonCompactCanonicalConstructor(Factory factory) {
		// contract: we can have an explicit canonical constructor replacing the implicit canonical constructor
		// Arrange
		CtModel model = factory.getModel();
		int totalTypes = model.getAllTypes().size();
		assertEquals(1, totalTypes);
		CtRecord firstRecord = model.getElements(new TypeFilter<>(CtRecord.class)).get(0);

		// Act
		List<CtConstructor<Object>> recordConstructors = firstRecord.getElements(new TypeFilter<>(CtConstructor.class));
		assertEquals(1, recordConstructors.size());
		CtConstructor<?> constructor = head(recordConstructors);

		// Assert
		assertFalse(constructor.isImplicit());
		assertFalse(constructor.isCompactConstructor());
		assertEquals(constructor.getParameters().get(0).getSimpleName(), "x");
	}

	@ModelTest(value = "./src/test/resources/records/GenericRecord.java", complianceLevel = 16)
	void testProperReturnInRecordAccessor(Factory factory) {
		// contract: the return statement in the accessor method should return a field read expression to the correct
		// field
		CtRecord record = head(factory.getModel().getElements(new TypeFilter<>(CtRecord.class)));

		assertThat(record.getRecordComponents()).isNotEmpty();
		for (CtRecordComponent component : record.getRecordComponents()) {
			CtMethod<?> method = component.toMethod();

			assertThat(method.getBody().<CtStatement>getLastStatement())
				.asInstanceOf(new InstanceOfAssertFactory<>(CtReturn.class, SpoonAssertions::assertThat))
				.getReturnedExpression()
				.self()
				.asInstanceOf(new InstanceOfAssertFactory<>(CtFieldRead.class, SpoonAssertions::assertThat))
				.getVariable()
				.getDeclaringType()
				.getSimpleName()
				.isEqualTo(record.getSimpleName());
		}
	}

	@Test
	void testRecordWithStaticField() {
		// contract: Static fields in records do not cause crashes
		CtClass<?> parsed = Launcher.parseClass("""
			public record User(int id, String name) {
			  private static String ADMIN_NAME = "admin";
			}
			""");
		assertThat(parsed).isInstanceOf(CtRecord.class);
		assertThat(parsed).getFields().hasSize(3);
		assertThat(parsed.getFields()).anySatisfy(it -> assertThat(it.getSimpleName()).isEqualTo("id"));
		assertThat(parsed.getFields()).anySatisfy(it -> assertThat(it.getSimpleName()).isEqualTo("name"));
		assertThat(parsed.getFields()).anySatisfy(it -> assertThat(it.getSimpleName()).isEqualTo("ADMIN_NAME"));
	}

	private <T> T head(Collection<T> collection) {
		return collection.iterator().next();
	}
}
