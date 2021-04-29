package spoon.support.reflect.internal;

import spoon.reflect.ModelElementContainerDefaultCapacities;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.path.CtRole;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

public class CtFormalTypeDeclarerHelper {

	private CtFormalTypeDeclarerHelper() {
	}

	public static <C extends CtFormalTypeDeclarer> List<CtTypeParameter> addFormalCtTypeParameterAt(C receiver, List<CtTypeParameter> formalCtTypeParameters, int position, CtTypeParameter formalTypeParameter) {
		if (formalTypeParameter == null) {
			return formalCtTypeParameters;
		}
		if (formalCtTypeParameters == CtElementImpl.<CtTypeParameter>emptyList()) {
			formalCtTypeParameters = new ArrayList<>(ModelElementContainerDefaultCapacities.TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		formalTypeParameter.setParent(receiver);
		receiver.getFactory().getEnvironment().getModelChangeListener().onListAdd(receiver, CtRole.TYPE_PARAMETER, formalCtTypeParameters, formalTypeParameter);
		formalCtTypeParameters.add(position, formalTypeParameter);
		return formalCtTypeParameters;
	}
}
