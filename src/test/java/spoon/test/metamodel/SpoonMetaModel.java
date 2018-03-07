/**
 * Copyright (C) 2006-2017 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.test.metamodel;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.SpoonException;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AllTypeMembersFunction;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.FileSystemFolder;
import spoon.support.visitor.ClassTypingContext;

/**
 * Represents a Spoon meta model of the AST nodes.
 */
public class SpoonMetaModel {
	public static final String CLASS_SUFFIX = "Impl";
	/**
	 * qualified names of packages which contains interfaces of spoon model
	 */
	public static final Set<String> MODEL_IFACE_PACKAGES = new HashSet<>(Arrays.asList(
			"spoon.reflect.code",
			"spoon.reflect.declaration",
			"spoon.reflect.reference"));

	private final Factory factory;
	
	/**
	 * {@link MetamodelConcept}s by name
	 */
	private final Map<String, MetamodelConcept> nameToConcept = new HashMap<>();

	/**
	 * Parses spoon sources and creates factory with spoon model.
	 *
	 * @param spoonJavaSourcesDirectory the root directory of java sources of spoon model.
	 * 	The directory must contain "spoon" directory.
	 */
	public SpoonMetaModel(File spoonJavaSourcesDirectory) {
		this(createFactory(spoonJavaSourcesDirectory));
	}

	/**
	 * @param factory already loaded factory with all Spoon model types
	 */
	public SpoonMetaModel(Factory factory) {
		this.factory =  factory;
		
		for (String apiPackage : MODEL_IFACE_PACKAGES) {
			if (factory.Package().get(apiPackage) == null) {
				throw new SpoonException("Spoon Factory model is missing API package " + apiPackage);
			}
			String implPackage = replaceApiToImplPackage(apiPackage);
			if (factory.Package().get(implPackage) == null) {
				throw new SpoonException("Spoon Factory model is missing implementation package " + implPackage);
			}
		}
		
		//search for all interfaces of spoon model and create MetamodelConcepts for them
		factory.getModel().filterChildren(new TypeFilter<>(CtInterface.class))
			.forEach((CtInterface<?> iface) -> {
				if (MODEL_IFACE_PACKAGES.contains(iface.getPackage().getQualifiedName())) {
					getOrCreateConcept(iface);
				}
			});
	}

	/**
	 * @return all {@link MetamodelConcept}s of spoon meta model
	 */
	public Collection<MetamodelConcept> getConcepts() {
		return Collections.unmodifiableCollection(nameToConcept.values());
	}
	
	/**
	 * @param type a spoon model class or interface, whose concept name has to be returned
	 * @return name of {@link MetamodelConcept}, which represents Spoon model {@link CtType}
	 */
	public static String getConceptName(CtType<?> type) {
		String name = type.getSimpleName();
		if (name.endsWith(CLASS_SUFFIX)) {
			name = name.substring(0, name.length() - CLASS_SUFFIX.length());
		}
		return name;
	}
	

	/**
	 * @param iface the interface of spoon model element
	 * @return {@link CtClass} of Spoon model which implements the spoon model interface. null if there is no implementation.
	 */
	public static CtClass<?> getImplementationOfInterface(CtInterface<?> iface) {
		String impl = replaceApiToImplPackage(iface.getQualifiedName()) + CLASS_SUFFIX;
		return (CtClass<?>) iface.getFactory().Type().get(impl);
	}

	private static final String modelApiPackage = "spoon.reflect";
	private static final String modelApiImplPackage = "spoon.support.reflect";
	
	private static String replaceApiToImplPackage(String modelInterfaceQName) {
		if (modelInterfaceQName.startsWith(modelApiPackage) == false) {
			throw new SpoonException("The qualified name doesn't belong to Spoon model API package: " + modelApiPackage);
		}
		return modelApiImplPackage + modelInterfaceQName.substring(modelApiPackage.length());
	}
	/**
	 * @param impl the implementation of spoon model element
	 * @return {@link CtInterface} of Spoon model which represents API of the spoon model class. null if there is no implementation.
	 */
	public static CtInterface<?> getInterfaceOfImplementation(CtClass<?> impl) {
		String iface = impl.getQualifiedName();
		if (iface.endsWith(CLASS_SUFFIX) == false || iface.startsWith("spoon.support.reflect.") == false) {
			throw new SpoonException("Unexpected spoon model implementation class: " + impl.getQualifiedName());
		}
		iface = iface.substring(0, iface.length() - CLASS_SUFFIX.length());
		iface = iface.replace("spoon.support.reflect", "spoon.reflect");
		return (CtInterface<?>) impl.getFactory().Type().get(iface);
	}

