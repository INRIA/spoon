/**
 * Copyright (C) 2006-2016 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
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
