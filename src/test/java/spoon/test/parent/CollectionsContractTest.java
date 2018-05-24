package spoon.test.parent;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import spoon.SpoonException;
import spoon.experimental.modelobs.ActionBasedChangeListenerImpl;
import spoon.experimental.modelobs.action.Action;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.CtVisitable;
import spoon.test.SpoonTestHelpers;
import spoon.test.metamodel.MMTypeKind;
import spoon.test.metamodel.MetamodelConcept;
import spoon.test.metamodel.MetamodelProperty;
import spoon.test.metamodel.SpoonMetaModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.createFactory;

import static spoon.test.parent.ParentContractTest.createCompatibleObject;

// check that kind of collection returned by collection getter
@RunWith(Parameterized.class)
public class CollectionsContractTest<T extends CtVisitable> {

	private static Factory factory = createFactory();
	private static final List<CtType<? extends CtElement>> allInstantiableMetamodelInterfaces = SpoonTestHelpers.getAllInstantiableMetamodelInterfaces();

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() throws Exception {
		return createReceiverList();
	}

	public static Collection<Object[]> createReceiverList() throws Exception {
		metamodel = new SpoonMetaModel(new File("src/main/java"));
		allProblems = new ArrayList<>();
		List<Object[]> values = new ArrayList<>();
		for (MetamodelConcept mmC : metamodel.getConcepts()) {
			if (mmC.getKind() == MMTypeKind.LEAF) {
				values.add(new Object[] { mmC });
			}
		}
		return values;
	}
	
	@AfterClass
	public static void reportAllProblems() {
		System.out.println("Expected collection handling:");
		System.out.println(allExpected.stream().sorted().collect(Collectors.joining("\n")));
		if (allProblems.size() > 0) {
			System.out.println("-----------------------------");
			System.out.println("Wrong collection handling:");
			System.out.println(allProblems.stream().sorted().collect(Collectors.joining("\n")));
		}
	}
	
	private static SpoonMetaModel metamodel;

	@Parameterized.Parameter(0)
	public MetamodelConcept mmConcept;
	
	enum CollectionKind {
		//read only collection
		READ_ONLY,
		//modifiaeble detached copy - changes doesn't influence model 
		MUTABLE_DETACHED,
		//modified attached - changes influence model. The parent is not set or change event is not sent
		MUTABLE_ATTACHED_INCORRECT,
		//modified attached - changes influence model. The parent is set, change event is sent
		MUTABLE_ATTACHED_CORRECT
	}
	
	static Set<CtRole> ignoredRoles = new HashSet<>(Arrays.asList(CtRole.POSITION, CtRole.MODIFIER));
	static List<String> allProblems = new ArrayList<>();
	static List<String> allExpected = new ArrayList<>();
	
