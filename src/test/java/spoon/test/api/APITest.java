package spoon.test.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.declaration.CtSimpleType;
import spoon.support.JavaOutputProcessor;

public class APITest {

	@Test
	public void testOverrideOutputWriter() throws Exception {
		// this test that we can correctly set the Java output processor
		final List<Object> l = new ArrayList<>();
		Launcher spoon = new Launcher() {
			@Override
			public JavaOutputProcessor createOutputWriter(File sourceOutputDir) {				
				return new JavaOutputProcessor() { 
					@Override
					public void process(CtSimpleType<?> e) {
						l.add(e);
					}
					@Override
					public void init() {
						// we do nothing
					}

				};
			}
			
		};
		spoon.setArgs(new String[] {"-i", "src/test/resources/spoon/test/api/"});
		spoon.run();
		Assert.assertEquals(2, l.size());
	}
}
