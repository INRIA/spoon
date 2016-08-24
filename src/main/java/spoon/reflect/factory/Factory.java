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
package spoon.reflect.factory;

import spoon.compiler.Environment;
import spoon.reflect.CtModel;

/**
 * Provides the sub-factories required by Spoon.
 *
 * Most classes provides a method getFactory() that returns the current factory.
 *
 * Otherwise FactoryImpl is a default implementation.
 */
public interface Factory {

	/** returns the Spoon model that has been built with this factory or one of its subfactories */
	CtModel getModel();

	CoreFactory Core(); // used 238 times

	TypeFactory Type(); // used 107 times

	EnumFactory Enum();

	Environment getEnvironment(); // used 71 times

	PackageFactory Package(); // used 30 times

	CodeFactory Code(); // used 28 times

	ClassFactory Class(); // used 27 times

	FieldFactory Field(); // used 9 times

	ExecutableFactory Executable(); // used 8 times

	CompilationUnitFactory CompilationUnit(); // used 7 times

	InterfaceFactory Interface();

	MethodFactory Method(); // used 5 times

	AnnotationFactory Annotation(); // used 4 times

	EvalFactory Eval(); // used 4 times

	ConstructorFactory Constructor(); // used 3 times
}