	@Test
	public void testContract() throws Throwable {
		// contract: check type of collection returned by getter
		// read only / modifiable-detached / modifiable-attached
		Class<? extends CtElement> elementClass = (Class<? extends CtElement>) mmConcept.getModelInterface().getActualClass();
		
		List<String> problems = new ArrayList<>();
		List<String> expected = new ArrayList<>();
		
		for (MetamodelProperty mmProperty : mmConcept.getRoleToProperty().values()) {
			if (mmProperty.getValueContainerType() == ContainerKind.SINGLE || ignoredRoles.contains(mmProperty.getRole())) {
				continue;
			}
			CtElement[] arguments = new CtElement[] {
					(CtElement) createCompatibleObject(mmProperty.getItemValueType()),
					(CtElement) createCompatibleObject(mmProperty.getItemValueType())
				};
			
			RoleHandler roleHandler = RoleHandlerHelper.getRoleHandler(elementClass, mmProperty.getRole());
			CollectionKind[] colKind;
			
			CtElement testedElement = factory.Core().create(elementClass);
			if (elementClass.equals(CtTypeReference.class)) {
				testedElement = factory.Type().createReference(ArrayList.class);
			}
			
			try {
				colKind = detectCollectionKind(mmProperty, roleHandler, testedElement, arguments);
			} catch (Throwable e) {
				problems.add("Failed check of;" + mmConcept + "#" + mmProperty.getName() + ". " + e.getClass().getSimpleName() + " : " + e.getMessage());
				continue;
			}
			String colKindStr = Arrays.asList(colKind).stream().map(CollectionKind::name).collect(Collectors.joining(", ", "[", "]"));
			if (mmProperty.isDerived()) {
				//derived properties should be unsettable
				if (containsOnly(colKind, CollectionKind.READ_ONLY) == false) {
					//report this problem
					problems.add("derived;" +  colKindStr + mmProperty.getName() + " of " + mmConcept);
				} else {
					//collect expected collection
					expected.add("derived;" + colKindStr + mmProperty.getName() + " of " + mmConcept);
				}
			} else {
				//normal properties should be attached correct
				if (containsOnly(colKind, CollectionKind.MUTABLE_ATTACHED_CORRECT) == false) {
					//report this problem
					problems.add("normal;" + colKindStr + mmProperty.getName() + " of " + mmConcept);
				} else {
					expected.add("normal;" + colKindStr + mmProperty.getName() + " of " + mmConcept);
				}
			}
		}
		
		allExpected.addAll(expected);
		
		if (problems.size() > 0) {
			allProblems.addAll(problems);
			fail(String.join("\n", problems));
		}
	}
	
	private boolean containsOnly(CollectionKind[] cks, CollectionKind expected) {
		for (CollectionKind collectionKind : cks) {
			if (collectionKind != expected) {
				return false;
			}
		}
		return true;
	}

	private CollectionKind[] detectCollectionKind(MetamodelProperty mmProperty, RoleHandler roleHandler, CtElement testedElement, CtElement... argument) {
		switch(roleHandler.getContainerKind()) {
		case MAP:
			return detectCollectionKindOfMap(mmProperty, roleHandler, testedElement, argument);
		case LIST:
		case SET:
			return detectCollectionKindOfCollection(mmProperty, roleHandler, testedElement, argument);
		case SINGLE:
			throw new SpoonException("Single is not tested here");
		}
		throw new SpoonException("Unexpected container kind " + roleHandler.getContainerKind());
	}
	
	static class ChangeListener extends ActionBasedChangeListenerImpl {
		List<Action> actions = new ArrayList<>();
		@Override
		public void onAction(Action action) {
			actions.add(action);
		}
	}
	
	//Some roles have type List, but behaves as Set internally.
	Set<CtRole> setRoles = new HashSet<>(Arrays.asList(CtRole.MODULE_DIRECTIVE,
			CtRole.SERVICE_TYPE,
			CtRole.EXPORTED_PACKAGE,
			CtRole.OPENED_PACKAGE,
			CtRole.REQUIRED_MODULE,
			CtRole.PROVIDED_SERVICE, CtRole.BOUND, CtRole.VALUE));