	private static Factory createFactory(File spoonJavaSourcesDirectory) {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setCommentEnabled(true);
//		// Spoon model interfaces
		Arrays.asList("spoon/reflect/code",
				"spoon/reflect/declaration",
				"spoon/reflect/reference",
				"spoon/support/reflect/declaration",
				"spoon/support/reflect/code",
				"spoon/support/reflect/reference").forEach(path -> {
			launcher.addInputResource(new FileSystemFolder(new File(spoonJavaSourcesDirectory, path)));
		});
		launcher.buildModel();
		return launcher.getFactory();
	}

	/**
	 * @param type can be class or interface of Spoon model element
	 * @return existing or creates and initializes new {@link MetamodelConcept} which represents the `type` 
	 */
	private MetamodelConcept getOrCreateConcept(CtType<?> type) {
		String conceptName = getConceptName(type);
		MetamodelConcept mmConcept = getOrCreate(nameToConcept, conceptName, () -> new MetamodelConcept());
		if (mmConcept.name == null) {
			mmConcept.name = conceptName;
			initializeConcept(type, mmConcept);
		}
		return mmConcept;
	}
	
	/**
	 * is called once for each {@link MetamodelConcept}, to initialize it.
	 * @param type a class or inteface of the spoon model element
	 * @param mmConcept to be initialize {@link MetamodelConcept}
	 */
	private void initializeConcept(CtType<?> type, MetamodelConcept mmConcept) {
		//it is not initialized yet. Do it now
		if (type instanceof CtInterface<?>) {
			CtInterface<?> iface = (CtInterface<?>) type;
			mmConcept.setModelClass(getImplementationOfInterface(iface));
			mmConcept.setModelInterface(iface);
		} else if (type instanceof CtClass<?>) {
			CtClass<?> clazz = (CtClass<?>) type;
			mmConcept.setModelClass(clazz);
			mmConcept.setModelInterface(getInterfaceOfImplementation(clazz));
		} else {
			throw new SpoonException("Unexpected spoon model type: " + type.getQualifiedName());
		}

		//add fields of class
		if (mmConcept.getModelClass() != null) {
			addFieldsOfType(mmConcept, mmConcept.getModelClass());
		}
		//add fields of interface
		if (mmConcept.getModelInterface() != null) {
			//add fields of interface too. They are not added by above call of addFieldsOfType, because the MetamodelConcept already exists in nameToConcept
			addFieldsOfType(mmConcept, mmConcept.getModelInterface());
		}
		//initialize all fields
		mmConcept.getRoleToProperty().forEach((role, mmField) -> {
			//if there are more methods for the same field then choose the one which best matches the field type
			mmField.sortByBestMatch();
			//finally initialize value type of this field
			mmField.setValueType(mmField.detectValueType());
		});
	}

	/**
	 * adds all {@link MetamodelProperty}s of `ctType`
	 * @param mmConcept the owner of to be created fields
	 * @param ctType to be scanned {@link CtType}
	 */
	private void addFieldsOfType(MetamodelConcept mmConcept, CtType<?> ctType) {
		ctType.getTypeMembers().forEach(typeMember -> {
			if (typeMember instanceof CtMethod<?>) {
				CtMethod<?> method = (CtMethod<?>) typeMember;
				CtRole role = getRoleOfMethod(method);
				if (role != null) {
					MetamodelProperty field = mmConcept.getOrCreateMMField(role);
					field.addMethod(method);
				} else {
					mmConcept.otherMethods.add(method);
				}
			}
		});
		addFieldsOfSuperType(mmConcept, ctType.getSuperclass());
		ctType.getSuperInterfaces().forEach(superIfaceRef -> addFieldsOfSuperType(mmConcept, superIfaceRef));
	}

	private static Set<String> EXPECTED_TYPES_NOT_IN_CLASSPATH = new HashSet<>(Arrays.asList(
			"java.lang.Cloneable",
			"spoon.processing.FactoryAccessor",
			"spoon.reflect.visitor.CtVisitable",
			"spoon.reflect.visitor.chain.CtQueryable",
			"spoon.template.TemplateParameter",
			"java.lang.Iterable",
			"java.io.Serializable"));


