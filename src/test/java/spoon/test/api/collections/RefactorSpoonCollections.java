package spoon.test.api.collections;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.Test;

import spoon.Launcher;
import spoon.metamodel.MMMethod;
import spoon.metamodel.MMMethodKind;
import spoon.metamodel.Metamodel;
import spoon.metamodel.MetamodelConcept;
import spoon.metamodel.MetamodelProperty;
import spoon.pattern.Match;
import spoon.pattern.Pattern;
import spoon.pattern_detector.FoundPattern;
import spoon.pattern_detector.PatternDetector;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.FieldAccessFilter;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.code.CtStatementListImpl;
import spoon.support.reflect.code.CtTryWithResourceImpl;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.util.ModelList;
import spoon.support.util.ModelSet;
import spoon.support.util.internal.MapUtils;
import spoon.test.pattern_detector.PatternDetectorTest;

public class RefactorSpoonCollections {
	
	int matchCount;
	int methodCount;
	
	@Test
	public void testDetectPatterns() {
		Metamodel mm = new Metamodel(new File("src/main/java"));
		Factory factory = mm.getConcepts().iterator().next().getMetamodelInterface().getFactory();
		CtTypeReference<?> modelListRef = factory.Type().createReference(ModelList.class);
		CtTypeReference<?> modelSetRef = factory.Type().createReference(ModelSet.class);
		
		Map<MMMethodKind, PatternDetector> detectorsByMethodKind = new HashMap<>();
		
		forEachMetamodelMethodAccessingField(mm, (field, method) -> {
			if (field.getType().isSubtypeOf(modelListRef) || field.getType().isSubtypeOf(modelSetRef)) {
				//this property is already refactored
				return;
			}
			MetamodelPropertyField fieldRole = field.getAnnotation(MetamodelPropertyField.class);
			CtRole role = fieldRole.role()[0];
			MetamodelConcept concept = mm.getConcept((Class) field.getDeclaringType().getActualClass());
			MetamodelProperty property = concept.getProperty(role);
			if (property.getContainerKind() != ContainerKind.SINGLE) {
				MMMethod mmethod = property.getMethodBySignature(method.getSignature());
				if (mmethod == null) {
					System.out.println("Missing method for signature: " + concept.getName() + "#" + method.getSignature());
					return;
				}
				PatternDetector detector = MapUtils.getOrCreate(detectorsByMethodKind, mmethod.getKind(),
						() -> new PatternDetector().setIgnoreComments(true));
				if (mmethod.getKind()==MMMethodKind.GET) {
					if (mmethod.getName().equals("getModifiers")) {
						this.getClass();
					}
				}
				detector.matchCode(method);
				methodCount++;
			}
		});

		File targetDir = new File("target/patterns");
		targetDir.mkdirs();
		int i = 0;
		for (Map.Entry<MMMethodKind, PatternDetector> entry : detectorsByMethodKind.entrySet()) {
			MMMethodKind kind = entry.getKey();
			PatternDetector detector = entry.getValue();
			List<FoundPattern> patterns = detector.getPatterns();
			File targetDir2 = new File(targetDir, "patterns_" + kind.name());
			targetDir2.mkdirs();
			for (FoundPattern pattern : patterns) {
				String className = "Pattern_" + String.valueOf(i++);
				File file = new File(targetDir2, className + ".java");
				System.out.println(file.getAbsolutePath());
				try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
					w.write("class " + className + " {\n");
					w.write(pattern.toString());
					w.write("\n\n");
					w.write("}\n");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Test
	public void testPrintSpoonPropertyAccessorMethods() {
		matchCount = 0;
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/java/spoon/test/api/collections/patterns");
		launcher.buildModel();
		
		List<Pattern> allPatterns = new ArrayList<>();
		
		Factory patternFactory = launcher.getFactory();
		CtTypeReference<?> patternTypeRef = patternFactory.Type().createReference(Pattern.class);
		patternFactory.getModel().filterChildren((CtMethod method) -> method.getType().equals(patternTypeRef)).forEach((CtMethod<Pattern> method) -> {
			allPatterns.add(callStaticMethod(method, patternFactory));
		});
		
		Metamodel mm = new Metamodel(new File("src/main/java"));
		Factory factory = mm.getConcepts().iterator().next().getMetamodelInterface().getFactory();
		CtTypeReference<?> modelListRef = factory.Type().createReference(ModelList.class);
		CtTypeReference<?> modelSetRef = factory.Type().createReference(ModelSet.class);
		forEachMetamodelMethodAccessingField(mm, (field, m) -> {
			if (field.getType().isSubtypeOf(modelListRef) || field.getType().isSubtypeOf(modelSetRef)) {
				//this field is already refactored
				return;
			}
			
			if (field.getType().isSubtypeOf(factory.Type().COLLECTION)
					|| field.getType().isSubtypeOf(factory.Type().MAP)) {
				Match match = matchPatterns(allPatterns, m);
				if (match == null) {
					System.out.println(m.getDeclaringType().getQualifiedName() + "\n" + m);
				}
				matchCount++;
			}
		});
	}

	@Test
	public void testWrongPattern() {
		Metamodel mm = new Metamodel(new File("src/main/java"));
		Factory factory = mm.getConcepts().iterator().next().getMetamodelInterface().getFactory();
		CtMethod<?> method1 = factory.Type().get(CtElementImpl.class).getMethodsByName("removeAnnotation").get(0);
		CtMethod<?> method2 = factory.Type().get(CtTryWithResourceImpl.class).getMethodsByName("removeResource").get(0);
		
		List<FoundPattern> detectedPatterns = detect(method1, method2);
		
		assertEquals(1, detectedPatterns.size());
		FoundPattern detectedPattern = detectedPatterns.get(0);
		System.out.println(detectedPattern.getPattern().toString());
		assertEquals(2, detectedPattern.getCountOfMatches());
		assertEquals(7, detectedPattern.getPattern().getParameterInfos().size());
		PatternDetectorTest.checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method1));
		PatternDetectorTest.checkPatternMatchesCodeAndGeneratesSameCode(detectedPattern, Collections.singletonList(method2));
	}
	
	private List<FoundPattern> detect(CtElement... elements) {
		PatternDetector pd = new PatternDetector();
		for (CtElement ele : elements) {
			pd.matchCode(ele);
		}
		List<FoundPattern> detectedPatterns = pd.getPatterns();
		return detectedPatterns;
	}
	
	
	private Match matchPatterns(List<Pattern> allPatterns, CtMethod<?> m) {
		for (Pattern pattern : allPatterns) {
			Match match = matchesPattern(pattern, m);
			if (match != null) {
				return match;
			}
		}
		return null; 
	}
	
	private Match matchesPattern(Pattern pattern, CtMethod<?> m) {
		List<Match> matches = pattern.getMatches(m);
		if (matches.size() > 0) {
			return matches.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T> T callStaticMethod(CtMethod<T> method, Object... args) {
		Class cls = method.getDeclaringType().getActualClass();
		Method m;
		try {
			m = cls.getMethod(method.getSimpleName(), getMethodParameterTypes(method));
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
		try {
			return (T) m.invoke(null, args);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getTargetException());
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Class[] getMethodParameterTypes(CtMethod<?> method) {
		Class[] paramTypes = new Class[method.getParameters().size()];
		for (int i = 0; i < paramTypes.length; i++) {
			paramTypes[i] = method.getParameters().get(i).getType().getActualClass();
		}
		return paramTypes;
	}

	/**
	 * @param fieldMethodConsumer called for each spoon meta model method which accesses CtField annotated by MetamodelPropertyField 
	 */
	private void forEachMetamodelMethodAccessingField(Metamodel mm, BiConsumer<CtField<?>, CtMethod<?>> fieldMethodConsumer) {
		Set<CtMethod<?>> visitedMethods = Collections.newSetFromMap(new IdentityHashMap<>());
		forEachMetamodelField(mm, field -> {
			System.out.println(field.getDeclaringType().getQualifiedName() + "#" + field.getSimpleName());
			CtType<?> implType =  field.getDeclaringType();
			implType.filterChildren(new FieldAccessFilter(field.getReference()))
				.map((CtFieldAccess<?> fa) -> fa.getParent(CtMethod.class))
				.forEach((CtMethod<?> m)->{
					if (visitedMethods.add(m)) {
						fieldMethodConsumer.accept(field, m);
					}
				});
		});
		
	}
	
	private void forEachMetamodelMMMethodAccessingMethod(Metamodel mm, BiConsumer<MMMethod, MMMethodKind> fieldMethodConsumer) {
		Set<CtMethod<?>> visitedMethods = Collections.newSetFromMap(new IdentityHashMap<>());
		forEachMetamodelProperty(mm, property -> {
			System.out.println(property.toString());
			for (MMMethodKind kind : MMMethodKind.values()) {
				List<MMMethod> methods = property.getMethods(kind);
				for (MMMethod mmMethod : methods) {
					fieldMethodConsumer.accept(mmMethod, kind);
				}
			}
		});
		
	}

	/**
	 * @param fieldConsumer is called for each CtField annotated by MetamodelPropertyField of spoon meta model
	 */
	private void forEachMetamodelField(Metamodel mm, Consumer<CtField<?>> fieldConsumer) {
		List<CtField<?>> allSpoonFields = new ArrayList<>();
		for (MetamodelConcept mmConcept : mm.getConcepts()) {
			if (mmConcept.getImplementationClass() == null) {
				continue;
			}
			for (CtField<?> field : mmConcept.getImplementationClass().getFields()) {
				if (field.hasAnnotation(MetamodelPropertyField.class)) {
					fieldConsumer.accept(field);
				}
			} 
		}
	}

	/**
	 * @param propertyConsumer is called for each CtField annotated by MetamodelPropertyField of spoon meta model
	 */
	private void forEachMetamodelProperty(Metamodel mm, Consumer<MetamodelProperty> propertyConsumer) {
		List<CtField<?>> allSpoonFields = new ArrayList<>();
		for (MetamodelConcept mmConcept : mm.getConcepts()) {
			if (mmConcept.getImplementationClass() == null) {
				continue;
			}
			for (MetamodelProperty property : mmConcept.getProperties()) {
				propertyConsumer.accept(property);
			} 
		}
	}
}
