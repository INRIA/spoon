package spoon.architecture;

import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSwitchExpression;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.CtYieldStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;
/**
 * This defines elements filter of the spoon java model. It contains default filters for often used elements.
 * For more complex filters use an custom {@link Filter} implementation or {@code new TypeFilter(ASTElement.class)}. It is possible to reuse a filter.
 * <p>
 * A model element is part of ast and not always part of the visible code.
 * All filters convert the elements convert the element type.
 */
@SuppressWarnings("unchecked")
public enum DefaultElementFilter {
	// we skip CtUnboundVariableReference.
	// Copyright note: Almost all examples are copied from http://spoon.gforge.inria.fr/code_elements.html and not own work.

	/**
	 * This defines a filter for annotations <b>usage</b>. A annotation is e.g.: {@code @SuppressWarnings("unchecked")}.
	 * This filter does <b>not</b> match annotation definition. Use {@link ANNOTATIONS_DEFINITIONS} for annotation definitions. <p>

	 */
	ANNOTATIONS() {
		@Override
		public Filter<CtAnnotation<?>> getFilter() {
			return new TypeFilter<CtAnnotation<?>>(CtAnnotation.class);
		}
	},
	/**
	 * This defines a filter for annotation definition.
	 * <pre>
	 * {@code
	 * @interface TestAnnotation { }
	 * }
	 * </pre> <p>
	 */
	ANNOTATIONS_DEFINITIONS() {
		@Override
		public Filter<CtAnnotationType<?>> getFilter() {
			return new TypeFilter<CtAnnotationType<?>>(CtAnnotationType.class);
		}
	},
	/**
	 * This defines a filter for anonymous executables. Anonymous executable are anonymous executable block declaration in a class.
	 * The code block in the constructor, <pre>{@code {put("one",1);}}</pre> call as example.
	 * <pre>
	 * {@code
	 * 	static Map<String,Integer> numbers =
		new HashMap<String,Integer>() {
			private static final long serialVersionUID = 4293695943460830881L;
		{
			put("one",1);
		}};
	 * }
	 * </pre> <p>
	 */
	ANONYMOUS_EXECUTABLES() {
		@Override
		public Filter<CtAnonymousExecutable> getFilter() {
			return new TypeFilter<CtAnonymousExecutable>(CtAnonymousExecutable.class);
		}
	},
	/**
	 * This defines a filter for array reads. An array read is <pre>{@code array[0]}</pre> <p>
	 */
	ARRAY_READS() {
		@Override
		public Filter<CtArrayRead<?>> getFilter() {
			return new TypeFilter<CtArrayRead<?>>(CtArrayRead.class);
		}
	},
	/**
	 * This defines a filter for array writes. An array read is <pre>{@code array[0] = 3}</pre> <p>
	 */
	ARRAY_WRITES() {
		@Override
		public Filter<CtArrayWrite<?>> getFilter() {
			return new TypeFilter<CtArrayWrite<?>>(CtArrayWrite.class);
		}
	},
	/**
	 * This defines a filter for ArrayTypeReferences. An arrayTypeReferences is {@code int[]} for {@code int[][]}. Only multidimensional arrays have array type references. <p>
	 */
	ARRAY_TYPE_REFERENCES() {
		@Override
		public Filter<CtArrayTypeReference<?>> getFilter() {
			return new TypeFilter<CtArrayTypeReference<?>>(CtArrayTypeReference.class);
		}
	},
	/**
	 * This defines a filter for assert statements. An assert is <pre>{@code assert 1+1==2}</pre> <p>

	 */
	ASSERTS() {
		@Override
		public Filter<CtAssert<?>> getFilter() {
			return new TypeFilter<CtAssert<?>>(CtAssert.class);
		}
	},
	/**
	 * This defines a filter for assignments. An assignment is <pre>{@code x = 4;}</pre>. <p>

	 */
	ASSIGNMENTS() {
		@Override
		public Filter<CtAssignment<?, ?>> getFilter() {
			return new TypeFilter<CtAssignment<?, ?>>(CtAssignment.class);
		}
	},
	/**
	 * This defines a filter for binary operators. A binary operator is <pre>{@code
	 *	// 3+4 is the binary expression
	 *	int x = 3 + 4;
	 * }</pre> <p>

	 */
	BINARY_OPERATORS() {
		@Override
		public Filter<CtBinaryOperator<?>> getFilter() {
			return new TypeFilter<CtBinaryOperator<?>>(CtBinaryOperator.class);
		}
	},
	/**
	 * This defines a filter for code blocks A code block is a list of statements between curly brackets.
	 *  Example: <pre>
 	 *  { // &lt;-- block start
 	 *   System.out.println("foo");
 	 *  }
	 *	</pre> <p>

	 */
	BLOCKS() {
		@Override
		public Filter<CtBlock<?>> getFilter() {
			return new TypeFilter<CtBlock<?>>(CtBlock.class);
		}
	},
	/**
	 * This defines a filter for break statements.A break statement is <pre>{@code break}</pre> <p>

	 */
	BREAKS() {
		@Override
		public Filter<CtBreak> getFilter() {
			return new TypeFilter<CtBreak>(CtBreak.class);
		}
	},
	/**
	 * This defines a filter for a case statement. A case statement is part of a switch.
	 * A case statement can be either the old style or new arrow style. <p>

	 */
	CASES() {
		@Override
		public Filter<CtCase<?>> getFilter() {
			return new TypeFilter<CtCase<?>>(CtCase.class);
		}
	},
	/**
	 * This defines a filter for catch blocks. A catch block is a part of a try catch.
	 * <pre>{@code catch(Exception e)}</pre> <p>

	 */
	CATCHES() {
		@Override
		public Filter<CtCatch> getFilter() {
			return new TypeFilter<CtCatch>(CtCatch.class);
		}
	},
	/**
   * This defines a filter for type parameter (aka generics).
   * For example, in class A&lt;E&gt; { ... }, the "E" is modeled as an instance of CtTypeParameter. <p>

	 */
	TYPE_PARAMETERS() {
		@Override
		public Filter<CtTypeParameter> getFilter() {
			return new TypeFilter<CtTypeParameter>(CtTypeParameter.class);
		}
	},
	/** This defines a filter for conditionals. A conditional is a ternary expressions/?.
	 *  Example: <pre>{@code
	 * int a = 3==5 ? 3 : 5;
	 * }</pre> <p>

	 */
	CONDITIONALS() {
		@Override
		public Filter<CtConditional<?>> getFilter() {
			return new TypeFilter<CtConditional<?>>(CtConditional.class);
		}
	},
	/**
	 * This defines a filter for constructors. Example: <pre>{@code Foo() { }
	 * }</pre> <p>

	 */
	CONSTRUCTORS() {
		@Override
		public Filter<CtConstructor<?>> getFilter() {
			return new TypeFilter<CtConstructor<?>>(CtConstructor.class);
		}
	},
	/**
	 * This defines a filter for continue statements.A continue statement is <pre>{@code continue}</pre>

	 */
	CONTINUES() {
		@Override
		public Filter<CtContinue> getFilter() {
			return new TypeFilter<CtContinue>(CtContinue.class);
		}
	},
	/**
	 * This defines a filter for enum definitions. A enum type definition is <pre>{@code enum TestEnum{CORRECT, WRONG}}</pre><p>

	 */
	ENUMS() {
		@Override
		public Filter<CtEnum<?>> getFilter() {
			return new TypeFilter<CtEnum<?>>(CtEnum.class);
		}
	},
	/**
	 * This defines a filter for executable references. A executable reference is either a constructor or a method.
	 * A reference to an executable is e.g. an invocation. <p>

	 */
	EXECUTABLE_REFERENCES() {
		@Override
		public Filter<CtExecutableReference<?>> getFilter() {
			return new TypeFilter<CtExecutableReference<?>>(CtExecutableReference.class);
		}
	},
	/**
	 * This defines a filter for enum values. An enum value is a constant member in an enum.
	 * In <pre>{@code enum TestEnum{CORRECT, WRONG}}</pre> {@code CORRECT} and {@code WRONG} are members.
	 * Normal fields are <b>not</b> enum values. <p>

	 */
	ENUM_VALUES() {
		@Override
		public Filter<CtEnumValue<?>> getFilter() {
			return new TypeFilter<CtEnumValue<?>>(CtEnumValue.class);
		}
	},
	/**
	 * This defines a filter for this access. A this access is <pre>{@code this.value}</pre> A this access can be implicit and can be invisible in the code.
	 * Use {@link CtThisAccess#isImplicit()} to check it.
	 * <p>

	 */
	THIS_ACCESSES() {
		@Override
		public Filter<CtThisAccess<?>> getFilter() {
			return new TypeFilter<CtThisAccess<?>>(CtThisAccess.class);
		}
	},
	/**
	 * This defines a filter for field references. A reference could be seen as a usage.

	 */
	FIELD_REFERENCES() {
		@Override
		public Filter<CtFieldReference<?>> getFilter() {
			return new TypeFilter<CtFieldReference<?>>(CtFieldReference.class);
		}
	},
	/**
	 * This defines a filter for for-loops.
	 * Example:
 	 * <pre>
 	 *     // a for statement
 	 *     for(int i=0; i&lt;10; i++) {
 	 *     	System.out.println("foo");
 	 *     }
 	 * </pre> <p>

	 */
	FOR_LOOPS() {
		@Override
		public Filter<CtFor> getFilter() {
			return new TypeFilter<CtFor>(CtFor.class);
		}
	},
	/**
	 * This defines a filter for forEach-loops.
	 * Example:
	 * <pre>
	 *     java.util.List l = new java.util.ArrayList();
	 *     for(Object o : l) { // &lt;-- foreach loop
	 *     	System.out.println(o);
	 *     }
	 * </pre> <p>
	 */
	FOREACH_LOOPS() {
		@Override
		public Filter<CtForEach> getFilter() {
			return new TypeFilter<CtForEach>(CtForEach.class);
		}
	},
		/**
	 * This defines a filter for ifs.
 	 * Example:
 	 * <pre>
 	 *     if (1==0) {
 	 *     	System.out.println("foo");
 	 *     } else {
 	 *     	System.out.println("bar");
 	 *     }
 	 * </pre> <p>

	 */
	IFS() {
		@Override
		public Filter<CtIf> getFilter() {
			return new TypeFilter<CtIf>(CtIf.class);
		}
	},

