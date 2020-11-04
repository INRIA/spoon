package spoon.architecture.runner;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assume.assumeNotNull;
import org.junit.jupiter.api.Test;
import spoon.reflect.CtModel;

public class ModelBuilderTest {


	@Test
	public void testCaseInsensitivity() {
		// contract: the modelBuilder lookup is case insensitive.
		ModelBuilder builder = new ModelBuilder();
		builder.insertInputPath("tAcOs", "foo");
		assertThat(builder.getModelWithIdentifier("tAcOs")).isNotNull();
		assertThat(builder.getModelWithIdentifier("TACOS")).isNotNull();
		assertThat(builder.getModelWithIdentifier("tacos")).isNotNull();
	}
	@Test
	public void testEmptyBuilder() {
		// contract: any lookup in an empty builder returns null
		ModelBuilder builder = new ModelBuilder();
		assertThat(builder.getModelWithIdentifier("foo")).isNull();
	}
	@Test
	public void testInsertBuildModel() {
		// contract adding a model does not modify it
		ModelBuilder builder = new ModelBuilder();
		builder.insertInputPath("test", "src/test/java/spoon/architecture/runner/ModelBuilderTest.java");
		CtModel model = builder.getModelWithIdentifier("test");
		// if the model is null we abort here
		assumeNotNull(model);
		builder.insertInputPath("test2", model);
		// in all cases we must have the same model
		assertThat(model).isEqualTo(builder.getModelWithIdentifier("test"));
		assertThat(model).isEqualTo(builder.getModelWithIdentifier("test2"));
		assertThat(builder.getModelWithIdentifier("test2")).isEqualTo(builder.getModelWithIdentifier("test"));
	}
	@Test
	public void testSetJavaVersion() {
		// setting the java version changes the version for model building
		ModelBuilder builder = new ModelBuilder(9);
		builder.insertInputPath("test", "src/test/java/spoon/architecture/runner/ModelBuilderTest.java");
		CtModel model = builder.getModelWithIdentifier("test");
		assertThat(model.getUnnamedModule().getFactory().getEnvironment().getComplianceLevel()).isEqualTo(9);
	}
}
