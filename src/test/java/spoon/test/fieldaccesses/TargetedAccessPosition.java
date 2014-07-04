package spoon.test.fieldaccesses;

public class TargetedAccessPosition {
	public TargetedAccessPosition ta;
	public void foo(){
		TargetedAccessPosition t = new TargetedAccessPosition();
		t.ta.ta = t;
	}
}
