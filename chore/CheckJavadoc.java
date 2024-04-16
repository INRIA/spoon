///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS fr.inria.gforge.spoon:spoon-core:RELEASE
//DEPS fr.inria.gforge.spoon:spoon-javadoc:RELEASE
//DEPS org.slf4j:slf4j-nop:1.7.36
//JAVA 17+

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import spoon.Launcher;
import spoon.javadoc.api.StandardJavadocTagType;
import spoon.javadoc.api.elements.JavadocBlockTag;
import spoon.javadoc.api.elements.JavadocCommentView;
import spoon.javadoc.api.elements.JavadocElement;
import spoon.javadoc.api.elements.JavadocInlineTag;
import spoon.javadoc.api.elements.JavadocReference;
import spoon.javadoc.api.elements.JavadocText;
import spoon.javadoc.api.elements.JavadocVisitor;
import spoon.javadoc.api.elements.snippets.JavadocSnippetTag;
import spoon.javadoc.api.parsing.InheritanceResolver;
import spoon.javadoc.api.parsing.JavadocParser;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

public class CheckJavadoc {

  private static final Path CWD = Path.of("").toAbsolutePath();

  private static final Set<String> EXCLUDED_TYPES = Set.of(
      "spoon.reflect.visitor.CtAbstractVisitor",
      "spoon.reflect.visitor.CtBiScannerDefault",
      "spoon.reflect.visitor.CtInheritanceScanner",
      "spoon.reflect.visitor.CtScanner",
      "spoon.reflect.visitor.CtVisitor",
      "spoon.support.compiler.jdt.JDTTreeBuilder",
      "spoon.support.compiler.jdt.ParentExiter",
      "spoon.support.visitor.clone.CloneVisitor",
      "spoon.support.visitor.replace.ReplacementVisitor"
  );

  private final Map<ViolationType, Integer> violationCounts = new HashMap<>();

  private void check(CtModel model, Factory factory) {
    CtTypeReference<Object> object = factory.Type().objectType();
    InheritanceResolver inheritanceResolver = new InheritanceResolver();

    // Ensure all classes extend Object
    for (CtType<?> type : model.getAllTypes()) {
      if (type.getSuperclass() == null) {
        type.setSuperclass(object);
      }
    }

    for (CtExecutable<?> element : model.getElements(new TypeFilter<>(CtExecutable.class))) {
      if (element.isImplicit()) {
        continue;
      }
      if (!(element instanceof CtTypeMember member) || !needsComment(member)) {
        continue;
      }
      if (element.getComments().stream().filter(it -> it instanceof CtJavaDoc).count() > 1) {
        violation(element, ViolationType.MULTIPLE_JAVADOC_COMMENTS);
      }
      List<JavadocElement> elements = JavadocParser.forElement(element);
      List<JavadocElement> inheritedElements = inheritanceResolver.completeJavadocWithInheritedTags(
        element,
        new JavadocCommentView(elements)
      );
      if (inheritedElements.isEmpty()) {
        violationNoJavadoc(element);
        continue;
      }
      JavadocCommentView doc = new JavadocCommentView(inheritedElements);
      if (element instanceof CtConstructor<?>) {
        violationUndocumentedParameter(element, doc);
      } else if (element instanceof CtMethod<?>) {
        violationUndocumentedParameter(element, doc);
        violationMissingReturn(element, doc);
      }
    }
  }

  private void violationNoJavadoc(CtExecutable<?> executable) {
    CtTypeReference<Object> object = executable.getFactory().Type().objectType();

    // Do not require javadoc for methods of java.lang.Object
    if (executable instanceof CtMethod<?> method) {
      boolean declaredInObject = method.getTopDefinitions()
        .stream()
        .map(CtTypeMember::getDeclaringType)
        .map(CtType::getReference)
        .anyMatch(object::equals);

      if (declaredInObject) {
        return;
      }
    }

    // Do not require javadoc for empty constructors
    if (executable instanceof CtConstructor<?> constructor) {
      if (constructor.getParameters().isEmpty()) {
        return;
      }
    }
    violation(executable, ViolationType.MISSING_JAVADOC);
  }

  private boolean needsComment(CtTypeMember element) {
    CtElement current = element;
    while (current != null) {
      if (current.isImplicit()) {
        return false;
      }
      if (current instanceof CtTypeMember member) {
        if (!member.isPublic()) {
          return false;
        }
      }
      if (current instanceof CtType<?> type && EXCLUDED_TYPES.contains(type.getQualifiedName())) {
        return false;
      }
      current = current.getParent();
    }
    return true;
  }

