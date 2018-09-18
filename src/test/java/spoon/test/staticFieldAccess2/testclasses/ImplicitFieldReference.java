package spoon.test.staticFieldAccess2.testclasses;

public class ImplicitFieldReference {
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
