package spoon.template;

import java.lang.reflect.Field;

import spoon.SpoonException;
import spoon.processing.FactoryAccessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.support.template.Parameters;

/**
 * handles the well-formedness and helper methods of templates
 */
public abstract class AbstractTemplate<T extends CtElement> implements Template<T> {

	/**
	 * verifies whether there is at least one template parameter.
	 */
	public boolean isWellFormed() {
		return Parameters.getAllTemplateParameterFields(this.getClass()).size() > 0;
	}

	/**
	 * verifies whether all template parameters are filled.
	 */
	public boolean isValid() {
		try {
			for (Field f : Parameters.getAllTemplateParameterFields(this.getClass())) {
				if (f.get(this) == null) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			throw new SpoonException(e);
		}
	}

	/**
	 * returns a Spoon factory object from the first template parameter that contains one
	 */
	public Factory getFactory() {
		try {
			for (Field f : Parameters.getAllTemplateParameterFields(this.getClass())) {
				if (f.get(this) != null && f.get(this) instanceof FactoryAccessor) {
					return ((FactoryAccessor) f.get(this)).getFactory();
				}
			}
		} catch (Exception e) {
			throw new SpoonException(e);
		}
		throw new TemplateException("no factory found in this template");
	}

}
