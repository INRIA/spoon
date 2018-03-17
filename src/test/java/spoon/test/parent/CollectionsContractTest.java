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
		if (allProblems.size() > 0) {
			System.out.println(String.join("\n", allProblems));
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
	
	@Test
	public void testContract() throws Throwable {
		// contract: check type of collection returned by getter
		// read only / modifiable-detached / modifiable-attached
		Class<? extends CtElement> elementClass = (Class<? extends CtElement>) mmConcept.getModelInterface().getActualClass();
		CtElement testedElement = factory.Core().create(elementClass);
		if (elementClass.equals(CtTypeReference.class)) {
			testedElement = factory.Type().createReference(ArrayList.class);
		}
		
		List<String> problems = new ArrayList<>();
		
		for (MetamodelProperty mmProperty : mmConcept.getRoleToProperty().values()) {
			if (mmProperty.getValueContainerType() == ContainerKind.SINGLE || ignoredRoles.contains(mmProperty.getRole())) {
				continue;
			}
			Object argument = createCompatibleObject(mmProperty.getItemValueType());
			
			RoleHandler roleHandler = RoleHandlerHelper.getRoleHandler(elementClass, mmProperty.getRole());
			CollectionKind colKind = detectCollectionKind(roleHandler, testedElement, (CtElement) argument);
			if (colKind != null) {
				if (mmProperty.isDerived()) {
					//derived properties should be unsettable
					if (colKind != CollectionKind.READ_ONLY) {
						//report this problem
						problems.add("derived;" +mmConcept + "#" + mmProperty.getName() + ";" + colKind.name());
					}
				} else {
					//normal properties should be attached correct
					if (colKind != CollectionKind.MUTABLE_ATTACHED_CORRECT) {
						//report this problem
						problems.add("normal;" + mmConcept + "#" + mmProperty.getName() + ";" + colKind.name());
					}
				}
			} else {
				problems.add("Failed check of;" + mmConcept + "#" + mmProperty.getName());
			}
		}
		
		if (problems.size() > 0) {
			allProblems.addAll(problems);
			fail(String.join("\n", problems));
		}
	}

	private CollectionKind detectCollectionKind(RoleHandler roleHandler, CtElement testedElement, CtElement argument) {
		switch(roleHandler.getContainerKind()) {
		case MAP:
			return detectCollectionKindOfMap(roleHandler, testedElement, argument);
		case LIST:
		case SET:
			return detectCollectionKindOfCollection(roleHandler, testedElement, argument);
		case SINGLE:
			return null;
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

	private CollectionKind detectCollectionKindOfCollection(RoleHandler roleHandler, CtElement testedElement, CtElement argument) {
		ChangeListener changeListener = new ChangeListener();
		testedElement.getFactory().getEnvironment().setModelChangeListener(changeListener);
		Collection col;
		try {
			col = (Collection) roleHandler.getValue(testedElement);
		} catch (Exception e) {
			return null;
		}
		try {
			col.add(argument);
		} catch (UnsupportedOperationException e) {
			return CollectionKind.READ_ONLY;
		}
		Collection col2 = (Collection) roleHandler.getValue(testedElement);
		if (col2.contains(argument) == false) {
			return CollectionKind.MUTABLE_DETACHED;
		}
		if (argument.isParentInitialized() && argument.getParent() == testedElement) {
			//the parent was set - OK
			if (changeListener.actions.size() > 0) {
				//the change event was sent - OK
				return CollectionKind.MUTABLE_ATTACHED_CORRECT;
			}
		}
		return CollectionKind.MUTABLE_ATTACHED_INCORRECT;
	}

	private CollectionKind detectCollectionKindOfMap(RoleHandler roleHandler, CtElement testedElement, CtElement argument) {
		ChangeListener changeListener = new ChangeListener();
		testedElement.getFactory().getEnvironment().setModelChangeListener(changeListener);
		
		Map<String, CtElement> col = (Map) roleHandler.getValue(testedElement);
		try {
			col.put("x", argument);
		} catch (UnsupportedOperationException e) {
			return CollectionKind.READ_ONLY;
		}
		Map<String, CtElement> col2 = (Map<String, CtElement>) roleHandler.getValue(testedElement);
		if (col2.get("x") != argument) {
			return CollectionKind.MUTABLE_DETACHED;
		}
		if (argument.isParentInitialized() && argument.getParent() == testedElement) {
			//the parent was set - OK
			if (changeListener.actions.size() > 0) {
				//the change event was sent - OK
				return CollectionKind.MUTABLE_ATTACHED_CORRECT;
			}
		}
		return CollectionKind.MUTABLE_ATTACHED_INCORRECT;
	}

}
