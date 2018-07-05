package spoon.test.fieldaccesses.testclasses;

public class TargetedAccessPosition {
	public TargetedAccessPosition ta;
	public void foo(){
		TargetedAccessPosition t = new TargetedAccessPosition();
		t.ta.ta = t;
	}
}
