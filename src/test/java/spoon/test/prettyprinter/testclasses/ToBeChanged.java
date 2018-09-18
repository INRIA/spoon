package spoon.test.prettyprinter.testclasses;

import java.util.ArrayList;
import java.util.List;

/**
 * The content of this file 
 * 

 * 		should not be changed
 * Because DJPP should print only modified content again 
 */
public
@Deprecated
abstract class /* even this comment stays here together with all SPACES and EOLs*/ ToBeChanged<T, K> /*before extends*/ 
	extends ArrayList<T /* let's confuse > it */ > implements List<T>,
	Cloneable
{
	
	
	/**/
	final
	//
	private String string = "a"
			+ "b" + "c"+"d";
	
	//and spaces here are wanted too
	
	
	public <T, K> void andSomeOtherMethod(
			int param1,
			String param2         , List<?>[][] ... twoDArrayOfLists)
	{/**/
		System.out.println("aaa"
				+ "xyz");
	/*x*/}
	List<?>[][] twoDArrayOfLists = new List<?>[7][];
}

//and what about this comment? 