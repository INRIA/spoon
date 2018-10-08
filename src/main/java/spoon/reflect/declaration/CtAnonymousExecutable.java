/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.reflect.declaration;


import java.util.List;
import java.util.Set;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.UnsettableProperty;




/**
 * This element defines an anonymous executable block declaration in a class.
 *
 * @see spoon.reflect.declaration.CtClass
 */
public interface CtAnonymousExecutable extends CtExecutable<Void>, CtTypeMember {
	@Override
	CtAnonymousExecutable clone();

	@Override
	@UnsettableProperty
	CtAnonymousExecutable setSimpleName(String simpleName);

	@Override
	@UnsettableProperty
	CtAnonymousExecutable setThrownTypes(Set<CtTypeReference<? extends Throwable>> thrownTypes);

	@Override
	@UnsettableProperty
	CtAnonymousExecutable setParameters(List<CtParameter<?>> parameters);

	@Override
	@UnsettableProperty
	CtAnonymousExecutable setType(CtTypeReference<Void> type);

	@Override
	@UnsettableProperty
	CtAnonymousExecutable addParameter(CtParameter<?> parameter);

	@Override
	@UnsettableProperty
	CtAnonymousExecutable addThrownType(CtTypeReference<? extends Throwable> throwType);

}