	/**
	 * add all fields of `superTypeRef` into `mmConcept`
	 * @param concept sub type
	 * @param superTypeRef super type
	 */
	private void addFieldsOfSuperType(MetamodelConcept concept, CtTypeReference<?> superTypeRef) {
		if (superTypeRef == null) {
			return;
		}
		CtType<?> superType = superTypeRef.getDeclaration();
		if (superType == null) {
			if (EXPECTED_TYPES_NOT_IN_CLASSPATH.contains(superTypeRef.getQualifiedName()) == false) {
				throw new SpoonException("Cannot create spoon meta model. The class " + superTypeRef.getQualifiedName() + " is missing class path");
			}
			return;
		}
		//call getOrCreateConcept recursively for super concepts
		MetamodelConcept superConcept = getOrCreateConcept(superType);
		if (superConcept != concept) {
			concept.addSuperConcept(superConcept);
		}
	}

	static <K, V> V getOrCreate(Map<K, V> map, K key, Supplier<V> valueCreator) {
		V value = map.get(key);
		if (value == null) {
			value = valueCreator.get();
			map.put(key, value);
		}
		return value;
	}
	static <T> boolean addUniqueObject(Collection<T> col, T o) {
		if (containsObject(col, o)) {
			return false;
		}
		col.add(o);
		return true;
	}
	static boolean containsObject(Iterable<? extends Object> iter, Object o) {
		for (Object object : iter) {
			if (object == o) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param method to be checked method
	 * @return {@link CtRole} of spoon model method. Looking into all super class/interface implementations of this method
	 */
	public static CtRole getRoleOfMethod(CtMethod<?> method) {
		Factory f = method.getFactory();
		CtAnnotation<PropertyGetter> getter = getInheritedAnnotation(method, f.createCtTypeReference(PropertyGetter.class));
		if (getter != null) {
			return getter.getActualAnnotation().role();
		}
		CtAnnotation<PropertySetter> setter = getInheritedAnnotation(method, f.createCtTypeReference(PropertySetter.class));
		if (setter != null) {
			return setter.getActualAnnotation().role();
		}
		return null;
	}

	/**
	 * @param method a start method
	 * @param annotationType a searched annotation type
	 * @return annotation from the first method in superClass and superInterface hierarchy for the method with required annotationType
	 */
	private static <A extends Annotation> CtAnnotation<A> getInheritedAnnotation(CtMethod<?> method, CtTypeReference<A> annotationType) {
		CtAnnotation<A> annotation = method.getAnnotation(annotationType);
		if (annotation == null) {
			CtType<?> declType = method.getDeclaringType();
			final ClassTypingContext ctc = new ClassTypingContext(declType);
			annotation = declType.map(new AllTypeMembersFunction(CtMethod.class)).map((CtMethod<?> currentMethod) -> {
				if (method == currentMethod) {
					return null;
				}
				if (ctc.isSameSignature(method, currentMethod)) {
					CtAnnotation<A> annotation2 = currentMethod.getAnnotation(annotationType);
					if (annotation2 != null) {
						return annotation2;
					}
				}
				return null;
			}).first();
		}
		return annotation;
	}

	public Factory getFactory() {
		return factory;
	}

	public List<CtType<? extends CtElement>> getAllInstantiableMetamodelInterfaces() {
		SpoonAPI interfaces = new Launcher();
		interfaces.addInputResource("src/main/java/spoon/reflect/declaration");
		interfaces.addInputResource("src/main/java/spoon/reflect/code");
		interfaces.addInputResource("src/main/java/spoon/reflect/reference");
		interfaces.buildModel();

		SpoonAPI implementations = new Launcher();
		implementations.addInputResource("src/main/java/spoon/support/reflect/declaration");
		implementations.addInputResource("src/main/java/spoon/support/reflect/code");
		implementations.addInputResource("src/main/java/spoon/support/reflect/reference");
		implementations.buildModel();

		List<CtType<? extends CtElement>> result = new ArrayList<>();
		for(CtType<? > itf : interfaces.getModel().getAllTypes()) {
			String impl = itf.getQualifiedName().replace("spoon.reflect", "spoon.support.reflect")+"Impl";
			CtType implClass = implementations.getFactory().Type().get(impl);
			if (implClass != null && !implClass.hasModifier(ModifierKind.ABSTRACT)) {
				result.add((CtType<? extends CtElement>) itf);
			}
		}
		return result;
	}

}
