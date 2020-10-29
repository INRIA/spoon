package spoon.architecture.style;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.ErrorCollector;
import spoon.architecture.preconditions.VisibilityFilter;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.declaration.CtType;

public class DocChecks {
	// no rule has no exceptions.
	private List<String> aAnExceptions = new ArrayList<String>() {
		{
			add("university");
		}
	};

	@Architecture(modelNames = "srcmodel")
	public void aAnMistakes(CtModel srcModel) {
		// contract: check all javadoc if there is any a followed by {a,e,o,u,i}. Exceptions are given in aAnExceptions. Feel free to add some more.
		// This rule is more for showcasing the possibilities and never needed.
		Precondition<CtJavaDoc> pre = Precondition.of(DefaultElementFilter.JAVA_DOCS.getFilter());
		ErrorCollector<CtJavaDoc> collector = new ErrorCollector<>("There is an a/an rule violation");
		Constraint<CtJavaDoc> con = Constraint.of(collector, doc -> !hasAnMistake(doc));
		ArchitectureTest.of(pre, con).runCheck(srcModel);
		collector.printCollectedErrors();
	}

	private boolean hasAnMistake(CtJavaDoc doc) {
		Pattern aMistake = Pattern.compile("[\\s.]a\\s+[aeuoi]", Pattern.CASE_INSENSITIVE);
		Pattern aNMistake = Pattern.compile("[\\s.]an\\s+[^aeuoi]", Pattern.CASE_INSENSITIVE);
		if (aAnExceptions.stream().anyMatch(v -> doc.getContent().contains(v))) {
			return false;
		}
		return aMistake.matcher(doc.getContent().toLowerCase()).find() && aNMistake.matcher(doc.getContent().toLowerCase()).find();
	}

	@Architecture(modelNames = "srcmodel")
	public void classDocumentation(CtModel srcModel) {
		// contract: every type has a proper documentation (30 chars at least).
		Precondition<CtType<?>> pre = Precondition.of(DefaultElementFilter.TYPES.getFilter(), VisibilityFilter.isPublic());
		ErrorCollector<CtType<?>> collector = new ErrorCollector<>("There is a type without documentation. Every public class must have documentation (at least 30 chars) ");
		Constraint<CtType<?>> con = Constraint.of(collector, type -> type.getDocComment().length() > 30);
		ArchitectureTest.of(pre, con).runCheck(srcModel);
		collector.printCollectedErrors();
	}
}