  private void violationUndocumentedParameter(CtExecutable<?> executable, JavadocCommentView doc) {
    List<String> params = executable.getParameters()
      .stream()
      .map(CtNamedElement::getSimpleName)
      .collect(Collectors.toCollection(ArrayList::new));

    if (executable instanceof CtFormalTypeDeclarer formalTypeDeclarer) {
      formalTypeDeclarer.getFormalCtTypeParameters().forEach(it -> params.add("<" + it.getSimpleName() + ">"));
    }

    Set<String> encounteredParameters = new HashSet<>();
    for (JavadocBlockTag tag : doc.getBlockTag(StandardJavadocTagType.PARAM)) {
      Optional<String> maybeParameterName = tag.getArgument(JavadocText.class).map(JavadocText::getText);
      if (maybeParameterName.isEmpty()) {
        violation(executable, ViolationType.PARAMETER_WITHOUT_NAME);
        continue;
      }
      String parameterName = maybeParameterName.orElseThrow();
      if (!params.remove(parameterName)) {
        violation(executable, ViolationType.UNKNOWN_PARAMETER, parameterName);
      }
      if (!encounteredParameters.add(parameterName)) {
        violation(executable, ViolationType.DUPLICATED_PARAM, parameterName);
      }
      if (isBlank(tag.getElements().subList(1, tag.getElements().size()))) {
        violation(executable, ViolationType.MISSING_PARAMETER_DESCRIPTION, parameterName);
      }
    }

    for (String param : params) {
      violation(executable, ViolationType.MISSING_PARAMETER, param);
    }
  }

  private void violationMissingReturn(CtExecutable<?> executable, JavadocCommentView doc) {
    if (executable.getType().equals(executable.getFactory().Type().voidPrimitiveType())) {
      return;
    }
    List<JavadocElement> returnTagValues = new ArrayList<>(doc.getBlockTag(StandardJavadocTagType.RETURN));
    for (JavadocElement element : doc.getBody()) {
      if (element instanceof JavadocInlineTag inline && inline.getTagType() == StandardJavadocTagType.RETURN) {
        returnTagValues.add(element);
      }
    }

    if (returnTagValues.isEmpty()) {
      violation(executable, ViolationType.MISSING_RETURN);
      return;
    }
    // TODO: Disallow more than one return tag
    JavadocElement tag = returnTagValues.get(0);
    if (isBlank(tag)) {
      violation(executable, ViolationType.MISSING_RETURN_DESCRIPTION);
    }
  }

  private void violation(CtExecutable<?> context, ViolationType type, Object... args) {
    String message = type.getTemplate().formatted(args);
    Path fileAsPath = context.getPosition().getFile().toPath();
    String file = fileAsPath.startsWith(CWD) ? CWD.relativize(fileAsPath).toString() : fileAsPath.toString();
    int line = context.getPosition().getLine();
    int column = context.getPosition().getColumn();
    String methodName = context.getSimpleName() + context.getParameters()
      .stream()
      .map(it -> it.getType().getSimpleName())
      .collect(Collectors.joining(",", "(", ")"));

    violationCounts.merge(type, 1, Math::addExact);

    System.out.println("[ERROR] " + file + ":" + line + ":" + column + ": " + message + " in " + methodName);
  }

  private static boolean isBlank(JavadocElement element) {
    return element.accept(new IsBlankVisitor());
  }

  private static boolean isBlank(Collection<JavadocElement> elements) {
    return elements.stream().allMatch(CheckJavadoc::isBlank);
  }

  public static void main(String... args) {
    Launcher launcher = new Launcher();
    launcher.getEnvironment().setComplianceLevel(17);
    launcher.addInputResource("src/main/java");
    launcher.addInputResource("spoon-javadoc/src/main/java");
    CtModel model = launcher.buildModel();

    CheckJavadoc checker = new CheckJavadoc();
    checker.check(model, launcher.getFactory());

    System.out.println("Violations per category");
    for (var entry : checker.violationCounts.entrySet().stream().sorted(Entry.comparingByValue()).toList()) {
      System.out.println(entry);
    }

    int errorCount = checker.violationCounts.values().stream().mapToInt(Integer::intValue).sum();
    System.out.println("There are " + errorCount + " errors reported by Checkstyle");
  }

  public enum ViolationType {
    MISSING_JAVADOC("Missing javadoc comment"),
    TOO_MANY_RETURN("More than one @return tag"),
    MISSING_RETURN("Missing @return tag"),
    MISSING_RETURN_DESCRIPTION("Missing description for @return"),
    MISSING_PARAMETER("Missing @param '%s' tag"),
    MISSING_PARAMETER_DESCRIPTION("Missing description for @param '%s'"),
    PARAMETER_WITHOUT_NAME("Parameter name missing in @param tag"),
    DUPLICATED_PARAM("Parameter '%s' occurred multiple times"),
    UNKNOWN_PARAMETER("Nonexistant parameter name '%s' in @param tag "),
    MULTIPLE_JAVADOC_COMMENTS("More than one javadoc comment found for element");

    private final String template;

    ViolationType(String template) {
      this.template = template;
    }

    public String getTemplate() {
      return template;
    }
  }

  private static class IsBlankVisitor implements JavadocVisitor<Boolean> {

    @Override
    public Boolean defaultValue() {
      return true;
    }

    @Override
    public Boolean visitInlineTag(JavadocInlineTag tag) {
      return tag.getElements().stream().allMatch(it -> it.accept(this));
    }

    @Override
    public Boolean visitBlockTag(JavadocBlockTag tag) {
      return tag.getElements().stream().allMatch(it -> it.accept(this));
    }

    @Override
    public Boolean visitSnippet(JavadocSnippetTag snippet) {
      return false;
    }

    @Override
    public Boolean visitText(JavadocText text) {
      return text.getText().isBlank();
    }

    @Override
    public Boolean visitReference(JavadocReference reference) {
      return false;
    }
  }
}
