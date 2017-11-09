package spoon.test.template.testclasses.inheritance;


import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtParameter;
import spoon.template.Local;
import spoon.template.Parameter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SubTemplate extends SuperTemplate {

	public void toBeOverriden() {
		super.toBeOverriden();
	}

	public void methodWithTemplatedParameters(Object params) {
		List var = null; // will be replaced by List newVarName = null;
		ArrayList l = null; // will be replaced by LinkedList l = null;
		List o = (ArrayList) new ArrayList(); // will be replaced by List o = (LinkedList) new LinkedList();
		invocation.S();
		{
			for(Object x : intValues) {
				System.out.println(x); // will be inlined
			}
		}
		for(Object x : intValues) {
			System.out.println(x); // will be inlined
		}
		for(Object x : intValues) {
			{
				System.out.println(x); // will be inlined
			}
		}
		for(Object x : intValues) System.out.println(x); // will be inlined
		for(Object x : o) {
			System.out.println(x); // will be NOT inlined
		}
		var = o;	//will be replaced by newVarName = o
		l = (ArrayList) var;	//will be replaced by l = (LinkedList) newVarName
	}
	
	List var = null;
	public void methodWithFieldAccess() {
		List o = (ArrayList) new ArrayList(); // will be replaced by List o = (LinkedList) new LinkedList();
		ArrayList l = null; // will be replaced by LinkedList l = null;
		var = o;
		l = (ArrayList) var;	//will be replaced by l = (LinkedList) newVarName
	}

	/**
	 *  var
	 */
	/*
	 *  {@link ArrayList}
	 */
	// {@link invocation}
	void var() {}

	// method parameter template
	@Parameter
	public List<CtParameter> params;

	// name template "var" -> "newVarName"
	@Parameter("var")
	public String param_var = "newVarName";

	// type reference template
	@Parameter
	Class ArrayList = LinkedList.class;

	// invocation template
	@Parameter
	public CtInvocation invocation;

	// foreach inlining
	@Parameter
	public CtExpression[] intValues;

	@Local
	public void ignoredMethod(){}

	class InnerClass{}


}
