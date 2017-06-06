package spoon.test.template.testclasses;

import spoon.reflect.code.CtBlock;
import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.StatementTemplate;
import spoon.template.TemplateParameter;

public class SubstituteRootTemplate extends StatementTemplate {

	@Override
	public void statement() throws Throwable {
		block.S();
	}
	
	@Parameter
	TemplateParameter<Void> block;

	@Local
	public SubstituteRootTemplate(CtBlock<Void> block) {
		this.block = block;
	}
	
	@Local
	void sampleBlock() {
		String s="Spoon is cool!";
	}
}