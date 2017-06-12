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
package spoon.template;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.InvocationFilter;
import spoon.support.template.DefaultParameterMatcher;
import spoon.support.template.ParameterMatcher;
import spoon.support.template.Parameters;
import spoon.support.util.RtHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class defines an engine for matching a template to pieces of code.
 */
public class TemplateMatcher implements Filter<CtElement> {

	/**
	 * Searches for all invocations of {@link TemplateParameter#S()} in "root", a CtClass model of {@link Template}
	 *
	 * @param root CtClass model of {@link Template}
	 */
	private List<CtInvocation<?>> getMethods(CtClass<? extends Template<?>> root) {
		CtExecutableReference<?> methodRef = root.getFactory().Executable()
				.createReference(root.getFactory().Type().createReference(TemplateParameter.class), root.getFactory().Type().createTypeParameterReference("T"), "S");
		List<CtInvocation<?>> meths = Query.getElements(root, new InvocationFilter(methodRef));

		return meths;
	}

	/**
	 * @param templateType CtClass model of {@link Template}
	 * @return list of all names of template parameters.
	 * It includes parameters typed by {@link TemplateParameter} and parameters with annotation {@link Parameter}.
	 */
	private List<String> getTemplateNameParameters(CtClass<? extends Template<?>> templateType) {
		return Parameters.getNames(templateType);
	}

