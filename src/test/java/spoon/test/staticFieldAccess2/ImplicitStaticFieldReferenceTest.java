package spoon.test.staticFieldAccess2;

import static spoon.testing.utils.ModelUtils.canBeBuilt;

import static org.junit.Assert.*;
import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

public class ImplicitStaticFieldReferenceTest
{
	private static final boolean expectImplicit = false;

    @Test
    public void testImplicitStaticFieldReference() throws Exception {
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
    public void testImplicitStaticFieldReferenceAutoImport() throws Exception {
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
    public void testImplicitFieldReference() throws Exception {
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
    public void testImplicitFieldReferenceAutoImport() throws Exception {
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
    public void testAmbiguousImplicitFieldReference() throws Exception {
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
    public void testAmbiguousImplicitFieldReferenceAutoImport() throws Exception {
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
    public void testImplicitStaticClassAccess() throws Exception {
    	Launcher launcher = checkFile(false, "ImplicitStaticClassAccess.java");
    }
    @Test
    public void testImplicitStaticClassAccessAutoImport() throws Exception {
    	Launcher launcher = checkFile(true, "ImplicitStaticClassAccess.java");
    }
    @Test
    public void testGenericsWithAmbiguousStaticField() throws Exception {
    	Launcher launcher = checkFile(false, "GenericsWithAmbiguousStaticField.java");
    }
    @Test
    public void testGenericsWithAmbiguousStaticFieldAutoImport() throws Exception {
    	Launcher launcher = checkFile(true, "GenericsWithAmbiguousStaticField.java");
    }
    
    @Test
    public void testChildOfGenericsWithAmbiguousStaticField() throws Exception {
    	Launcher launcher = checkFile(false, "ChildOfGenericsWithAmbiguousStaticField.java");
    }
    
    @Test
    public void testChildOfGenericsWithAmbiguousStaticFieldAutoImport() throws Exception {
    	Launcher launcher = checkFile(true, "ChildOfGenericsWithAmbiguousStaticField.java");
    	if(expectImplicit) {
        	CtClass<?> cls = launcher.getFactory().Class().get(ChildOfGenericsWithAmbiguousStaticField.class);
        	//The toString of the method does not have a context of class, so it different result 
//        	assertEquals("spoon.test.staticFieldAccess2.GenericsWithAmbiguousStaticField.<V, C>genericMethod()", cls.getMethod("m1").getBody().getStatements().get(0).toString());
        	assertTrue(cls.toString().indexOf("spoon.test.staticFieldAccess2.GenericsWithAmbiguousStaticField.<V, C>genericMethod()")>=0);
        	assertEquals("genericMethod()", cls.getMethod("m1").getBody().getStatements().get(1).toString());
    	}
    }

    @Test
    public void testGenericsWithAmbiguousMemberField() throws Exception {
    	Launcher launcher = checkFile(false, "GenericsWithAmbiguousMemberField.java");
    }
    @Test
    public void testGenericsWithAmbiguousMemberFieldAutoImport() throws Exception {
    	Launcher launcher = checkFile(true, "GenericsWithAmbiguousMemberField.java");
    }

    @Test
    public void testAnnotationInChildWithConstants() throws Exception {
    	Launcher launcher = checkFile(false, "ChildOfConstants.java", "Constants.java");
    }

    @Test
    public void testAnnotationInChildWithConstantsAutoImport() throws Exception {
    	Launcher launcher = checkFile(true, "ChildOfConstants.java", "Constants.java");
    }
    
    private static Launcher checkFile(boolean autoImports, String... fileName) {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(autoImports);

		String pckg = "spoon/test/staticFieldAccess2/";
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
