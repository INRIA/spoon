package spoon.test.template.testclasses.replace;

import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.ElementPrinterHelper;
import spoon.reflect.visitor.TokenWriter;

public class OldPattern {

	//Here are parameters this Pattern
	public static class Parameters {
		public boolean useStartKeyword;
		
		public String startKeyword;
		public boolean startPrefixSpace; 
		public String start; 
		public boolean startSuffixSpace; 
		public boolean nextPrefixSpace; 
		public String next; 
		public boolean nextSuffixSpace; 
		public boolean endPrefixSpace; 
		public String end;
		
		public CtBlock<Void> statements;
		
		public CtTypeReference<?> entityType;
		public CtTypeReference<?> itemType;
		
		public CtInvocation<Iterable<Item>> getIterable;
	}
	
	void patternModel(Parameters params) throws Exception {
		//This is a model of this Pattern
		if(params.useStartKeyword) {
			printer.writeSpace().writeKeyword(params.startKeyword).writeSpace();
		}
		try (spoon.reflect.visitor.ListPrinter lp = elementPrinterHelper.createListPrinter(
				params.startPrefixSpace, 
				params.start, 
				params.startSuffixSpace, 
				params.nextPrefixSpace, 
				params.next, 
				params.nextSuffixSpace, 
				params.endPrefixSpace, 
				params.end
			)) {
			for (Item item : params.getIterable.S()) {
				lp.printSeparatorIfAppropriate();
				params.statements.S();
			}
		}
	}
	
	/**
	 * @param factory a to be used factory
	 * @return a Pattern instance of this Pattern
	 */
	public static Pattern createPattern(Factory factory) {
		return PatternBuilder
			//Create a pattern from all statements of OldPattern_ParamsInNestedType#patternModel
			.create(factory, OldPattern.class, model->model.setBodyOfMethod("patternModel"))
			.configureParameters(pb->pb
					.parametersByVariable("params", "item")
					.parameter("statements").setContainerKind(ContainerKind.LIST)
			)
			.configureAutomaticParameters()
			.configureInlineStatements(ls -> ls.byVariableName("useStartKeyword"))
			.build();
	}

	private ElementPrinterHelper elementPrinterHelper;
	private TokenWriter printer;
}
