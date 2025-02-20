package spoon.javadoc.api.parsing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import spoon.javadoc.api.JavadocTagType;
import spoon.javadoc.api.StandardJavadocTagType;
import spoon.javadoc.api.elements.JavadocBlockTag;
import spoon.javadoc.api.elements.JavadocCommentView;
import spoon.javadoc.api.elements.JavadocElement;
import spoon.javadoc.api.elements.JavadocInlineTag;
import spoon.javadoc.api.elements.JavadocReference;
import spoon.javadoc.api.elements.JavadocText;
import spoon.javadoc.api.elements.JavadocVisitor;
import spoon.javadoc.api.elements.snippets.JavadocSnippetTag;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;

/**
 * Javadoc specifies comment inheritance for methods. This class provides utilities to look up inherited elements.
 */
public class InheritanceResolver {

	/**
	 * Returns a list with all super methods in
	 * <a
	 * href="https://docs.oracle.com/en/java/javase/19/docs/specs/javadoc/doc-comment-spec.html#method-comments-algorithm">
	 * inheritance order</a>
	 *
	 * @param startType the type to search upwards from
	 * @param target the target method
	 * @return a list with all super methods in inheritance order
	 */
	public List<CtMethod<?>> findSuperMethodsInCommentInheritanceOrder(CtType<?> startType, CtMethod<?> target) {
		List<CtMethod<?>> methods = new ArrayList<>();

		// Look in each directly implemented (or extended) interface in the order they appear following the word
		// implements (or extends) in the type declaration.
		//   Use the first documentation comment found for this method.
		List<CtType<?>> superInterfaces = sortByPosition(startType.getSuperInterfaces())
			.stream()
			.map(CtTypeReference::getTypeDeclaration)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());

		for (CtType<?> type : superInterfaces) {
			CtMethod<?> method = getMethod(type, target);

			if (method != null) {
				methods.add(method);
			}
			// Recursively find super methods in parent types. This is not the way it is
			// specified in <22, but the way it works in the standard doclet.
			methods.addAll(findSuperMethodsInCommentInheritanceOrder(type, target));
		}

		if (!startType.isInterface() && !startType.getQualifiedName().equals(Object.class.getName())) {
			// When Step 2 fails to find a documentation comment and this is a class other than the Object class,
			// but not an interface:

			// If the superclass has a documentation comment for this method, then use it.
			List<CtMethod<?>> superclassMethods = new ArrayList<>();
			addSuperclassMethods(startType, target, superclassMethods);

			// From Java 22 onwards the direct superclass methods come first
			if (startType.getFactory().getEnvironment().getComplianceLevel() >= 22) {
				methods.addAll(0, superclassMethods);
			} else {
				methods.addAll(superclassMethods);
			}
		}

