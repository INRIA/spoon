package spoon.test.prettyprinter;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.experimental.modelobs.FineModelChangeListener;
import spoon.processing.AbstractProcessor;
import spoon.refactoring.Refactoring;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.filter.AnnotationFilter;
import spoon.reflect.visitor.filter.SuperInheritanceHierarchyFunction;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.reflect.visitor.printer.sniper.SniperJavaPrettyPrinter;
import spoon.test.prettyprinter.testclasses.AClass;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SniperTest {

	public Launcher createSpoon() throws Exception {
	    Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/prettyprinter/");
		spoon.getEnvironment().setSniperMode(true);
		spoon.buildModel();
		return spoon;
	}

	private CtFieldRead readEnumValue(Factory factory, Class enumClass, String field) {
		CtFieldRead<Object> fieldRead = factory.createFieldRead();
		CtTypeReference<Object> ctTypeReference = factory.createCtTypeReference(enumClass);
		fieldRead.setTarget(factory.createTypeAccess(ctTypeReference));
		CtFieldReference<Object> fieldReference = factory.createFieldReference();
		fieldReference.setDeclaringType(ctTypeReference);
		fieldReference.setSimpleName(field);
		fieldReference.setStatic(true);
		fieldReference.setFinal(true);
		fieldRead.setVariable(fieldReference);
		return fieldRead;
	}


	@Test
	public void testAddAnnotation() throws Exception {
		Launcher spoon = createSpoon();
		Factory factory = spoon.getFactory();
		CtClass<AClass> aClass = factory.Class().get(AClass.class);

		factory.Annotation().annotate(aClass.getMethodsByName("param").get(0), Deprecated.class);

		SniperJavaPrettyPrinter sniper = new SniperJavaPrettyPrinter(spoon.getEnvironment());
		sniper.calculate(aClass.getPosition().getCompilationUnit(), Arrays.asList(aClass));
		String output = sniper.getResult();
		Assert.assertEquals("   @java.lang.Deprecated", output.substring(271, 295));
	}

	@Test
	public void testAddComment() throws Exception {
		Launcher spoon = createSpoon();
		Factory factory = spoon.getFactory();
		CtClass<AClass> aClass = factory.Class().get(AClass.class);

		aClass.getMethodsByName("param").get(0).addComment(factory.createInlineComment("blabla"));

		SniperJavaPrettyPrinter sniper = new SniperJavaPrettyPrinter(spoon.getEnvironment());
		sniper.calculate(aClass.getPosition().getCompilationUnit(), Arrays.asList(aClass));
		String output = sniper.getResult();
		Assert.assertEquals("   // blabla", output.substring(271, 283));
	}

	@Test
	public void testPrettyPrinter() throws Exception {
		Launcher spoon = createSpoon();
		Factory factory = spoon.getFactory();
		CtClass<AClass> aClass = factory.Class().get(AClass.class);

		CtMethod method = factory.Core().createMethod();
		method.setSimpleName("m");
		method.setType(factory.Type().VOID_PRIMITIVE);
		method.addModifier(ModifierKind.PUBLIC);
		method.setBody(factory.Core().createBlock());

		aClass.addMethod(method);

		aClass.setSimpleName("Blabla");
		aClass.removeModifier(ModifierKind.PUBLIC);
		aClass.addModifier(ModifierKind.PRIVATE);
		aClass.addComment(factory.createInlineComment("blabla"));

		CtLocalVariable param = aClass.getMethodsByName("param").get(0).getElements(new TypeFilter<CtLocalVariable>(CtLocalVariable.class)).get(0);
		Refactoring.changeLocalVariableName(param, "g");

		aClass.getMethod("aMethod").getBody().addStatement(aClass.getFactory().Code().createCodeSnippetStatement("System.out.println(\"test\")"));

		CtStatement statement = aClass.getMethod("aMethodWithGeneric").getBody().getStatement(0);
		statement.replace(aClass.getFactory().Code().createCodeSnippetStatement("System.out.println(\"test\")"));
		CtFieldRead ctFieldRead = readEnumValue(factory, CtRole.class, CtRole.LABEL.name());
		aClass.getMethod("aMethodWithGeneric").getBody().addStatement(factory.createLocalVariable(factory.createCtTypeReference(CtRole.class), "d", ctFieldRead));

		//aClass.getMethod("aMethodWithGeneric").getBody().addStatement(statement);

		//aClass.getMethod("aMethodWithGeneric").getBody().removeStatement(aClass.getMethod("aMethodWithGeneric").getBody().getStatement(0));
		SniperJavaPrettyPrinter sniper = new SniperJavaPrettyPrinter(spoon.getEnvironment());
		sniper.calculate(aClass.getPosition().getCompilationUnit(), Arrays.asList(aClass));
		System.out.println(sniper.getResult());
	}

	enum ActionType {
		UPDATE,
		DELETE,
		DELETE_ALL,
		ADD;

		public String getTitleName() {
			String s = name().toLowerCase();
			s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
			int i = s.indexOf("_");
			if (i != -1) {
				s = s.substring(0, i) + Character.toUpperCase(s.charAt(i+1)) + s.substring(i+2);
			}
			return s;
		}

		static ActionType fromString(String name) {
			for (int i = 0; i < ActionType.values().length; i++) {
				if (ActionType.values()[i].getTitleName().toLowerCase().equals(name.toLowerCase())) {
					return ActionType.values()[i];
				}
			}
			return null;
		}
	}
	enum ContextType {
		OBJECT,
		LIST,
		SET,
		MAP;

		public String getTitleName() {
			String s = name().toLowerCase();
			return Character.toUpperCase(s.charAt(0)) + s.substring(1);
		}

		static ContextType fromString(String name) {
			for (int i = 0; i < ContextType.values().length; i++) {
				if (ContextType.values()[i].getTitleName().toLowerCase().equals(name.toLowerCase())) {
					return ContextType.values()[i];
				}
			}
			return null;
		}
	}
	@Test
	public void test() {
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/main/java/spoon/");
		spoon.getEnvironment().useTabulations(true);
		spoon.getEnvironment().setAutoImports(true);
		spoon.buildModel();



		spoon.getModel().processWith(new AbstractProcessor<CtIf>() {
			Set<CtClass> changedClass = new HashSet<>();
			Set<String> fields = new HashSet<>();

			int count = 0;
			@Override
			public boolean isToBeProcessed(CtIf candidate) {
				return candidate.getCondition().toString().equals("getFactory().getEnvironment().buildStackChanges()");
			}

			@Override
			public void process(CtIf element) {
				changedClass.add(element.getParent(CtClass.class));
				CtInvocation<?> statement;
				try {
					statement = (CtInvocation) ((CtBlock) element.getThenStatement()).getStatement(0);
				} catch (Exception x) {
					try {
						statement = (CtInvocation) ((CtBlock)((CtForEach) ((CtBlock) element.getThenStatement()).getStatement(0)).getBody()).getStatement(0);
					} catch (Exception e) {
						System.out.println(element.getParent(CtClass.class).getQualifiedName());
						System.out.println(element);
						return;
					}
				}
				CtConstructorCall<?> constructorCall = (CtConstructorCall<?>) statement.getArguments().get(0);
				ActionType type = getActionType(constructorCall);
				ContextType context = getContext(constructorCall);

				CtInvocation getFactoryInvocation = (CtInvocation) ((CtInvocation) statement.getTarget()).getTarget().clone();
				CtExecutableReference<?> changeExecutableReference = getFactory().Type().get(Factory.class).getMethod("Change").getReference();
				CtInvocation<?> changeInvocation = getFactory().createInvocation(getFactoryInvocation, changeExecutableReference);
				if (context == null || type == null) {
					System.out.println(constructorCall);
					return;
				}
				String methodName = "on" + context.getTitleName() + type.getTitleName();
				List<CtMethod<?>> factoryMethods = getFactory().Type().get(FineModelChangeListener.class).getMethodsByName(methodName);
				if (factoryMethods.isEmpty()) {
					System.out.println(methodName);
					return;
				}
				CtExecutableReference<?> reference = factoryMethods.get(0).getReference();
				CtInvocation<?> factoryCall = getFactory().createInvocation(changeInvocation, reference);

				List<CtExpression<?>> arguments = new ArrayList<>();
				CtConstructorCall<?> contextCreation = (CtConstructorCall) constructorCall.getArguments().get(0);
				CtExpression<?> field = contextCreation.getArguments().get(1);
				String fieldName = "";
				if (field instanceof CtFieldRead) {
					fieldName = ((CtFieldRead) field).getVariable().getSimpleName();
				} else if (field instanceof CtLiteral) {
					fieldName = ((CtLiteral) field).getValue().toString();
				}
				CtRole role = CtRole.fromName(fieldName);
				CtFieldRead ctFieldRead = readEnumValue(getFactory(), CtRole.class, role.name());
				fields.add(fieldName);
				arguments.add(contextCreation.getArguments().get(0));
				arguments.add(ctFieldRead);
				arguments.addAll(contextCreation.getArguments().subList(1, contextCreation.getArguments().size()));
				arguments.addAll(constructorCall.getArguments().subList(1, constructorCall.getArguments().size()));

				factoryCall.setArguments(arguments);

				spoon.getEnvironment().setSniperMode(true);
				element.replace(factoryCall);
				spoon.getEnvironment().setSniperMode(false);
				count ++;
			}

			private ActionType getActionType(CtConstructorCall<?> constructorCall) {
				return ActionType.fromString(constructorCall.getType().getSimpleName().replace("Action", ""));
			}

			private ContextType getContext(CtConstructorCall<?> constructorCall) {
				CtConstructorCall<?> contextCreation = (CtConstructorCall<?>) constructorCall.getArguments().get(0);
				return ContextType.fromString(contextCreation.getType().getSimpleName().replace("Context", ""));
			}

			@Override
			public void processingDone() {
				for (String field : fields) {
					CtRole propertyName = CtRole.fromName(field);
					if (propertyName == null) {
						System.out.println(field);
					}
				}

				for (CtClass aClass : changedClass) {
					SniperJavaPrettyPrinter sniper = new SniperJavaPrettyPrinter(spoon.getEnvironment());
					CompilationUnit compilationUnit = aClass.getPosition().getCompilationUnit();
					sniper.calculate(compilationUnit, Arrays.asList(aClass));
					try {
						PrintWriter writer = new PrintWriter(compilationUnit.getFile(), "UTF-8");
						writer.print(sniper.getResult());
						writer.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}



	@Test
	public void addPropertyAnnotation() {
		Launcher spoon = new Launcher();
		spoon.addInputResource("../spoon2/src/main/java/spoon/support/reflect");
		spoon.setOutputFilter(new TypeFilter<CtType<?>>(CtType.class) {
			@Override
			public boolean matches(CtType<?> element) {
				return !(element instanceof CtReference);
			}
		});
		spoon.getEnvironment().useTabulations(true);
		spoon.getEnvironment().setAutoImports(true);
		spoon.getEnvironment().setNoClasspath(true);
		spoon.buildModel();



		spoon.getModel().processWith(new AbstractProcessor<CtField>() {
			Set<CtClass> changedClass = new HashSet<>();
			Map<Class, List<CtRole>> properties = new HashMap<>();

			@Override
			public boolean isToBeProcessed(CtField candidate) {
				if ("serialVersionUID".equals(candidate.getSimpleName())) {
					return false;
				}
				if (candidate.hasModifier(ModifierKind.FINAL) || candidate.hasModifier(ModifierKind.STATIC) || candidate.hasModifier(ModifierKind.TRANSIENT)) {
					return false;
				}
				CtClass parent = candidate.getParent(CtClass.class);
				return parent != null && !(parent.isSubtypeOf(getFactory().createCtTypeReference(CtReference.class))) && parent.isSubtypeOf(getFactory().createCtTypeReference(CtElement.class));
			}

			@Override
			public void process(CtField element) {
				CtClass parent = element.getParent(CtClass.class);
				if (parent == null || parent instanceof CtEnum || !parent.isTopLevel()) {
					return;
				}
				changedClass.add(parent);

				String fieldName = element.getSimpleName();
				CtRole role = CtRole.fromName(fieldName);
				if (role == null) {
					if (fieldName.endsWith("s")) {
						fieldName = fieldName.substring(0, fieldName.length() - 1);
					}
					role = CtRole.fromName(fieldName);
					if (role == null) {
						System.out.println(element);
						return;
					}
				}

				spoon.getEnvironment().setSniperMode(true);
				getFactory().Annotation().annotate(element, MetamodelPropertyField.class);
				CtAnnotation<Annotation> annotation = element.getAnnotation(getFactory().createCtTypeReference(MetamodelPropertyField.class));
				annotation.addValue("role", role);
				spoon.getEnvironment().setSniperMode(false);
			}

			@Override
			public void processingDone() {
				for (Class aClass : properties.keySet()) {
					CtQuery map = getFactory().Type().get(aClass)
							.map(new SuperInheritanceHierarchyFunction()
									.includingSelf(false)
									.returnTypeReferences(true)
									.failOnClassNotFound(false));
					System.out.println(aClass);
					List<String> propertyList = new ArrayList<>();
					for (CtTypeReference o : new HashSet<>(map.list(CtTypeReference.class))) {
						Class supClass = o.getActualClass();
						if (properties.containsKey(supClass)) {
							for (CtRole propertyName : properties.get(supClass)) {
								//propertyList.add(propertyName.getTitleName());
							}
						}
					}
					for (CtRole propertyName : properties.get(aClass)) {
						propertyList.add(propertyName.getTitleName());
					}
					Collections.sort(propertyList);
					for (String s : propertyList) {
						System.out.println("\t" + s);
					}
				}
				for (CtClass aClass : changedClass) {
					SniperJavaPrettyPrinter sniper = new SniperJavaPrettyPrinter(spoon.getEnvironment());
					CompilationUnit compilationUnit = aClass.getPosition().getCompilationUnit();
					sniper.calculate(compilationUnit, Arrays.asList(aClass));
					try {
						PrintWriter writer = new PrintWriter(compilationUnit.getFile(), "UTF-8");
						writer.print(sniper.getResult());
						writer.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Test
	public void addPropertyAnnotationToGetter() {
		Launcher spoon = new Launcher();
		spoon.addInputResource("../spoon2/src/main/java/spoon/support/reflect");
		spoon.addInputResource("../spoon2/src/main/java/spoon/reflect");
		spoon.getEnvironment().useTabulations(true);
		spoon.getEnvironment().setAutoImports(true);
		spoon.getEnvironment().setNoClasspath(true);
		spoon.getEnvironment().setCommentEnabled(true);
		spoon.buildModel();

		Factory factory = spoon.getFactory();

		Map<CtTypeReference, Set<CtFieldRead>> roleByInterface = new HashMap<>();

		CtTypeReference annotationReference = factory.createCtTypeReference(MetamodelPropertyField.class);

		List<CtField> elements = spoon.getModel().getElements(new AnnotationFilter<>(CtField.class, MetamodelPropertyField.class));
		for (CtField<?> element : elements) {
			CtAnnotation annotation = element.getAnnotation(annotationReference);
			CtTypeReference roleInterface = ((CtTypeAccess) ((CtFieldRead)annotation.getValue("clazz")).getTarget()).getAccessedType();
			CtFieldRead role = ((CtFieldRead)annotation.getValue("role"));

			if (!roleByInterface.containsKey(roleInterface)) {
				roleByInterface.put(roleInterface, new HashSet<>());
			}
			roleByInterface.get(roleInterface).add(role);
		}

		for (CtTypeReference roleInterface : roleByInterface.keySet()) {
			CtType<?> declaration = roleInterface.getTypeDeclaration();

			for (CtFieldRead role : roleByInterface.get(roleInterface)) {
				String roleName = role.getVariable().getSimpleName();

				CtRole propertyName = CtRole.fromName(roleName);
				if (propertyName == null) {
					System.err.println(roleName);
					continue;
				}
				String titleName = propertyName.getCamelCaseName().toLowerCase();
				boolean isFound = false;
				for (CtMethod<?> method : declaration.getMethods()) {
					String methodName = method.getSimpleName().toLowerCase();

					CtAnnotation<Annotation> annotation;
					if (methodName.startsWith("get" + titleName)
							|| methodName.startsWith(titleName)) {

						spoon.getEnvironment().setSniperMode(true);
						factory.Annotation().annotate(method, PropertyGetter.class);
						spoon.getEnvironment().setSniperMode(false);

						annotation = method.getAnnotation(factory.createCtTypeReference(PropertyGetter.class));

					} else if (methodName.startsWith("set" + titleName)
							|| methodName.startsWith("add" + titleName)
							|| methodName.startsWith("remove" + titleName)
							|| methodName.startsWith("put" + titleName)) {
						spoon.getEnvironment().setSniperMode(true);
						factory.Annotation().annotate(method, PropertySetter.class);
						spoon.getEnvironment().setSniperMode(false);

						annotation = method.getAnnotation(factory.createCtTypeReference(PropertySetter.class));
					} else {
						continue;
					}
					annotation.addValue("role", role);
					isFound = true;
				}
				if (!isFound) {
					System.err.println(declaration.getQualifiedName() + "#" + roleName);
				}
			}

			SniperJavaPrettyPrinter sniper = new SniperJavaPrettyPrinter(spoon.getEnvironment());
			CompilationUnit compilationUnit = declaration.getPosition().getCompilationUnit();
			sniper.calculate(compilationUnit, Arrays.asList(declaration));
			try {
				PrintWriter writer = new PrintWriter(compilationUnit.getFile(), "UTF-8");
				writer.print(sniper.getResult());
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
