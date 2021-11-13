package spoon.test.record;

import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Collection;
import javax.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtRecord;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

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
		assertEquals(2, head(records).getFields().size());
		assertEquals(2, head(records).getMethods().size());
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
		assertTrue(records.iterator().next().getConstructors().iterator().next().isCompactConstructor());
		String correctConstructor =
			"Rational {" + lineSeparator()
		+	"    int gcd = records.Rational.gcd(num, denom);" + lineSeparator()
		+	"    num /= gcd;" + lineSeparator()
		+	"    denom /= gcd;" + lineSeparator()
		+	"}";
		assertEquals(correctConstructor, head(head(records).getConstructors()).toString());
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

	private <T> T head(Collection<T> collection) {
		return collection.iterator().next();
	}

}
