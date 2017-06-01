package spoon.test.template.testclasses;

import spoon.reflect.code.CtBlock;
import spoon.template.ExtensionTemplate;
import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.TemplateParameter;

public class ArrayAccessTemplate extends ExtensionTemplate {

	public void method() throws Throwable {
		blocks[0].S();
		blocks[1].S();
	}
	
	public void method2() throws Throwable {
		System.out.println(strings[1]);
	}

	@Parameter
	TemplateParameter<CtBlock<?>>[] blocks;

	@Parameter
	String[] strings;

	@Local
	public ArrayAccessTemplate(TemplateParameter<CtBlock<?>>[] blocks) {
		this.blocks = blocks;
		strings = new String[]{"first","second"};
	}
	
	@Local
	void sampleBlocks() {
		{
			int i=0;
		}
		{
			String s="Spoon is cool!";
		}
	}
}
