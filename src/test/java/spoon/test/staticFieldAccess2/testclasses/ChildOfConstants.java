package spoon.test.staticFieldAccess2.testclasses;

//the PRIO must be prefixed by Constants because we are not yet in scope of Constants class
@ALong(number = Constants.PRIO)
public class ChildOfConstants extends Constants
{
	long p1 = Constants.PRIO;
	long p2 = PRIO;
}