	/**
	 * Collects all AST nodes, which has to be substituted, because they represents a template parameter declared by field annotated by {@link Parameter}
	 *
	 * TODO test it: This code is probably wrong, or I did not understood it...
	 * @param templateType CtClass model of {@link Template}
	 * @return ??
	 */
	private List<CtTypeReference<?>> getTemplateTypeParameters(final CtClass<? extends Template<?>> templateType) {

		final List<CtTypeReference<?>> ts = new ArrayList<>();
		final Collection<String> c = Parameters.getNames(templateType);
		new CtScanner() {
			@Override
			public void visitCtTypeParameterReference(CtTypeParameterReference reference) {
				//BUG? Parameters#isParameterSource() avoids CtTypeParameterReference ... so is it correct?
				if (c.contains(reference.getSimpleName())) {
					ts.add(reference);
				}
			}

			@Override
			public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
				if (c.contains(reference.getSimpleName())) {
					ts.add(reference);
				}
			}

		}.scan(templateType);
		return ts;
	}

	/**
	 * Looks for fields of type {@link CtStatementList} in the template and returns these fields,
	 * BUG: ? It does not care about annotation Parameter, so there is actually not possible to generate field of type CtStatementList as part of code generate by template
	 * @param root CtClass model of {@link Template}
	 * @param variables
	 * @return returns for fields of type {@link CtStatementList} in the template
	 */
	private List<CtFieldReference<?>> getVarargs(CtClass<? extends Template<?>> root, List<CtInvocation<?>> variables) {
		List<CtFieldReference<?>> fields = new ArrayList<>();
		for (CtFieldReference<?> field : root.getAllFields()) {
			if (field.getType().getActualClass() == CtStatementList.class) {
				boolean alreadyAdded = false;
				//BUG: alreadyAdded can be never true, because `variables` are collected from fields of type `TemplateParameters`,
				//so their type can never be CtStatementList ...
				for (CtInvocation<?> invocation : variables) {
					alreadyAdded |= ((CtFieldAccess<?>) invocation.getTarget()).getVariable().getDeclaration().equals(field);
				}
				if (!alreadyAdded) {
					fields.add(field);
				}
			}
		}
		return fields;
	}

	/** the template itself */
	private CtElement templateRoot;

	/**
	 * Holds matches of template parameters (keys) to nodes from matched target
	 */
	private Map<Object, Object> matches = new HashMap<>();

	/**
	 * Names of all template parameters declared in `templateType` and it's super types/interfaces.
	 * There are
	 * 1) names of all fields of type {@link TemplateParameter}
	 * 2) value of annotation {@link Parameter#value()} applied to an parameter field
	 * 3) name of an field annotated with {@link Parameter} with undefined {@link Parameter#value()}
	 */
	private List<String> names;

	/**
	 * The {@link CtClass} model of java class {@link Template},
	 * which contains to be matched elements defined by `templateRoot`
	 */
	private CtClass<? extends Template<?>> templateType;

	/**
	 * All the {@link CtTypeReference}s from `templateType`, whose name is a parameter name
	 * (is contained in `names`)
	 */
	private List<CtTypeReference<?>> typeVariables;

	/**
	 * List of all fields of type {@link CtStatementList},
	 * which are not covered by `variables`
	 */
	private List<CtFieldReference<?>> varArgs;

	/**
	 * List of all invocations of {@link TemplateParameter#S()}) in scope of `templateType`
	 */
	private List<CtInvocation<?>> variables;

	/**
	 * Constructs a matcher for a given template.
	 *
	 * @param templateRoot the template to match against
	 *
	 */
	@SuppressWarnings("unchecked")
	public TemplateMatcher(CtElement templateRoot) {
		this.templateType = templateRoot.getParent(CtClass.class);
		this.templateRoot = templateRoot;
		variables = getMethods(templateType);
		typeVariables = getTemplateTypeParameters(templateType);
		names = getTemplateNameParameters(templateType);
		varArgs = getVarargs(templateType, variables);
		//check that template matches itself
		if (helperMatch(this.templateRoot, this.templateRoot) == false) {
			throw new SpoonException("TemplateMatcher was unable to find itself, it certainly indicates a bug. Please revise your template or report an issue.");
		}
	}

	/**
	 * adds a target element which matches and template element
	 * @param template an object template. It can be:
	 * - CtInvocation - represents an variable
	 * - CtTypeReference - represents an type variable
	 * - String - represents a matching name in a reference
	 * - CtParameter - ??
	 * - ...?
	 * @param target an matching target object
	 * @return false if there was already a different match to the same `template` object
	 */
	private boolean addMatch(Object template, Object target) {
		Object inv = matches.get(template);
		Object o = matches.put(template, target);
		/*
		 * BUG: it always returns true, because inv==o. It is contract of Map.
		 * The correct code is probably:
		 *
		 * Object inv = matches.get(template);
		 * if (inv != null && inv.equals(target) == false) {
		 *   //another value would be inserted. TemplateMatcher does not support matching of different values for the same template parameter
		 *   return false;
		 * }
		 * matches.put(template, target)
		 * return true;
		 * Object inv = matches.put(template, target);
		 * return (null == inv) || inv.equals(target);
		 *
		 * But callers of addMatch does not handle return value consistently to this contract ...
		 */
		return (null == inv) || inv.equals(o);
	}

	/**
	 * Detects whether `teList` contains a multiElement template parameter
	 * @param teList a list of template nodes
	 * @return a first found multiElement template parameter
	 */
	private CtElement checkListStatements(List<?> teList) {
		for (Object tem : teList) {
			//TODO: simplify, if it is same like an item of variables, then it must be a CtInvocation
			if (containsSame(variables, tem) && (tem instanceof CtInvocation)) {
				CtInvocation<?> listCand = (CtInvocation<?>) tem;
				//BUG: it returns true only for parameters of type TemplateParameter, because interface TemplateParameter can never be a subtype of something else
				boolean ok = listCand.getFactory().Type().createReference(TemplateParameter.class).isSubtypeOf(listCand.getTarget().getType());
				return ok ? listCand : null;
			}
			if (tem instanceof CtVariable) {
				CtVariable<?> var = (CtVariable<?>) tem;
				String name = var.getSimpleName();
				for (CtFieldReference<?> f : varArgs) {
					if (f.getSimpleName().equals(name)) {
						return f.getDeclaration();
					}
				}
			}
		}

		return null;
	}

	/**
	 * Finds all target program sub-trees that correspond to a template.
	 *
	 * @param targetRoot
	 * 		the target to be tested for match
	 * @return the matched elements
	 */
	public <T extends CtElement> List<T> find(final CtElement targetRoot) {
		return targetRoot.filterChildren(this).list();
	}

	/**
	 *
	 * returns an appropriate ParameterMatcher defined in a template parameter, or else a default one
	 *
	 * if a template parameter (field annotated with @Parameter) whose name (field name) is a substring of the template name, it also works
	 */
	private ParameterMatcher findParameterMatcher(CtNamedElement templateDeclaration) throws InstantiationException, IllegalAccessException {
		if (templateDeclaration == null) {
			return new DefaultParameterMatcher();
		}
		String name = templateDeclaration.getSimpleName();
		CtClass<?> clazz = null;
		try {
			clazz = templateDeclaration.getParent(CtClass.class);
		} catch (ParentNotInitializedException e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		}
		if (clazz == null) {
			return new DefaultParameterMatcher();
		}

		Collection<CtFieldReference<?>> fields = clazz.getAllFields();

		CtFieldReference<?> param = null;
		for (CtFieldReference<?> fieldRef : fields) {
			Parameter p = fieldRef.getDeclaration().getAnnotation(Parameter.class);
			if (p == null) {
				continue; // not a parameter.
			}
			String proxy = p.value();
			if (!"".equals(proxy)) {
				if (name.contains(proxy)) {
					param = fieldRef;
					break;
				}
			}

			if (name.contains(fieldRef.getSimpleName())) {
				param = fieldRef;
				break;
			}
			// todo: check for field hack.
		}
		return getParameterInstance(param);
	}

	@SuppressWarnings("unused")
	private String getBindedParameter(String pname) {
		final String[] x = new String[1]; // HACK! jeje
		x[0] = pname;
		new CtScanner() {
			@Override
			public <T> void visitCtField(CtField<T> f) {
				Parameter p = f.getAnnotation(Parameter.class);
				if ((p != null) && p.value().equals(x[0])) {
					x[0] = f.getSimpleName();
					return;
				}
				super.visitCtField(f);
			}
		}.scan(templateType);

		return x[0];
	}

	/**
	 * Returns all the matches in a map where the keys are the corresponding
	 * template parameters. The {@link #match(CtElement, CtElement)} method must
	 * have been called before.
	 */
	private Map<Object, Object> getMatches() {
		return matches;
	}

	/** returns a specific ParameterMatcher corresponding to the field acting as template parameter */
	private ParameterMatcher getParameterInstance(CtFieldReference<?> param) throws InstantiationException, IllegalAccessException {
		if (param == null) {
			// return a default impl
			return new DefaultParameterMatcher();
		}
		Parameter anParam = param.getDeclaration().getAnnotation(Parameter.class);
		if (anParam == null) {
			// Parameter not annotated. Probably is a TemplateParameter. Just
			// return a default impl
			return new DefaultParameterMatcher();
		}
		Class<? extends ParameterMatcher> pm = anParam.match();
		ParameterMatcher instance = pm.newInstance();
		return instance;
	}

	/**
	 * Detects whether `template` AST node and `target` AST node are matching.
	 * This method is called for each node of to be matched template
	 * and for appropriate node of `target`
	 *
	 * @param target actually checked AST node from target model
	 * @param template actually checked AST node from template
	 *
	 * @return true if template matches this node, false if it does not matches
	 *
	 * note: Made private to hide the Objects.
	 */
	private boolean helperMatch(Object target, Object template) {
		if ((target == null) && (template == null)) {
			return true;
		}
		if ((target == null) || (template == null)) {
			return false;
		}
		if (containsSame(variables, template) || containsSame(typeVariables, template)) {
			/*
			 * we are just matching a template parameter.
			 * Check that defined ParameterMatcher matches the target too
			 */
			boolean add = invokeCallBack(target, template);
			if (add) {
				//ParameterMatcher matches the target too, add that match
				//BUG: if addMatch returns false, then report it as
				//Launcher.LOGGER.debug("incongruent match");
				return addMatch(template, target);
			}
			return false;
		}
		if (target.getClass() != template.getClass()) {
			return false;
		}
		if ((template instanceof CtTypeReference) && template.equals(templateType.getReference())) {
			return true;
		}
		if ((template instanceof CtPackageReference) && template.equals(templateType.getPackage())) {
			return true;
		}
		if (template instanceof CtReference) {
			CtReference tRef = (CtReference) template;
			/*
			 * Check whether name of a template reference matches with name of target reference
			 * after replacing of variables in template name
			 */
			boolean ok = matchNames(tRef.getSimpleName(), ((CtReference) target).getSimpleName());
			/*
			 * TODO comment: In what case the template.equals(target) == true??
			 */
			if (ok && !template.equals(target)) {
				boolean remove = !invokeCallBack(target, template);
				if (remove) {
					/*
					 * BUG: if ParameterMatcher does not agrees then it should remove a match,
					 * but the match was inserted with different key by matchNames!
					 * The best solution would be to add the match only after it is agreed by ParameterMatcher.
					 * It avoids replacing of correct match by incorrect match in `matches`
					 */
					matches.remove(tRef.getSimpleName());
					return false;
				}
				return true;
			}
		}

		if (template instanceof CtNamedElement) {
			/*
			 * same code like above, with same bugs
			 * TODO use a shared function called from both places and fix it once.
			 */
			CtNamedElement named = (CtNamedElement) template;
			boolean ok = matchNames(named.getSimpleName(), ((CtNamedElement) target).getSimpleName());
			if (ok && !template.equals(target)) {
				boolean remove = !invokeCallBack(target, template);
				if (remove) {
					matches.remove(named.getSimpleName());
					return false;
				}
			}
		}

		if (template instanceof Collection) {
			return matchCollections((Collection<?>) target, (Collection<?>) template);
		}

		if (template instanceof Map) {
			if (template.equals(target)) {
				return true;
			}

			Map<?, ?> temMap = (Map<?, ?>) template;
			Map<?, ?> tarMap = (Map<?, ?>) target;

			if (!temMap.keySet().equals(tarMap.keySet())) {
				return false;
			}

			return matchCollections(tarMap.values(), temMap.values());
		}

		if (template instanceof CtBlock<?>) {
			final List<CtStatement> statements = ((CtBlock) template).getStatements();
			if (statements.size() == 1 && statements.get(0) instanceof CtInvocation) {
				final CtInvocation ctStatement = (CtInvocation) statements.get(0);
				if ("S".equals(ctStatement.getExecutable().getSimpleName()) && CtBlock.class.equals(ctStatement.getType().getActualClass())) {
					return true;
				}
			}
		}

		if (target instanceof CtElement) {
			//TODO cache relevant fields for a spoon model class in a static Map
			for (Field f : RtHelper.getAllFields(target.getClass())) {
				f.setAccessible(true);
				if (Modifier.isStatic(f.getModifiers())) {
					continue;
				}
				if (f.getName().equals("parent")) {
					continue;
				}
				if (f.getName().equals("position")) {
					continue;
				}
				if (f.getName().equals("docComment")) {
					continue;
				}
				if (f.getName().equals("factory")) {
					continue;
				}
				if (f.getName().equals("comments")) {
					continue;
				}
				if (f.getName().equals("metadata")) {
					continue;
				}
				try {
					if (!helperMatch(f.get(target), f.get(template))) {
						return false;
					}
				} catch (IllegalAccessException ignore) {
				}
			}
			return true;
		} else if (target instanceof String) {
			return matchNames((String) template, (String) target);
		} else {
			return target.equals(template);
		}
	}

	/**
	 * invokes {@link ParameterMatcher} associated to the `template` (= template parameter)
	 * @param target a potentially matching element
	 * @param template a matching parameter, which may define extra {@link ParameterMatcher}
	 * @return true if {@link ParameterMatcher} of `template` matches on `target`
	 *
	 * TODO: rename this method to #checkParameterMatcher
	 */
	private boolean invokeCallBack(Object target, Object template) {
		try {
			if (template instanceof CtInvocation) {
				CtFieldAccess<?> param = (CtFieldAccess<?>) ((CtInvocation<?>) template).getTarget();
				ParameterMatcher instance = getParameterInstance(param.getVariable());
				return instance.match(this, (CtInvocation<?>) template, (CtElement) target);
			} else if (template instanceof CtReference) {
				// Get parameter
				CtReference ref = (CtReference) template;
				ParameterMatcher instance;
				if (ref.getDeclaration() == null || ref.getDeclaration().getAnnotation(Parameter.class) == null) {
					instance = new DefaultParameterMatcher();
				} else {
					Parameter param = ref.getDeclaration().getAnnotation(Parameter.class);
					instance = param.match().newInstance();
				}
				return instance.match(this, (CtReference) template, (CtReference) target);
			} else if (template instanceof CtNamedElement) {
				CtNamedElement named = (CtNamedElement) template;
				ParameterMatcher instance = findParameterMatcher(named);
				return instance.match(this, (CtElement) template, (CtElement) target);
			} else {
				// Should not happen
				throw new RuntimeException();
			}
		} catch (InstantiationException e) {
			Launcher.LOGGER.error(e.getMessage(), e);
			return true;
		} catch (IllegalAccessException e) {
			Launcher.LOGGER.error(e.getMessage(), e);
			return true;
		}
	}

	/**
	 * Detects whether `object` represent a template variable `inMulti`
	 * TODO: rename to isCurrentTemplateParameter ?
	 */
	private boolean isCurrentTemplate(Object object, CtElement inMulti) {
		if (object instanceof CtInvocation<?>) {
			//BUG: should use == instead of equals?
			return object.equals(inMulti);
		}
		if (object instanceof CtParameter) {
			CtParameter<?> param = (CtParameter<?>) object;
			for (CtFieldReference<?> varArg : varArgs) {
				if (param.getSimpleName().equals(varArg.getSimpleName())) {
					return varArg.equals(inMulti);
				}
			}
		}
		return false;
	}

	/**
	 * Matches a target program sub-tree against a template.
	 *
	 * @param targetRoot
	 * 		the target to be tested for match
	 * @return true if matches
	 */
	@Override
	public boolean matches(CtElement targetRoot) {
		if (targetRoot == templateRoot) {
			// This case can occur when we are scanning the entire package for example see TemplateTest#testTemplateMatcherWithWholePackage
			// Correct template matches itself of course, but client does not want that
			return false;
		}
		return helperMatch(targetRoot, templateRoot);
	}

	@SuppressWarnings("unchecked")
	private boolean matchCollections(Collection<?> target, Collection<?> template) {
		final List<Object> teList = new ArrayList<>(template);
		final List<Object> taList = new ArrayList<>(target);

		// inMulti keeps the multiElement templateVariable we are at
		CtElement inMulti = nextListStatement(teList, null);

		// multi keeps the values to assign to inMulti
		List<Object> multi = new ArrayList<>();

		if (null == inMulti) {
			// If we are not looking at template with multiElements
			// the sizes should then be the same
			if (teList.size() != taList.size()) {
				return false;
			}
			//TODO simplify the cycle. Use one index for both lists
			for (int te = 0, ta = 0; (te < teList.size()) && (ta < taList.size()); te++, ta++) {
				if (!helperMatch(taList.get(ta), teList.get(te))) {
					return false;
				}
			}
			return true;
		}
		for (int te = 0, ta = 0; (te < teList.size()) && (ta < taList.size()); te++, ta++) {

			if (isCurrentTemplate(teList.get(te), inMulti)) {
				//te index points to template parameter, which accepts multiple statements
				if (te + 1 >= teList.size()) {
					//it is the last parameter of template list. Add all remaining target list items
					multi.addAll(taList.subList(te, taList.size()));
					//create statement list and add match
					CtStatementList tpl = templateType.getFactory().Core().createStatementList();
					tpl.setStatements((List<CtStatement>) (List<?>) multi);
					if (!invokeCallBack(tpl, inMulti)) {
						return false;
					}
					boolean ret = addMatch(inMulti, multi);
					//BUG: if addMatch returns false, then report it as
					//Launcher.LOGGER.debug("incongruent match");
					return ret;
				}
				//there is next template parameter. Move to it
				te++;
				//adds all target list items, which are not matching to next template parameter, to the actual template parameter
				/*
				 * IMPROVE:
				 * - do not check (te < teList.size()), because it is already tested above
				 * - get teList.get(te) into local variable
				 * then it will be clear that this cycle iterates over taList only
				 */
				while ((te < teList.size()) && (ta < taList.size()) && !helperMatch(taList.get(ta), teList.get(te))) {
					multi.add(taList.get(ta));
					ta++;
				}
				//BUG: te-- ?? Or do not increase te above at all?

				//we have found first target parameter, which fits to next template parameter
				//create statement list for previous parameter and add it's match
				CtStatementList tpl = templateType.getFactory().Core().createStatementList();
				tpl.setStatements((List<CtStatement>) (List<?>) multi);
				if (!invokeCallBack(tpl, inMulti)) {
					return false;
				}
				//BUG: why we do not care about return value here?
				// if addMatch returns false, then report it as
				//Launcher.LOGGER.debug("incongruent match");
				addMatch(inMulti, tpl);
				// update inMulti
				inMulti = nextListStatement(teList, inMulti);
				multi = new ArrayList<>();
			} else {
				//parameter on te index is not a multivalue statement
				if (!helperMatch(taList.get(ta), teList.get(te))) {
					return false;
				}
				//TODO: make condition more readable. E.g. ta+1>=taList.size()
				if (!(ta + 1 < taList.size()) && (inMulti != null)) {
					/*
					 * there is no next target item in taList,
					 * but there is still some template parameter,
					 * which expects one
					 */
					CtStatementList tpl = templateType.getFactory().Core().createStatementList();
					//BUG: it looks like `multi` must be always empty
					//TODO: delete this cycle
					for (Object o : multi) {
						tpl.addStatement((CtStatement) o);
					}
					//so it returns empty statement list - might be OK
					if (!invokeCallBack(tpl, inMulti)) {
						return false;
					}
					//BUG: why we do not care about return value here?
					// if addMatch returns false, then report it as
					//Launcher.LOGGER.debug("incongruent match");
					addMatch(inMulti, tpl);
					// update inMulti
					inMulti = nextListStatement(teList, inMulti);
					multi = new ArrayList<>();
					/*
					 * BUG: if there is next `inMulti` template parameter,
					 * then it is not checked whether it matches empty statement list,
					 * because ta+1==taList.size() and it finishes the main cycle.
					 */
				}
			}
		}
		return true;
	}

	/**
	 * Detects if `templateName` (a name from template) matches with `elementName` (a name from target),
	 * after replacing parameter names in `templateName`
	 * @param templateName the name from template
	 * @param elementName the name from target
	 * @return true if matching
	 *
	 * TODO fix BUG: refactor this method and callers, to call addMatch correctly
	 */
	private boolean matchNames(String templateName, String elementName) {

			for (String templateParameterName : names) {
				// pname = pname.replace("_FIELD_", "");
				if (templateName.contains(templateParameterName)) {
					String newName = templateName.replace(templateParameterName, "(.*)");
					Pattern p = Pattern.compile(newName);
					Matcher m = p.matcher(elementName);
					if (!m.matches()) {
						return false;
					}
					// TODO: fix with parameter from @Parameter
					// boolean ok = addMatch(getBindedParameter(pname),
					// m.group(1));
					boolean ok = addMatch(templateParameterName, m.group(1));
					if (!ok) {
						Launcher.LOGGER.debug("incongruent match");
						return false;
					}
					return true;
				}
			}
		return templateName.equals(elementName);
	}

	/**
	 * returns next ListStatement parameter from teList
	 *
	 * BUG: it works only for first and second parameter. The 3rd call  will return first parameter again!
	 *
	 * @param teList
	 * @param inMulti TODO replace by int index
	 * @return TODO return int index of found statement or -1 if there is no next one
	 */
	private CtElement nextListStatement(List<?> teList, CtElement inMulti) {
		if (inMulti == null) {
			return checkListStatements(teList);
		}
		List<?> teList2 = new ArrayList<Object>(teList);
		if (inMulti instanceof CtInvocation) {
			//BUG: we should use removeSame, which uses "==" instead of "equals"
			teList2.remove(inMulti);
		} else if (inMulti instanceof CtVariable) {
			CtVariable<?> var = (CtVariable<?>) inMulti;
			for (Iterator<?> iter = teList2.iterator(); iter.hasNext();) {
				CtVariable<?> teVar = (CtVariable<?>) iter.next();
				if (teVar.getSimpleName().equals(var.getSimpleName())) {
					iter.remove();
					//BUG? Should it really remove all variables of the same name, or should it remove first found?
				}
			}
		}
		return checkListStatements(teList2);
	}

	/**
	 * Is used instead of Collection#contains(Object),
	 * which uses Object#equals operator,
	 * which returns true even for not same objects.
	 *
	 * @param collection to be checked collection
	 * @param item to be searched object
	 * @return true if `collection` contains instance of `item`.
	 */
	private static boolean containsSame(Iterable<? extends Object> collection, Object item) {
		for (Object object : collection) {
			if (object == item) {
				return true;
			}
		}
		return false;
	}
}
