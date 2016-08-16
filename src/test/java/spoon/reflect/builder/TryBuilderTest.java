/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.reflect.builder;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtTry;
import spoon.reflect.factory.Factory;

public class TryBuilderTest {

	@Test
	public void test() {
		Factory factory = new Launcher().getFactory();
		Builder x = factory.Builder();

CtTry aTry = x.Try()
		.inBody(x.Increment(x.Literal(1)))
		.createCatch()
			.parameter("e", IllegalArgumentException.class)
			.inBody(x.Increment(x.Literal(1)))
			.close()
		.createCatch()
			.parameter("e", Exception.class)
			.close()
		.inFinally(x.Decrement(x.Literal(1)))
.build();
Assert.assertEquals("try {\n"
		+ "    1++;\n"
		+ "} catch (java.lang.IllegalArgumentException e) {\n"
		+ "    1++;\n"
		+ "} catch (java.lang.Exception e) {\n"
		+ "} finally {\n"
		+ "    1--;\n"
		+ "}", aTry.toString());

	}

}