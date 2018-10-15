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
package spoon.test.staticFieldAccess2;

import static spoon.testing.utils.ModelUtils.canBeBuilt;

import static org.junit.Assert.*;
import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.test.staticFieldAccess2.testclasses.AmbiguousImplicitFieldReference;
import spoon.test.staticFieldAccess2.testclasses.ChildOfGenericsWithAmbiguousStaticField;
import spoon.test.staticFieldAccess2.testclasses.ImplicitFieldReference;
import spoon.test.staticFieldAccess2.testclasses.ImplicitStaticFieldReference;

public class ImplicitStaticFieldReferenceTest
{
	private static final boolean expectImplicit = false;

    @Test
    public void testImplicitStaticFieldReference() {
    	Launcher launcher = checkFile(false, "ImplicitStaticFieldReference.java");
    	if(expectImplicit) {
        	CtClass<?> cls = launcher.getFactory().Class().get(ImplicitStaticFieldReference.class);
        	assertEquals("return ImplicitStaticFieldReference", cls.getMethod("reader").getBody().getStatements().get(0).toString());
        	assertEquals("ImplicitStaticFieldReference = value", cls.getMethodsByName("writer").get(0).getBody().getStatements().get(0).toString());
        	assertEquals("reader()", cls.getMethodsByName("testLocalMethodInvocations").get(0).getBody().getStatements().get(0).toString());
        	assertEquals("longWriter(7)", cls.getMethodsByName("testLocalMethodInvocations").get(0).getBody().getStatements().get(1).toString());
    	}
    }

    @Test
    public void testImplicitStaticFieldReferenceAutoImport() {
    	Launcher launcher = checkFile(true, "ImplicitStaticFieldReference.java");
    	if(expectImplicit) {
        	CtClass<?> cls = launcher.getFactory().Class().get(ImplicitStaticFieldReference.class);
        	assertEquals("return ImplicitStaticFieldReference", cls.getMethod("reader").getBody().getStatements().get(0).toString());
        	assertEquals("ImplicitStaticFieldReference = value", cls.getMethodsByName("writer").get(0).getBody().getStatements().get(0).toString());
        	assertEquals("reader()", cls.getMethodsByName("testLocalMethodInvocations").get(0).getBody().getStatements().get(0).toString());
        	assertEquals("longWriter(7)", cls.getMethodsByName("testLocalMethodInvocations").get(0).getBody().getStatements().get(1).toString());
    	}
    }

    @Test
    public void testImplicitFieldReference() {
    	Launcher launcher = checkFile(false, "ImplicitFieldReference.java");
    	if(expectImplicit) {
        	CtClass<?> cls = launcher.getFactory().Class().get(ImplicitFieldReference.class);
        	assertEquals("return memberField", cls.getMethod("getMemberField").getBody().getStatements().get(0).toString());
        	assertEquals("memberField = p_memberField", cls.getMethodsByName("setMemberField").get(0).getBody().getStatements().get(0).toString());
//        	assertEquals("this.memberField = memberField", cls.getMethodsByName("setMemberField2").get(0).getBody().getStatements().get(0).toString());
        	assertEquals("getMemberField()", cls.getMethodsByName("testLocalMethodInvocations").get(0).getBody().getStatements().get(0).toString());
    	}
    }
    @Test
    public void testImplicitFieldReferenceAutoImport() {
    	Launcher launcher = checkFile(true, "ImplicitFieldReference.java");
    	if(expectImplicit) {
        	CtClass<?> cls = launcher.getFactory().Class().get(ImplicitFieldReference.class);
        	assertEquals("return memberField", cls.getMethod("getMemberField").getBody().getStatements().get(0).toString());
        	assertEquals("memberField = p_memberField", cls.getMethodsByName("setMemberField").get(0).getBody().getStatements().get(0).toString());
//        	assertEquals("this.memberField = memberField", cls.getMethodsByName("setMemberField2").get(0).getBody().getStatements().get(0).toString());
        	assertEquals("getMemberField()", cls.getMethodsByName("testLocalMethodInvocations").get(0).getBody().getStatements().get(0).toString());
    	}
    }
    
