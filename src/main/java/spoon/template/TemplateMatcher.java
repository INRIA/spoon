/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
public class TemplateMatcher {

	private List<CtInvocation<?>> getMethods(CtClass<? extends Template<?>> root) {
		CtExecutableReference<?> methodRef = root.getFactory().Executable()
				.createReference(root.getFactory().Type().createReference(TemplateParameter.class), root.getFactory().Type().createTypeParameterReference("T"), "S");
		List<CtInvocation<?>> meths = Query.getElements(root, new InvocationFilter(methodRef));

		return meths;
	}

	private List<String> getTemplateNameParameters(CtClass<? extends Template<?>> templateType) {
		final List<String> ts = new ArrayList<>();
		final Collection<String> c = Parameters.getNames(templateType);
		ts.addAll(c);
		return ts;
	}

	private List<CtTypeReference<?>> getTemplateTypeParameters(final CtClass<? extends Template<?>> templateType) {

		final List<CtTypeReference<?>> ts = new ArrayList<>();
		final Collection<String> c = Parameters.getNames(templateType);
		new CtScanner() {
			@Override
			public void visitCtTypeParameterReference(CtTypeParameterReference reference) {
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

	private List<CtFieldReference<?>> getVarargs(CtClass<? extends Template<?>> root, List<CtInvocation<?>> variables) {
		List<CtFieldReference<?>> fields = new ArrayList<>();
		for (CtFieldReference<?> field : root.getReference().getAllFields()) {
			if (field.getType().getActualClass() == CtStatementList.class) {
				boolean alreadyAdded = false;
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

	private List<CtElement> finds = new ArrayList<>();

	private Map<Object, Object> matches = new HashMap<>();

	private List<String> names;

	private CtClass<? extends Template<?>> templateType;

	private List<CtTypeReference<?>> typeVariables;

	private List<CtFieldReference<?>> varArgs;

	private List<CtInvocation<?>> variables;

	/**
	 * Constructs a matcher for a given template.
	 *
	 */
	public TemplateMatcher(CtElement templateRoot) {
		this.templateType = templateRoot.getParent(CtClass.class);
		this.templateRoot = templateRoot;
		variables = getMethods(templateType);
		typeVariables = getTemplateTypeParameters(templateType);
		names = getTemplateNameParameters(templateType);
		varArgs = getVarargs(templateType, variables);
		this.templateType = templateType;
	}

	private boolean addMatch(Object template, Object target) {
		Object inv = matches.get(template);
		Object o = matches.put(template, target);
		return (null == inv) || inv.equals(o);
	}

	private CtElement checkListStatements(List<?> teList) {
		for (Object tem : teList) {
			if (variables.contains(tem) && (tem instanceof CtInvocation)) {
				CtInvocation<?> listCand = (CtInvocation<?>) tem;
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
	public List<CtElement> find(final CtElement targetRoot) {
		new CtScanner() {
			@Override
			public void scan(CtElement element) {
				if (match(element, templateRoot)) {
					finds.add(element);
					// matches.clear();
				}
				super.scan(element);
			}
		}.scan(targetRoot);

		return finds;
	}

	private ParameterMatcher findParameterMatcher(CtElement declaration, String name) throws InstantiationException, IllegalAccessException {
		if (declaration == null) {
			return new DefaultParameterMatcher();
		}
		CtClass<?> clazz = null;
		try {
			clazz = declaration.getParent(CtClass.class);
		} catch (ParentNotInitializedException e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		}
		if (clazz == null) {
			return new DefaultParameterMatcher();
		}

		Collection<CtFieldReference<?>> fields = clazz.getReference().getAllFields();

		CtFieldReference<?> param = null;
		for (CtFieldReference<?> fieldRef : fields) {
			Parameter p = fieldRef.getDeclaration().getAnnotation(Parameter.class);
			if (p == null) {
				continue; // not a parameter.
			}
			String proxy = p.value();
			if (proxy != "") {
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

		if (param == null) {
			throw new IllegalStateException("Parameter not defined " + name + "at " + declaration.getPosition());
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

	private ParameterMatcher getParameterInstance(CtFieldReference<?> param) throws InstantiationException, IllegalAccessException {
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

	/*
	 * Made private to hide the Objects.
	 */
	private boolean helperMatch(Object target, Object template) {
		if ((target == null) && (template == null)) {
			return true;
		}
		if ((target == null) || (template == null)) {
			return false;
		}
		if (variables.contains(template) || typeVariables.contains(template)) {
			// TODO: upcall the parameter matcher if defined
			boolean add = invokeCallBack(target, template);
			if (add) {
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
			boolean ok = matchNames(tRef.getSimpleName(), ((CtReference) target).getSimpleName());
			if (ok && !template.equals(target)) {
				boolean remove = !invokeCallBack(target, template);
				if (remove) {
					matches.remove(tRef.getSimpleName());
					return false;
				}
				return true;
			}
		}

		if (template instanceof CtNamedElement) {
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
				ParameterMatcher instance = findParameterMatcher(named, named.getSimpleName());
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

	private boolean isCurrentTemplate(Object object, CtElement inMulti) {
		if (object instanceof CtInvocation<?>) {
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
	 * Matches a target program sub-tree against a template. Once this method
	 * has been called, {@link #getMatches()} will give the matching parts if
	 * any.
	 *
	 * @param targetRoot
	 * 		the target to be tested for match
	 * @param templateRoot
	 * 		the template to match against
	 * @return true if matches
	 * @see #getMatches()
	 */
	private boolean match(CtElement targetRoot, CtElement templateRoot) {
		return helperMatch(targetRoot, templateRoot);
	}

	@SuppressWarnings("unchecked")
	private boolean matchCollections(Collection<?> target, Collection<?> template) {
		List<Object> teList = new ArrayList<>(template);
		List<Object> taList = new ArrayList<>(target);

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

			for (int te = 0, ta = 0; (te < teList.size()) && (ta < taList.size()); te++, ta++) {
				if (!helperMatch(taList.get(ta), teList.get(te))) {
					return false;
				}
			}
			return true;
		}
		for (int te = 0, ta = 0; (te < teList.size()) && (ta < taList.size()); te++, ta++) {

			if (isCurrentTemplate(teList.get(te), inMulti)) {
				if (te + 1 >= teList.size()) {
					multi.addAll(taList.subList(te, taList.size()));
					CtStatementList tpl = templateType.getFactory().Core().createStatementList();
					tpl.setStatements((List<CtStatement>) (List<?>) multi);
					if (!invokeCallBack(tpl, inMulti)) {
						return false;
					}
					boolean ret = addMatch(inMulti, multi);
					return ret;
				}
				te++;
				while ((te < teList.size()) && (ta < taList.size()) && !helperMatch(taList.get(ta), teList.get(te))) {
					multi.add(taList.get(ta));
					ta++;
				}
				CtStatementList tpl = templateType.getFactory().Core().createStatementList();
				tpl.setStatements((List<CtStatement>) (List<?>) multi);
				if (!invokeCallBack(tpl, inMulti)) {
					return false;
				}
				addMatch(inMulti, tpl);
				// update inMulti
				inMulti = nextListStatement(teList, inMulti);
				multi = new ArrayList<>();
			} else {
				if (!helperMatch(taList.get(ta), teList.get(te))) {
					return false;
				}
				if (!(ta + 1 < taList.size()) && (inMulti != null)) {
					CtStatementList tpl = templateType.getFactory().Core().createStatementList();
					for (Object o : multi) {
						tpl.addStatement((CtStatement) o);
					}
					if (!invokeCallBack(tpl, inMulti)) {
						return false;
					}
					addMatch(inMulti, tpl);
					// update inMulti
					inMulti = nextListStatement(teList, inMulti);
					multi = new ArrayList<>();
				}
			}
		}
		return true;
	}

	private boolean matchNames(String name, String tname) {

		try {
			for (String pname : names) {
				// pname = pname.replace("_FIELD_", "");
				if (name.contains(pname)) {
					String newName = name.replace(pname, "(.*)");
					Pattern p = Pattern.compile(newName);
					Matcher m = p.matcher(tname);
					if (!m.matches()) {
						return false;
					}
					// TODO: fix with parameter from @Parameter
					// boolean ok = addMatch(getBindedParameter(pname),
					// m.group(1));
					boolean ok = addMatch(pname, m.group(1));
					if (!ok) {
						Launcher.LOGGER.debug("incongruent match");
						return false;
					}
					return true;
				}
			}
		} catch (RuntimeException e) {
			// //fall back on dumb way to do it.
			// if
		}
		return name.equals(tname);
	}

	private CtElement nextListStatement(List<?> teList, CtElement inMulti) {
		if (inMulti == null) {
			return checkListStatements(teList);
		}
		List<?> teList2 = new ArrayList<Object>(teList);
		if (inMulti instanceof CtInvocation) {
			teList2.remove(inMulti);
		} else if (inMulti instanceof CtVariable) {
			CtVariable<?> var = (CtVariable<?>) inMulti;
			for (Iterator<?> iter = teList2.iterator(); iter.hasNext();) {
				CtVariable<?> teVar = (CtVariable<?>) iter.next();
				if (teVar.getSimpleName().equals(var.getSimpleName())) {
					iter.remove();
				}
			}
		}
		return checkListStatements(teList2);
	}

}
