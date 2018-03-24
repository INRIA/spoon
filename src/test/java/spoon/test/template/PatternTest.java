package spoon.test.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonModelBuilder;
import spoon.pattern.InlineStatementsBuilder;
import spoon.pattern.ParametersBuilder;
import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern.TemplateModelBuilder;
import spoon.pattern.matcher.Match;
import spoon.pattern.parameter.ParameterInfo;
import spoon.pattern.parameter.ParameterValueProvider;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.template.testclasses.replace.DPPSample1;
import spoon.test.template.testclasses.replace.NewPattern;
import spoon.test.template.testclasses.replace.OldPattern;
import spoon.test.template.testclasses.types.AClassWithMethodsAndRefs;
import spoon.testing.utils.ModelUtils;


// main test of Spoon's patterns
public class PatternTest {

	@Test
	public void testPatternParameters() {
		//contract: all the parameters of Pattern are available
		Factory f = ModelUtils.build(
				new File("./src/test/java/spoon/test/template/testclasses/replace/DPPSample1.java"),
				new File("./src/test/java/spoon/test/template/testclasses/replace")
			);
		Pattern p = OldPattern.createPatternFromOldPattern(f);
		Map<String, ParameterInfo> parameterInfos = p.getParameterInfos();

		// the code in createPatternFromOldPattern creates 15 pattern parameters
		assertEquals(15, parameterInfos.size());
		// .. which are
		assertEquals(new HashSet<>(Arrays.asList("next","item","startPrefixSpace","printer","start",
				"statements","nextPrefixSpace","startSuffixSpace","elementPrinterHelper",
				"endPrefixSpace","startKeyword","useStartKeyword","end","nextSuffixSpace","getIterable"
				)), parameterInfos.keySet());
		// the map from getParameterInfos is consistent
		for (Map.Entry<String, ParameterInfo> e : parameterInfos.entrySet()) {
			assertEquals(e.getKey(), e.getValue().getName());
		}
	}

	@Test
	public void testPatternToString() {
		//contract: Pattern can be printed to String and each parameter is defined there
		Factory f = ModelUtils.build(
				new File("./src/test/java/spoon/test/template/testclasses/replace/DPPSample1.java"),
				new File("./src/test/java/spoon/test/template/testclasses/replace")
			);
		Pattern p = OldPattern.createPatternFromOldPattern(f);
		String strOfPattern = p.toString();
		
		Map<String, ParameterInfo> parameterInfos = p.getParameterInfos();
		assertEquals(15, parameterInfos.size());

		// contract: all parameters from getParameterInfos are pretty-printed
		for (Map.Entry<String, ParameterInfo> e : parameterInfos.entrySet()) {
			assertTrue("The parameter " + e.getKey() + " is missing", strOfPattern.indexOf("<= ${"+e.getKey()+"}")>=0);
		}
	}

	@Test
	public void testMatchSample1() throws Exception {
		// contract: a complex pattern is well matched twice
		Factory f = ModelUtils.build(
				new File("./src/test/java/spoon/test/template/testclasses/replace/DPPSample1.java"),
				new File("./src/test/java/spoon/test/template/testclasses/replace")
		);
		CtClass<?> classDJPP = f.Class().get(DPPSample1.class);
		assertNotNull(classDJPP);
		assertFalse(classDJPP.isShadow());

		CtType<Object> type = f.Type().get(OldPattern.class);
		Pattern p = PatternBuilder
				//Create a pattern from all statements of OldPattern_ParamsInNestedType#patternModel
				.create(new TemplateModelBuilder(type).setBodyOfMethod("patternModel").getTemplateModels())
				.configureParameters((ParametersBuilder pb) -> pb
						// creating patterns parameters for all references to "params" and "items"
						.createPatternParameterForVariable("params", "item")
						.parameter("statements").setContainerKind(ContainerKind.LIST)
				)
				.createPatternParameters()
				.configureInlineStatements(ls -> ls.byVariableName("useStartKeyword"))
				.build();

		// so let's try to match this complex pattern  on DJPP
		List<Match> matches = p.getMatches(classDJPP);

		// there are two results (the try-with-resource in each method
		assertEquals(2, matches.size());
		ParameterValueProvider params = matches.get(0).getParameters();
		assertEquals("\"extends\"", params.getValue("startKeyword").toString());
		assertEquals(Boolean.TRUE, params.getValue("useStartKeyword"));
		assertEquals("false", params.getValue("startPrefixSpace").toString());
		assertEquals("null", params.getValue("start").toString());
		assertEquals("false", params.getValue("startSuffixSpace").toString());
		assertEquals("false", params.getValue("nextPrefixSpace").toString());
		assertEquals("\",\"", params.getValue("next").toString());
		assertEquals("true", params.getValue("nextSuffixSpace").toString());
		assertEquals("false", params.getValue("endPrefixSpace").toString());
		assertEquals("\";\"", params.getValue("end").toString());
		assertEquals("ctEnum.getEnumValues()", params.getValue("getIterable").toString());
		assertEquals("[scan(enumValue)]", params.getValue("statements").toString());

		params = matches.get(1).getParameters();
		// all method arguments to createListPrinter have been matched
		assertEquals(null, params.getValue("startKeyword"));
		assertEquals(Boolean.FALSE, params.getValue("useStartKeyword"));
		assertEquals("false", params.getValue("startPrefixSpace").toString());
		assertEquals("null", params.getValue("start").toString());
		assertEquals("false", params.getValue("startSuffixSpace").toString());
		assertEquals("false", params.getValue("nextPrefixSpace").toString());
		assertEquals("\",\"", params.getValue("next").toString());
		assertEquals("true", params.getValue("nextSuffixSpace").toString());
		assertEquals("false", params.getValue("endPrefixSpace").toString());
		assertEquals("\";\"", params.getValue("end").toString());
		assertEquals("ctEnum.getEnumValues()", params.getValue("getIterable").toString());
		assertEquals("[scan(enumValue)]", params.getValue("statements").toString());
	}