		return methods;
	}

	private void addSuperclassMethods(CtType<?> startType, CtMethod<?> target, List<CtMethod<?>> methods) {
		CtTypeReference<?> superTypeRef = startType.getSuperclass();
		if (superTypeRef == null) {
			return;
		}
		CtType<?> superType = superTypeRef.getTypeDeclaration();
		if (superType == null) {
			return;
		}

		CtMethod<?> method = getMethod(superType, target);
		if (method != null) {
			methods.add(method);
		}
		// If Step 3a failed to find a documentation comment, then recursively apply this entire algorithm
		// to the superclass.
		methods.addAll(findSuperMethodsInCommentInheritanceOrder(superType, target));
	}

	private static CtMethod<?> getMethod(CtType<?> type, CtMethod<?> target) {
		return type.getMethod(
			target.getSimpleName(),
			target.getParameters().stream().map(CtTypedElement::getType).toArray(CtTypeReference<?>[]::new)
		);
	}

	private List<CtTypeReference<?>> sortByPosition(Collection<CtTypeReference<?>> references) {
		return references.stream()
			.sorted(Comparator.comparing(CtElement::getPosition, (o1, o2) -> {
				if (!o1.isValidPosition() && !o2.isValidPosition()) {
					return 0;
				}
				if (!o1.isValidPosition()) {
					return 1;
				}
				if (!o2.isValidPosition()) {
					return -1;
				}
				return Integer.compare(o1.getSourceStart(), o2.getSourceStart());
			}))
			.collect(Collectors.toList());
	}

	/**
	 * Completes the javadoc for a {@link CtElement} with inherited documentation.
	 *
	 * @param element the element to fetch inheritance information for
	 * @param view a view of the existing comments for the element
	 * @return the new and completed javadoc
	 */
	public List<JavadocElement> completeJavadocWithInheritedTags(CtElement element, JavadocCommentView view) {
		if (!(element instanceof CtMethod<?> method)) {
			return view.getElements();
		}
		Set<String> paramsToFind = method.getParameters()
			.stream()
			.map(CtNamedElement::getSimpleName)
			.collect(Collectors.toCollection(LinkedHashSet::new)); // keep decl order
		method.getFormalCtTypeParameters()
			.stream()
			.map(it -> "<" + it.getSimpleName() + ">")
			.forEach(paramsToFind::add);
		view.getBlockTagArguments(StandardJavadocTagType.PARAM, JavadocText.class)
			.forEach(it -> paramsToFind.remove(it.getText()));

		Set<String> throwsToFind = method.getThrownTypes()
			.stream()
			.map(CtTypeInformation::getQualifiedName)
			.collect(Collectors.toCollection(LinkedHashSet::new)); // keep decl order
		view.getBlockTagArguments(StandardJavadocTagType.THROWS, JavadocText.class)
			.forEach(it -> throwsToFind.remove(it.getText()));
		view.getBlockTagArguments(StandardJavadocTagType.EXCEPTION, JavadocText.class)
			.forEach(it -> throwsToFind.remove(it.getText()));

		boolean needsReturn = needsReturn(view);
		boolean needsBody = view.getBody().isEmpty();

		InheritedJavadoc inheritedJavadoc = lookupInheritedDocForMethod(method, paramsToFind, throwsToFind);

		// Order by body -> param -> return -> throws
		List<JavadocElement> newElements = new ArrayList<>();
		if (needsBody) {
			newElements.addAll(inheritedJavadoc.getBody());
		}
		paramsToFind.stream()
			.map(it -> inheritedJavadoc.getParams().get(it))
			.filter(Objects::nonNull)
			.forEach(newElements::add);
		if (needsReturn && inheritedJavadoc.getReturnTag() != null) {
			newElements.add(inheritedJavadoc.getReturnTag());
		}
		throwsToFind.stream()
			.map(it -> inheritedJavadoc.getThrowsClauses().get(it))
			.filter(Objects::nonNull)
			.forEach(newElements::add);

		List<JavadocElement> finalElements = new ArrayList<>(view.getElements());
		finalElements.addAll(newElements);

		JavadocReplaceInheritDocVisitor visitor = new JavadocReplaceInheritDocVisitor(inheritedJavadoc);
		return finalElements.stream()
			.flatMap(it -> it.accept(visitor).stream())
			.collect(Collectors.toList());
	}

	private static boolean needsReturn(JavadocCommentView view) {
		// Block tags are always allowed
		if (!view.getBlockTag(StandardJavadocTagType.RETURN).isEmpty()) {
			return false;
		}
		// As an inline tag, it may only occur at the beginning of a method's main description.
		if (view.getBody().isEmpty()) {
			return true;
		}
		if (!(view.getBody().get(0) instanceof JavadocInlineTag start)) {
			return true;
		}
		return start.getTagType() != StandardJavadocTagType.RETURN;
	}

	private static InheritedJavadoc lookupInheritedDocForMethod(
		CtMethod<?> method,
		Set<String> paramsToFind,
		Set<String> throwsToFind
	) {
		List<CtMethod<?>> targets = new InheritanceResolver()
			.findSuperMethodsInCommentInheritanceOrder(method.getDeclaringType(), method);

		List<JavadocElement> body = new ArrayList<>();
		JavadocInheritanceCollectionVisitor visitor = new JavadocInheritanceCollectionVisitor(
			paramsToFind, throwsToFind
		);

		for (CtMethod<?> target : targets) {
			if (visitor.isFinished() && !body.isEmpty()) {
				break;
			}
			JavadocCommentView view = new JavadocCommentView(JavadocParser.forElement(target));
			if (body.isEmpty() && !view.getBody().isEmpty()) {
				body.addAll(view.getBody());
			}

			for (JavadocElement element : view.getElements()) {
				element.accept(visitor);
			}
		}

		return new InheritedJavadoc(
			body,
			visitor.returnTag,
			visitor.params,
			visitor.throwsClauses
		);
	}

	private static class InheritedJavadoc {

		private final List<JavadocElement> body;
		private final JavadocBlockTag returnTag;
		private final Map<String, JavadocBlockTag> params;
		private final Map<String, JavadocBlockTag> throwsClauses;

		public InheritedJavadoc(
			List<JavadocElement> body,
			JavadocBlockTag returnTag,
			Map<String, JavadocBlockTag> params,
			Map<String, JavadocBlockTag> throwsClauses
		) {
			this.body = body;
			this.returnTag = returnTag;
			this.params = params;
			this.throwsClauses = throwsClauses;
		}

		public JavadocBlockTag getReturnTag() {
			return returnTag;
		}

		public List<JavadocElement> getBody() {
			return Collections.unmodifiableList(body);
		}

		public Map<String, JavadocBlockTag> getParams() {
			return Collections.unmodifiableMap(params);
		}

		public Map<String, JavadocBlockTag> getThrowsClauses() {
			return Collections.unmodifiableMap(throwsClauses);
		}
	}

	private static class JavadocInheritanceCollectionVisitor implements JavadocVisitor<Void> {

		private final Set<String> missingParameters;
		private final Set<String> missingThrowsClauses;
		private final Map<String, JavadocBlockTag> params;
		private final Map<String, JavadocBlockTag> throwsClauses;
		private JavadocBlockTag returnTag;

		public JavadocInheritanceCollectionVisitor(Set<String> missingParameters, Set<String> missingThrowsClauses) {
			this.missingParameters = new HashSet<>(missingParameters);
			this.missingThrowsClauses = new HashSet<>(missingThrowsClauses);
			this.returnTag = null;
			this.params = new HashMap<>();
			this.throwsClauses = new HashMap<>();
		}

		public boolean isFinished() {
			return missingParameters.isEmpty() && missingThrowsClauses.isEmpty() && returnTag != null;
		}

		@Override
		public Void defaultValue() {
			return null;
		}

		@Override
		public Void visitInlineTag(JavadocInlineTag tag) {
			if (returnTag == null && tag.getTagType() == StandardJavadocTagType.RETURN) {
				returnTag = new JavadocBlockTag(tag.getElements(), StandardJavadocTagType.RETURN);
			}
			return JavadocVisitor.super.visitInlineTag(tag);
		}

		@Override
		public Void visitBlockTag(JavadocBlockTag tag) {
			if (tag.getTagType() == StandardJavadocTagType.PARAM) {
				tag.getArgument(JavadocText.class)
					.map(JavadocText::getText)
					.filter(missingParameters::contains)
					.ifPresent(arg -> {
						missingParameters.remove(arg);
						params.put(arg, tag);
					});
			}
			if (
				tag.getTagType() == StandardJavadocTagType.THROWS
				|| tag.getTagType() == StandardJavadocTagType.EXCEPTION
			) {
				tag.getArgument(JavadocReference.class)
					.map(it -> it.getReference().toString())
					.filter(missingThrowsClauses::contains)
					.ifPresent(arg -> {
						missingThrowsClauses.remove(arg);
						throwsClauses.put(arg, tag);
					});
			}
			if (returnTag == null && tag.getTagType() == StandardJavadocTagType.RETURN) {
				returnTag = tag;
			}
			return JavadocVisitor.super.visitBlockTag(tag);
		}
	}

	private static class JavadocReplaceInheritDocVisitor implements JavadocVisitor<List<JavadocElement>> {

		private final InheritedJavadoc inheritedJavadoc;
		private JavadocTagType currentType;

		private JavadocReplaceInheritDocVisitor(InheritedJavadoc inheritedJavadoc) {
			this.inheritedJavadoc = inheritedJavadoc;
		}

		@Override
		public List<JavadocElement> defaultValue() {
			throw new IllegalStateException("Default value called, a case was probably missed?");
		}

		@Override
		public List<JavadocElement> visitInlineTag(JavadocInlineTag tag) {
			if (tag.getTagType() == StandardJavadocTagType.INHERIT_DOC) {
				if (currentType == null) {
					return inheritedJavadoc.getBody();
				}
				Optional<JavadocText> argument = tag.getArgument(JavadocText.class);
				if (argument.isEmpty()) {
					return List.of(tag);
				}
				if (currentType == StandardJavadocTagType.PARAM) {
					JavadocBlockTag blockTag = inheritedJavadoc.getParams().get(argument.get().getText());
					if (blockTag != null) {
						return blockTag.getElements();
					}
				}
				if (currentType == StandardJavadocTagType.THROWS || currentType == StandardJavadocTagType.EXCEPTION) {
					JavadocBlockTag blockTag = inheritedJavadoc.getThrowsClauses().get(argument.get().getText());
					if (blockTag != null) {
						return blockTag.getElements();
					}
				}
			}
			return List.of(tag);
		}

		@Override
		public List<JavadocElement> visitBlockTag(JavadocBlockTag tag) {
			currentType = tag.getTagType();
			List<JavadocElement> newElements = new ArrayList<>();

			for (JavadocElement element : tag.getElements()) {
				newElements.addAll(element.accept(this));
			}

			return List.of(new JavadocBlockTag(newElements, tag.getTagType()));
		}

		@Override
		public List<JavadocElement> visitSnippet(JavadocSnippetTag snippet) {
			return List.of(snippet);
		}

		@Override
		public List<JavadocElement> visitText(JavadocText text) {
			return List.of(text);
		}

		@Override
		public List<JavadocElement> visitReference(JavadocReference reference) {
			return List.of(reference);
		}

	}

}