    @Test
    public void testAmbiguousImplicitFieldReference() {
    	Launcher launcher = checkFile(false, "AmbiguousImplicitFieldReference.java");
    	if(expectImplicit) {
        	CtClass<?> cls = launcher.getFactory().Class().get(AmbiguousImplicitFieldReference.class);
        	assertEquals("return memberField", cls.getMethod("getMemberField").getBody().getStatements().get(0).toString());
        	assertEquals("memberField = p_memberField", cls.getMethodsByName("setMemberField").get(0).getBody().getStatements().get(0).toString());
//        	assertEquals("this.memberField = memberField", cls.getMethodsByName("setMemberField2").get(0).getBody().getStatements().get(0).toString());
        	assertEquals("getMemberField()", cls.getMethodsByName("testLocalMethodInvocations").get(0).getBody().getStatements().get(0).toString());
    	}
    }
    @Test
    public void testAmbiguousImplicitFieldReferenceAutoImport() {
    	Launcher launcher = checkFile(true, "AmbiguousImplicitFieldReference.java");
    	if(expectImplicit) {
        	CtClass<?> cls = launcher.getFactory().Class().get(AmbiguousImplicitFieldReference.class);
        	assertEquals("return memberField", cls.getMethod("getMemberField").getBody().getStatements().get(0).toString());
        	assertEquals("memberField = p_memberField", cls.getMethodsByName("setMemberField").get(0).getBody().getStatements().get(0).toString());
//        	assertEquals("this.memberField = memberField", cls.getMethodsByName("setMemberField2").get(0).getBody().getStatements().get(0).toString());
        	assertEquals("getMemberField()", cls.getMethodsByName("testLocalMethodInvocations").get(0).getBody().getStatements().get(0).toString());
    	}
    }

    @Test
    public void testImplicitStaticClassAccess() {
    	Launcher launcher = checkFile(false, "ImplicitStaticClassAccess.java");
    }
    @Test
    public void testImplicitStaticClassAccessAutoImport() {
    	Launcher launcher = checkFile(true, "ImplicitStaticClassAccess.java");
    }
    @Test
    public void testGenericsWithAmbiguousStaticField() {
    	Launcher launcher = checkFile(false, "GenericsWithAmbiguousStaticField.java");
    }
    @Test
    public void testGenericsWithAmbiguousStaticFieldAutoImport() {
    	Launcher launcher = checkFile(true, "GenericsWithAmbiguousStaticField.java");
    }
    
    @Test
    public void testChildOfGenericsWithAmbiguousStaticField() {
    	Launcher launcher = checkFile(false, "ChildOfGenericsWithAmbiguousStaticField.java");
    }
    
    @Test
    public void testChildOfGenericsWithAmbiguousStaticFieldAutoImport() {
    	Launcher launcher = checkFile(true, "ChildOfGenericsWithAmbiguousStaticField.java");
    	if(expectImplicit) {
        	CtClass<?> cls = launcher.getFactory().Class().get(ChildOfGenericsWithAmbiguousStaticField.class);
        	//The toString of the method does not have a context of class, so it different result 
//        	assertEquals("spoon.test.staticFieldAccess2.testclasses.GenericsWithAmbiguousStaticField.<V, C>genericMethod()", cls.getMethod("m1").getBody().getStatements().get(0).toString());
        	assertTrue(cls.toString().contains("spoon.test.staticFieldAccess2.testclasses.GenericsWithAmbiguousStaticField.<V, C>genericMethod()"));
        	assertEquals("genericMethod()", cls.getMethod("m1").getBody().getStatements().get(1).toString());
    	}
    }

    @Test
    public void testGenericsWithAmbiguousMemberField() {
    	Launcher launcher = checkFile(false, "GenericsWithAmbiguousMemberField.java");
    }
    @Test
    public void testGenericsWithAmbiguousMemberFieldAutoImport() {
    	Launcher launcher = checkFile(true, "GenericsWithAmbiguousMemberField.java");
    }

    @Test
    public void testAnnotationInChildWithConstants() {
    	Launcher launcher = checkFile(false, "ChildOfConstants.java", "Constants.java");
    }

    @Test
    public void testAnnotationInChildWithConstantsAutoImport() {
    	Launcher launcher = checkFile(true, "ChildOfConstants.java", "Constants.java");
    }
    
    private static Launcher checkFile(boolean autoImports, String... fileName) {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(autoImports);

		String pckg = "spoon/test/staticFieldAccess2/testclasses/";
		for (String fn : fileName)
		{
			launcher.addInputResource("src/test/java/"+pckg+fn);
		}
		String targetDir = "./target/spooned"+(autoImports?"-autoImports":"");
		launcher.setSourceOutputDirectory(targetDir);
		launcher.buildModel();
		launcher.prettyprint();
		for (String fn : fileName)
		{
			canBeBuilt(targetDir+"/"+pckg+fn, 8);
		}
		return launcher;
    }
}
