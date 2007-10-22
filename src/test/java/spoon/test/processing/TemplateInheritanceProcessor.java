package spoon.test.processing;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.template.Substitution;
import spoon.test.template.SubTemplate;
import spoon.test.template.SuperTemplate;
import spoon.test.templateinheritance.SubClass;
import spoon.test.templateinheritance.SuperClass;

public class TemplateInheritanceProcessor extends AbstractProcessor<CtClass<?>> {

	public void process(CtClass<?> clazz) {
		if(clazz.getSimpleName().equals("")) return;
		System.out.println(clazz.getQualifiedName());
		if(clazz.getActualClass()==SuperClass.class) {
			Substitution.insertAll(clazz, new SuperTemplate());
			CtClass<?> c=getFactory().Class().get(SubClass.class);
			Substitution.insertAll(c, new SubTemplate());
		}
	}

}
