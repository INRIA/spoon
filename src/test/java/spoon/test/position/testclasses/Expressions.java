package spoon.test.position.testclasses;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Expressions {
	void method() {
		System.out.print("x");
		System.out.print(("x"));
		System.out.print((String)null);
		System.out.print(( String) ( (Serializable)(( (null )))));
		System.out.print((((String) null)));
		System.out.print(/*c1*/ ( /*c2*/
				(
						/*c3*/  String
						/*c4*/) //c5
				null /*c6*/
				//c7
				)    /*c8*/ 	 );
		System.out.print((List<?>) null);
		System.out.print((List<List<Map<String,Integer>>>) null);
	};
}