	@Test
	public void testTemplateReplace() throws Exception {
		// contract: ??
		Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		factory.getEnvironment().setComplianceLevel(8);
		factory.getEnvironment().setNoClasspath(true);
		factory.getEnvironment().setCommentEnabled(true);
		factory.getEnvironment().setAutoImports(true);
		final SpoonModelBuilder compiler = launcher.createCompiler(factory);
		compiler.addInputSource(new File("./src/main/java/spoon/reflect/visitor"));
		compiler.addInputSource(new File("./src/test/java/spoon/test/template/testclasses/replace"));
		compiler.build();
		CtClass<?> classDJPP = factory.Class().get(DefaultJavaPrettyPrinter.class);
		assertNotNull(classDJPP);
		assertFalse(classDJPP.isShadow());
		CtType<?> targetType = (classDJPP instanceof CtType) ? (CtType) classDJPP : classDJPP.getParent(CtType.class);
		Factory f = classDJPP.getFactory();

		// we create two different patterns
		Pattern newPattern = NewPattern.createPatternFromNewPattern(f);
		Pattern oldPattern = OldPattern.createPatternFromOldPattern(f);
		
		oldPattern.forEachMatch(classDJPP, (match) -> {
			CtElement matchingElement = match.getMatchingElement(CtElement.class, false);
			RoleHandler role = RoleHandlerHelper.getRoleHandlerWrtParent(matchingElement);
			List<CtElement> elements = newPattern.applyToType(targetType, (Class) role.getValueClass(), match.getParametersMap());
			match.replaceMatchesBy(elements);
		});

		launcher.setSourceOutputDirectory(new File("./target/spooned-template-replace/"));
		launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.CLASSES);
	}
	
	@Test
	public void testGenerateClassWithSelfReferences() throws Exception {
		//contract: a class with methods and fields can be used as template to generate a clone
		//all the references to the origin class are replace by reference to the new class
		CtType templateModel = ModelUtils.buildClass(AClassWithMethodsAndRefs.class);
		Factory factory = templateModel.getFactory();
		Pattern pattern = PatternBuilder.create(templateModel).build();
		final String newQName = "spoon.test.generated.ACloneOfAClassWithMethodsAndRefs";
		CtClass<?> generatedType = pattern.createType(factory, newQName, Collections.emptyMap());
		assertNotNull(generatedType);
		assertEquals(newQName, generatedType.getQualifiedName());
		assertEquals("ACloneOfAClassWithMethodsAndRefs", generatedType.getSimpleName());
		assertEquals(Arrays.asList("<init>","local","sameType","sameTypeStatic","anotherMethod","someMethod","Local","foo"),
				generatedType.getTypeMembers().stream().map(CtTypeMember::getSimpleName).collect(Collectors.toList()));
		//contract: all the type references points to new type
		Set<String> usedTypeRefs = new HashSet<>();
		generatedType.filterChildren(new TypeFilter<>(CtTypeReference.class))
			.forEach((CtTypeReference ref) -> usedTypeRefs.add(ref.getQualifiedName()));
		assertEquals(new HashSet<>(Arrays.asList(
				"spoon.test.generated.ACloneOfAClassWithMethodsAndRefs","void","boolean",
				"spoon.test.generated.ACloneOfAClassWithMethodsAndRefs$1Bar",
				"java.lang.Object","int","spoon.test.generated.ACloneOfAClassWithMethodsAndRefs$Local")),
				usedTypeRefs);
		//contract: all executable references points to executables in cloned type
		generatedType.filterChildren(new TypeFilter<>(CtExecutableReference.class)).forEach((CtExecutableReference execRef) ->{
			CtTypeReference declTypeRef = execRef.getDeclaringType();
			if(declTypeRef.getQualifiedName().startsWith("spoon.test.generated.ACloneOfAClassWithMethodsAndRefs")) {
				//OK
				return;
			}
			if(declTypeRef.getQualifiedName().equals(Object.class.getName())) {
				return;
			}
			fail("Unexpected declaring type " + declTypeRef.getQualifiedName());
		});
	}

	@Test
	public void testGenerateMethodWithSelfReferences() throws Exception {
		//contract: a method with self references can be used as a template to generate a clone
		//all the references to the origin class are replace by reference to the new class
		CtType templateModel = ModelUtils.buildClass(AClassWithMethodsAndRefs.class);
		Factory factory = templateModel.getFactory();
		Pattern pattern = PatternBuilder.create(
				(CtMethod) templateModel.getMethodsByName("foo").get(0),
				templateModel.getNestedType("Local"))
				//switch ON: generate by comments
				.setAddGeneratedBy(true)
				.build();
		final String newQName = "spoon.test.generated.ACloneOfAClassWithMethodsAndRefs";
		
		CtClass<?> generatedType = factory.createClass(newQName);
		
		assertNotNull(generatedType);
		assertEquals(newQName, generatedType.getQualifiedName());
		assertEquals("ACloneOfAClassWithMethodsAndRefs", generatedType.getSimpleName());

		pattern.applyToType(generatedType, CtMethod.class, Collections.emptyMap());
		//contract: new method and interface were added
		assertEquals(Arrays.asList("Local","foo"),
				generatedType.getTypeMembers().stream().map(CtTypeMember::getSimpleName).collect(Collectors.toList()));
		assertEquals(1, generatedType.getMethodsByName("foo").size());
		assertNotNull(generatedType.getNestedType("Local"));
		//contract: generate by comments are appended
		assertEquals("Generated by spoon.test.template.testclasses.types.AClassWithMethodsAndRefs#foo(AClassWithMethodsAndRefs.java:30)",
				generatedType.getMethodsByName("foo").get(0).getDocComment().trim());
		assertEquals("Generated by spoon.test.template.testclasses.types.AClassWithMethodsAndRefs$Local(AClassWithMethodsAndRefs.java:26)",
				generatedType.getNestedType("Local").getDocComment().trim());
		//contract: all the type references points to new type
		Set<String> usedTypeRefs = new HashSet<>();
		generatedType.filterChildren(new TypeFilter<>(CtTypeReference.class))
			.forEach((CtTypeReference ref) -> usedTypeRefs.add(ref.getQualifiedName()));
		assertEquals(new HashSet<>(Arrays.asList(
				"spoon.test.generated.ACloneOfAClassWithMethodsAndRefs","void",
				"spoon.test.generated.ACloneOfAClassWithMethodsAndRefs$1Bar",
				"java.lang.Object","spoon.test.generated.ACloneOfAClassWithMethodsAndRefs$Local")),
				usedTypeRefs);
		//contract: all executable references points to executables in cloned type
		generatedType.filterChildren(new TypeFilter<>(CtExecutableReference.class)).forEach((CtExecutableReference execRef) ->{
			CtTypeReference declTypeRef = execRef.getDeclaringType();
			if(declTypeRef.getQualifiedName().startsWith("spoon.test.generated.ACloneOfAClassWithMethodsAndRefs")) {
				//OK
				return;
			}
			if(declTypeRef.getQualifiedName().equals(Object.class.getName())) {
				return;
			}
			fail("Unexpected declaring type " + declTypeRef.getQualifiedName());
		});
	}
	@Test
	public void testInlineStatementsBuilder() throws Exception {
		// TODO: specify what InlineStatementsBuilder does
	}
}
