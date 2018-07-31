package spoon.test.staticFieldAccess2.testclasses;

public class AmbiguousImplicitFieldReference {
	/*
	 * The static field has exactly same name like the Class name.
	 * for example Apache CXF generates classes like that  
	 */
	public static String AmbiguousImplicitFieldReference = "c1";
	public String memberField;

	public String getMemberField() {
		return memberField;
	}

	public void setMemberField(String p_memberField) {
		memberField = p_memberField;
	}
	public void setMemberField2(String memberField) {
		this.memberField = memberField;
	}
	
	public void testLocalMethodInvocations() {
		getMemberField();
	}
}