	private CollectionKind[] detectCollectionKindOfCollection(MetamodelProperty mmProperty, RoleHandler roleHandler, CtElement testedElement, CtElement... arguments) {
		if (roleHandler.getRole()==CtRole.MODULE_DIRECTIVE) {
			this.getClass();
		}
		CollectionKind[] ck = new CollectionKind[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			CtElement argument = arguments[i];
			CtElement parentOfArgument = getParentOrNull(argument);
			Collection col;
			ChangeListener changeListener = new ChangeListener();
			testedElement.getFactory().getEnvironment().setModelChangeListener(changeListener);
			col = (Collection) roleHandler.getValue(testedElement);
			try {
				col.add(argument);
			} catch (UnsupportedOperationException e) {
				ck[i] = CollectionKind.READ_ONLY;
				boolean isSet;
				try {
					Collection c = roleHandler.asCollection(testedElement);
					isSet = c instanceof Set || setRoles.contains(roleHandler.getRole());
					c.add(argument);
				} catch (UnsupportedOperationException e2) {
					if (mmProperty.isDerived()) {
						//OK, it is not allowed to add values into derived collection
						continue;
					}
					throw e2;
				}
				Collection col2 = (Collection) roleHandler.getValue(testedElement);
				if (mmProperty.isUnsettable()) {
					//the setter of unsettable property can be called, but it changes nothing
					assertFalse(col2.contains(argument));
					assertSame(parentOfArgument, getParentOrNull(argument));
					assertTrue(changeListener.actions.isEmpty());
				} else {
					if (i > 0 && isSet) {
						//do not check second add into a Set. The second argument is often equal to first argument,
						//so it is not added to Set ... but it is not interesting for this test
						continue;
					}
					assertTrue(col2.contains(argument));
					assertTrue(argument.isParentInitialized());
					//getParent is not enough, because CtInvocation has CtExecutable which gets some values internally
					assertTrue(argument.hasParent(testedElement));
					assertTrue(changeListener.actions.size() > 0);
				}
				continue;
			}
			Collection col2 = (Collection) roleHandler.getValue(testedElement);
			if (col2.contains(argument) == false) {
				ck[i] = CollectionKind.MUTABLE_DETACHED;
				continue;
			}
			if (argument.isParentInitialized() && argument.getParent() == testedElement) {
				//the parent was set - OK
				if (changeListener.actions.size() > 0) {
					//the change event was sent - OK
					ck[i] = CollectionKind.MUTABLE_ATTACHED_CORRECT;
					continue;
				}
			}
			ck[i] = CollectionKind.MUTABLE_ATTACHED_INCORRECT;
		}
		return ck;
	}

	private CtElement getParentOrNull(CtElement argument) {
		if (argument.isParentInitialized()) {
			return argument.getParent();
		}
		return null;
	}

	private CollectionKind[] detectCollectionKindOfMap(MetamodelProperty mmProperty, RoleHandler roleHandler, CtElement testedElement, CtElement... arguments) {
		Map<String, CtElement> col = (Map) roleHandler.getValue(testedElement);
		CollectionKind[] ck = new CollectionKind[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			String key = "x"+i;
			CtElement argument = arguments[i];
			CtElement parentOfArgument = getParentOrNull(argument);
			ChangeListener changeListener = new ChangeListener();
			testedElement.getFactory().getEnvironment().setModelChangeListener(changeListener);
			try {
				col.put(key, argument);
			} catch (UnsupportedOperationException e) {
				ck[i] = CollectionKind.READ_ONLY;
				try {
					roleHandler.asMap(testedElement).put(key, argument);
				} catch (UnsupportedOperationException e2) {
					if (mmProperty.isDerived()) {
						//OK, it is not allowed to put values into derived collection
						continue;
					}
					throw e2;
				}
				col = (Map) roleHandler.getValue(testedElement);
				if (mmProperty.isUnsettable()) {
					//the setter of unsettable property can be called, but it changes nothing
					assertNull(col.get(key));
					assertSame(parentOfArgument, getParentOrNull(argument));
					assertTrue(changeListener.actions.isEmpty());
				} else {
					assertSame(argument, col.get(key));
					assertTrue(argument.isParentInitialized());
					assertSame(testedElement, argument.getParent());
					assertTrue(changeListener.actions.size() > 0);
				}
				
				continue;
			}
			Map<String, CtElement> col2 = (Map<String, CtElement>) roleHandler.getValue(testedElement);
			if (col2.get(key) != argument) {
				ck[i] = CollectionKind.MUTABLE_DETACHED;
				continue;
			}
			if (argument.isParentInitialized() && argument.getParent() == testedElement) {
				//the parent was set - OK
				if (changeListener.actions.size() > 0) {
					//the change event was sent - OK
					ck[i] = CollectionKind.MUTABLE_ATTACHED_CORRECT;
					continue;
				}
			}
			ck[i] = CollectionKind.MUTABLE_ATTACHED_INCORRECT;
		}
		return ck;
	}

}
