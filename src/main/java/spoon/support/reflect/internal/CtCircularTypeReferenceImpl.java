package spoon.support.reflect.internal;

import spoon.reflect.internal.CtCircularTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.reference.CtTypeParameterReferenceImpl;

public class CtCircularTypeReferenceImpl extends CtTypeParameterReferenceImpl implements CtCircularTypeReference {
	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtCircularTypeReference(this);
	}
}