	/**
 	 * This defines a filter for invocations. A invocation is a concrete method call.
 	 * Example:
 	 * <pre>
 	 *     // invocation of method println
 	 *     // the target is "System.out"
 	 *     System.out.println("foo");
 	 * </pre> <p>

	 */
	INVOCATIONS() {
		@Override
		public Filter<CtInvocation<?>> getFilter() {
			return new TypeFilter<CtInvocation<?>>(CtInvocation.class);
		}
	},
	/**
	 * This defines a filter for literals. A literals is a primitive or String and only the value, not the whole assignments.
 	 * <pre>
 	 *     int x = 4; // 4 is a literal
 	 * </pre>
	 * A null literal, as in s = null", is represented by a CtLiteral whose value is null.
	 * <p>

	 */
	LITERALS() {
		@Override
		public Filter<CtLiteral<?>> getFilter() {
			return new TypeFilter<CtLiteral<?>>(CtLiteral.class);
		}
	},
	/**
	 * This defines a filter for local variable. A local variable is defined within an executable body.
 	 *
 	 * Example:
 	 * <pre>
 	 *     // defines a local variable x
 	 *     int x = 0;
 	 * </pre>
 	 *
 	 * With Java 10, the local variable inference is now authorized, then the following code is valid too in a block scope:
 	 *
 	 * <pre>
 	 *     // local variable in Java 10
 	 *     var x = 0;
	 * </pre> <p>

	 */
	LOCAL_VARIABLES() {
		@Override
		public Filter<CtLocalVariable<?>> getFilter() {
			return new TypeFilter<CtLocalVariable<?>>(CtLocalVariable.class);
		}
	},
	/**
	 * This defines a filter for local variable references. A reference can be seen as a usage. <p>

	 */
	LOCAL_VARIABLES_REFERENCES() {
		@Override
		public Filter<CtLocalVariableReference<?>> getFilter() {
			return new TypeFilter<CtLocalVariableReference<?>>(CtLocalVariableReference.class);
		}
	},
	/**
	 * This defines a filter for catch variables. A catch variable is an exception variable in a catch block.
	 * Example:
	 * <pre>{@code
	 *  catch(SpoonException e) // <-- the SpoonException e is the catch variable
	 * }</pre> <p>

	 */
	CATCH_VARIABLES() {
		@Override
		public Filter<CtCatchVariable<?>> getFilter() {
			return new TypeFilter<CtCatchVariable<?>>(CtCatchVariable.class);
		}
	},
	/**
	 * This defines a filter for catch variable references. A reference can be seen as a usage. <p>

	 */
	CATCH_VARIABLES_REFERENCES() {
		@Override
		public Filter<CtCatchVariableReference<?>> getFilter() {
			return new TypeFilter<CtCatchVariableReference<?>>(CtCatchVariableReference.class);
		}
	},
	/**
	 * This defines a filter for method definitions in an annotation type.
	 * Example <pre>{@code
	 * public @interface JsonElement {
   * public String key() default ""; // <- annotation method
	 *	 }
	 * }</pre> <p>
	 */
	ANNOTATION_METHODS() {
		@Override
		public Filter<CtAnnotationMethod<?>> getFilter() {
			return new TypeFilter<CtAnnotationMethod<?>>(CtAnnotationMethod.class);
		}
	},
	/**
	 * This defines a filter for inline creations of a new arrays.
	 * Example : <pre>{@code
	 * int[] x = new int[] { 0, 1, 42}
	 * }<pre>
	 */
	NEW_ARRAYS() {
		@Override
		public Filter<CtNewArray<?>> getFilter() {
			return new TypeFilter<CtNewArray<?>>(CtNewArray.class);
		}
	},
	/**
	 * This defines a filter for constructor calls. A constructor call is <b>not</b> a method.
	 * Example:<pre>
   *     new Object();
   * </pre>
	 */
	CONSTRUCTOR_CALLS() {
		@Override
		public Filter<CtConstructorCall<?>> getFilter() {
			return new TypeFilter<CtConstructorCall<?>>(CtConstructorCall.class);
		}
	},
	/**
	 * This defines a filter for anonymous class creations.
   * Example:
 	 * <pre>
 	 *    // an anonymous class creation
 	 *    Runnable r = new Runnable() {
 	 *     	&#64;Override
 	 *     	public void run() {
 	 *     	  System.out.println("foo");
 	 *     	}
 	 *    };
 	 * </pre>
	 */
	NEW_CLASSES() {
		@Override
		public Filter<CtNewClass<?>> getFilter() {
			return new TypeFilter<CtNewClass<?>>(CtNewClass.class);
		}
	},
	/**
	 * This defines a filter for lambdas. Example: <pre>{@code (x -> { return x.toString());}</pre>
	 */
	LAMBDAS() {
		@Override
		public Filter<CtLambda<?>> getFilter() {
			return new TypeFilter<CtLambda<?>>(CtLambda.class);
		}
	},
	/**
	 * This defines a filter for expressions which represent an executable reference.
	 * Example: <pre>{@code
	 * java.util.function.Supplier p = Object::new;
	 * }</pre>
	 */
	EXECUTABLE_REFERENCE_EXPRESSIONS() {
		@Override
		public Filter<CtExecutableReferenceExpression<?, ?>> getFilter() {
			return new TypeFilter<CtExecutableReferenceExpression<?, ?>>(CtExecutableReferenceExpression.class);
		}
	},
	/**
	 * This defines a filter for self-operated assignments such as += or *=.
	 * Example: <pre>{@code x *= 3
	 * }
	 * </pre>
	 */
	OPERATOR_ASSIGNMENT() {
		@Override
		public Filter<CtOperatorAssignment<?, ?>> getFilter() {
			return new TypeFilter<CtOperatorAssignment<?, ?>>(CtOperatorAssignment.class);
		}
	},
	/**
	 * This defines a filter for references to packages. A reference is the usage of the package in the code e.g. in an import.
	 */
	PACKAGE_REFERENCES() {
		@Override
		public Filter<CtPackageReference> getFilter() {
			return new TypeFilter<CtPackageReference>(CtPackageReference.class);
		}
	},
	/**
	 * This defines a filter for method or constructor(executable) parameter. Example:
	 * <pre>{@code public void foo(int a) //<--- int a is the parameter}</pre>
	 */
	PARAMETERS() {
		@Override
		public Filter<CtParameter<?>> getFilter() {
			return new TypeFilter<CtParameter<?>>(CtParameter.class);
		}
	},
	/**
	 * This defines a filter for parameter reference. A parameter reference is the usage of a parameter. Example:
	 * <pre>{@code public void foo(int a) {
	 * System.out.println(a) // <-- reference to a.
	 * }}</pre>
	 *
	 */
	PARAMETER_REFERENCES() {
		@Override
		public Filter<CtParameterReference<?>> getFilter() {
			return new TypeFilter<CtParameterReference<?>>(CtParameterReference.class);
		}
	},
	/**
	 * This defines a filter for return statements. Example:
	 * <pre>{@code return 5}</pre>
	 */
	RETURNS() {
		@Override
		public Filter<CtReturn<?>> getFilter() {
			return new TypeFilter<CtReturn<?>>(CtReturn.class);
		}
	},
	/**
	 * This defines a filter for switch cases.
   * Example: <pre>
   * int x = 0;
   * switch(x) { // &lt;-- switch statement
   *     case 1:
   *       System.out.println("foo");
   * }</pre>
	 */
	SWITCHES() {
		@Override
		public Filter<CtSwitch<?>> getFilter() {
			return new TypeFilter<CtSwitch<?>>(CtSwitch.class);
		}
	},
		/**
	 * This defines a filter for switch expressions.
   * Example: <pre>
   * int i = 0;
   * int x = switch(i) { // &lt;-- switch expression
   *     case 1 -&gt; 10;
   *     case 2 -&gt; 20;
   *     default -&gt; 30;
   * };</pre>
	 */
	SWITCH_EXPRESSIONS() {
		@Override
		public Filter<CtSwitchExpression<?, ?>> getFilter() {
			return new TypeFilter<CtSwitchExpression<?, ?>>(CtSwitchExpression.class);
		}
	},
	/**
	 * This defines a filter for synchronized statements.
   * Example:
   * <pre>
   *    java.util.List l = new java.util.ArrayList();
   *    synchronized(l) {
   *     	System.out.println("foo");
   *    }
   * </pre>
	 */
	SYNCHRONIZED() {
		@Override
		public Filter<CtSynchronized> getFilter() {
			return new TypeFilter<CtSynchronized>(CtSynchronized.class);
		}
	},
	/**
	 * This defines a filter for throw statements.
   * Example:
   * <pre>
   *     throw new RuntimeException("oops")
   * </pre>
	 */
	THROWS() {
		@Override
		public Filter<CtThrow> getFilter() {
			return new TypeFilter<CtThrow>(CtThrow.class);
		}
	},
	/**
	 * This defines a filter for try blocks. Example:
   * <pre>
   *     try {
   *     	System.out.println("foo");
   *     } catch (Exception ignore) {}
   * </pre>
	 */
	TRIES() {
		@Override
		public Filter<CtTry> getFilter() {
			return new TypeFilter<CtTry>(CtTry.class);
		}
	},
	/**
	 * This defines a filter for try blocks.
 	 * Example:
 	 * <pre>
 	 *    // br is the resource
 	 *    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("/foo"))) {
 	 *    	br.readLine();
 	 *   }
 	 * </pre>
	 */
	TRIES_WITH_RESOURCES() {
		@Override
		public Filter<CtTryWithResource> getFilter() {
			return new TypeFilter<CtTryWithResource>(CtTryWithResource.class);
		}
	},
	/**
	 * This defines a filter for type parameter references.
	 */
	TYPE_PARAMETER_REFERENCES() {
		@Override
		public Filter<CtTypeParameterReference> getFilter() {
			return new TypeFilter<CtTypeParameterReference>(CtTypeParameterReference.class);
		}
	},
	/**
	 * This defines a filter for wildcard references. Represents a wildcard in generic type annotations, i.e. the "?" (e.g. the "?" in Collection&lt;?&gt; or Collection&lt;? extends List&gt;).
	 */
	WILDCARD_REFERENCES() {
		@Override
		public Filter<CtWildcardReference> getFilter() {
			return new TypeFilter<CtWildcardReference>(CtWildcardReference.class);
		}
	},
	/**
	 * This defines a filter for references to intersection types. A reference can be seen as usage.
	 */
	INTERSECTION_TYPE_REFERENCES() {
		@Override
		public Filter<CtIntersectionTypeReference<?>> getFilter() {
			return new TypeFilter<CtIntersectionTypeReference<?>>(CtIntersectionTypeReference.class);
		}
	},
	/**
	 * This defines a filter for type accesses A type access is either a static accesses, a Java 8 method references, instanceof binary expressions and ".class".
 	 * <pre>
 	 *     // access to static field
 	 *     java.io.PrintStream ps = System.out;
 	 * </pre>
 	 * <pre>
 	 *     // call to static method
 	 *     Class.forName("Foo")
 	 * </pre>
 	 * <pre>
 	 *     // method reference
 	 *     java.util.function.Supplier p =
 	 *       Object::new;
 	 * </pre>
 	 * <pre>
 	 *     // instanceof test
 	 *     boolean x = new Object() instanceof Integer // Integer is represented as an access to type Integer
 	 * </pre>
 	 * <pre>
 	 *     // fake field "class"
 	 *     Class x = Number.class
 	 * </pre>
	 */
	TYPE_ACCESSES() {
		@Override
		public Filter<CtTypeAccess<?>> getFilter() {
			return new TypeFilter<CtTypeAccess<?>>(CtTypeAccess.class);
		}
	},
	/**
	 * This defines a filter for unary operators. Unary operators are prefix and postfix operations Example:
	 * <pre>{@code x--;}</pre>
	 */
	UNARY_OPERATORS() {
		@Override
		public Filter<CtUnaryOperator<?>> getFilter() {
			return new TypeFilter<CtUnaryOperator<?>>(CtUnaryOperator.class);
		}
	},
	/**
	 * This defines a filter for variable reads. Variable reads are usages outside of assignments. Example:
   * <pre>
   *     String variable = "";
   *     System.out.println(
   *       variable // &lt;-- a variable read
   *     );
   * </pre>
	 */
	VARIABLE_READS() {
		@Override
		public Filter<CtVariableRead<?>> getFilter() {
			return new TypeFilter<CtVariableRead<?>>(CtVariableRead.class);
		}
	},
	/**
	 * This defines a filter for variable writes. In Java, it is a usage of a variable inside an assignment.
   *
   * For example:
   * <pre>
   *     String variable = "";
   *     variable = "new value"; // variable write
   * </pre>
   * <pre>
   *     String variable = "";
   *     variable += "";
   * </pre>
	 */
	VARIABLE_WRITES() {
		@Override
		public Filter<CtVariableWrite<?>> getFilter() {
			return new TypeFilter<CtVariableWrite<?>>(CtVariableWrite.class);
		}
	},
	/**
	 * This defines a filter for while-loops.
 	 * Example:
 	 * <pre>
 	 *     int x = 0;
 	 *     while (x!=10) {
 	 *         x=x+1;
 	 *     };
 	 * </pre>
	 */
	WHILES() {
		@Override
		public Filter<CtWhile> getFilter() {
			return new TypeFilter<CtWhile>(CtWhile.class);
		}
	},
	/**
	 * This defines a filter for annotation field access.
	 */
	ANNOTATION_FIELD_ACCESSES() {
		@Override
		public Filter<CtAnnotationFieldAccess<?>> getFilter() {
			return new TypeFilter<CtAnnotationFieldAccess<?>>(CtAnnotationFieldAccess.class);
		}
	},
	/**
	 * This defines a filter for variable reads.
   * In Java, it is a usage of a field outside an assignment. For example,
   * <pre>
   *     class Foo { int field; }
   *     Foo x = new Foo();
   *     System.out.println(x.field);
   * </pre>
   *
	 */
	FIELD_READS() {
		@Override
		public Filter<CtFieldRead<?>> getFilter() {
			return new TypeFilter<CtFieldRead<?>>(CtFieldRead.class);
		}
	},
	/**
	 * This defines a filter for field writes.
	 * In Java, it is a usage of a field inside an assignment.
   *
   * For example:
   * <pre>
   *     class Foo { int field; }
   *     Foo x = new Foo();
   *     x.field = 0;
   * </pre>
	 */
	FIELD_WRITES() {
		@Override
		public Filter<CtFieldWrite<?>> getFilter() {
			return new TypeFilter<CtFieldWrite<?>>(CtFieldWrite.class);
		}
	},
	/**
	 * This defines a filter for super accesses.
   * Example:
   * <pre>
   *     class Foo { int foo() { return 42;}};
   *     class Bar extends Foo {
   *     int foo() {
   *       return super.foo(); // &lt;-- access to super
   *     }
   *     };
   * </pre>
	 */
	SUPER_ACCESSES() {
		@Override
		public Filter<CtSuperAccess<?>> getFilter() {
			return new TypeFilter<CtSuperAccess<?>>(CtSuperAccess.class);
		}
	},
	/**
	 * This defines a filter for comments.
	 * Example: <pre>{@code // test comment }</pre>
	 */
	COMMENTS() {
		@Override
		public Filter<CtComment> getFilter() {
			return new TypeFilter<CtComment>(CtComment.class);
		}
	},
	/**
	 * This defines a filter for javadoc.
   * Example:
   * <pre>
   * &#x2F;**
   *  * Description
   *  * @tag a tag in the javadoc
   * *&#x2F;
   * </pre>
	 */
	JAVA_DOCS() {
		@Override
		public Filter<CtJavaDoc> getFilter() {
			return new TypeFilter<CtJavaDoc>(CtJavaDoc.class);
		}
	},
	/**
	 * This defines a filter for javadoc tags.
   * Example:
   * <code>
   * @since name description
   * </code>
	 */
	JAVA_DOC_TAGS() {
		@Override
		public Filter<CtJavaDocTag> getFilter() {
			return new TypeFilter<CtJavaDocTag>(CtJavaDocTag.class);
		}
	},
	/**
	 * This defines a filter for import statements.
   * Example:
   * <pre>
   *     import java.io.File;
   * </pre>
	 */
	IMPORTS() {
		@Override
		public Filter<CtImport> getFilter() {
			return new TypeFilter<CtImport>(CtImport.class);
		}
	},
	/**
	 * This defines a filter for module declaration. Modules are defined in `module-info.java` as follows:
   * <pre>
   *     module com.example.foo {
   *
   *     }
   * </pre>
	 */
	MODULES() {
		@Override
		public Filter<CtModule> getFilter() {
			return new TypeFilter<CtModule>(CtModule.class);
		}
	},
	/**
	 * This defines a filter for package exports in java modules. An exported or open package is usable by other modules.
	 * Examples:
   * <pre>
   *     exports com.example.foo.internal to com.example.foo.probe;
   *     opens com.example.foo.quux;
   * </pre>
	 */
	PACKAGE_EXPORTS() {
		@Override
		public Filter<CtPackageExport> getFilter() {
			return new TypeFilter<CtPackageExport>(CtPackageExport.class);
		}
	},
	/**
	 * This defines a filter for required module statements. A required module is part of a module declaration.
	 * Example:
   * <pre>
   *     requires transitive com.example.foo.network;
   * </pre>
	 */
	MODULE_REQUIREMENTS() {
		@Override
		public Filter<CtModuleRequirement> getFilter() {
			return new TypeFilter<CtModuleRequirement>(CtModuleRequirement.class);
		}
	},
	/**
	 * This defines a filter for provided service declarations.
   * Example:
   * <pre>
   *     provides com.example.foo.spi.Itf with com.example.foo.Impl;
   * </pre>
	 */
	PROVIDED_SERVICES() {
		@Override
		public Filter<CtProvidedService> getFilter() {
			return new TypeFilter<CtProvidedService>(CtProvidedService.class);
		}
	},
	/**
	 * This defines a filter for used service declarations.
   * <pre>
   *     uses java.logging.Logger;
   * </pre>
	 */
	USED_SERVICES() {
		@Override
		public Filter<CtUsedService> getFilter() {
			return new TypeFilter<CtUsedService>(CtUsedService.class);
		}
	},
	/**
	 * Defines a filter for compilation units. A compilation unit is a java file with one top level type declaration and zero or more non public declarations.
	 */
	COMPILATION_UNITS() {
		@Override
		public Filter<CtCompilationUnit> getFilter() {
			return new TypeFilter<CtCompilationUnit>(CtCompilationUnit.class);
		}
	},
	/**
	 * This defines a filter for package declarations. Example:
   * <pre>
   *     package your.nice.package.name;
   * </pre>
	 */
	PACKAGE_DECLARATIONS() {
		@Override
		public Filter<CtPackageDeclaration> getFilter() {
			return new TypeFilter<CtPackageDeclaration>(CtPackageDeclaration.class);
		}
	},
	/**
	 * This defines a filter for type member wildcard import references. These are imports for all static type members of a type.
	 * Example:
 	 * <code>somePackage.Type.*;</code>
	 */
	TYPE_MEMBER_WILDCARD_IMPORT_REFERENCES() {
		@Override
		public Filter<CtTypeMemberWildcardImportReference> getFilter() {
			return new TypeFilter<CtTypeMemberWildcardImportReference>(CtTypeMemberWildcardImportReference.class);
		}
	},
	/**
	 * This defines a filter for yield statements. Yield statements are part of switch expressions and can be implicit.
   * <pre>
   *     int x = 0;
   *     x = switch ("foo") {
   *         default -&gt; {
   * 					x=x+1;
   * 					yield x; //&lt;--- yield statement
   * 					}
   *     };
   * </pre>
	 */
	YIELD_STATEMENTS() {
		@Override
		public Filter<CtYieldStatement> getFilter() {
			return new TypeFilter<CtYieldStatement>(CtYieldStatement.class);
		}
	},
	/**
	 * This defines a filter for methods. Methods are <b>not</b> constructors. Use {@link #CONSTRUCTORS()} for them.
	 */
	METHODS() {
		@Override
		public Filter<CtMethod<?>> getFilter() {
			return new TypeFilter<CtMethod<?>>(CtMethod.class);
		}
	},
	/**
	 * This defines a filter for field declarations. A field is inside a type. For local variables use {@link #LOCAL_VARIABLES()}.
	 */
	FIELDS() {
		@Override
		public Filter<CtField<?>> getFilter() {
			return new TypeFilter<CtField<?>>(CtField.class);
		}
	},
	/**
	 * This defines a filter for classes. Classes are enums and normal classes. With {@code CtClass#isEnum()} you can filter them afterwards.
	 */
	CLASSES() {
		@Override
		public Filter<CtClass<?>> getFilter() {
			return new TypeFilter<CtClass<?>>(CtClass.class);

		}
	},
	/**
	 * This defines a filter for interfaces.
	 */
	INTERFACES() {
		@Override
		public Filter<CtInterface<?>> getFilter() {
			return new TypeFilter<CtInterface<?>>(CtInterface.class);

		}
	},
	/**
	 * This defines a filter for types. Types are enums, classes, interfaces and annotations.
	 */
	TYPES() {
		@Override
		public AbstractFilter<CtElement> getFilter() {
			return new AbstractFilter<CtElement>() {
				private AbstractFilter<CtType<?>> filter = new TypeFilter<CtType<?>>(CtType.class);
				@Override
				public boolean matches(CtElement element) {
					if (element instanceof CtType) {
						return filter.matches((CtType<?>) element) && !(element instanceof CtTypeParameter);
					}
					return false;
				}

			};
		}
	},
	/**
	 * This defines a filter for package declarations.
	 */
	PACKAGES() {
		@Override
		public Filter<CtPackage> getFilter() {
			return new TypeFilter<CtPackage>(CtPackage.class);

		}
	},
	/**
	 * This defines a filter for type references. A type reference is the usage of a type.
	 */
	TYPE_REFERENCE() {
		@Override
		public Filter<CtTypeReference<?>> getFilter() {
			return new TypeFilter<CtTypeReference<?>>(CtTypeReference.class);

		}
};

	/**
	 * Returns a element filter for the type. Use it with {@code CtModel#getElements(Filter)} to get all elements matching the given filter.
	 * The filter returns references and not copies. Every change on the elements is a change in the ast an vice versa. Returns never null.
	 * @param <T>  ast element type.
	 * @return  filter matching the given type.
	 */
	public abstract <T extends CtElement> Filter<T> getFilter();
}
