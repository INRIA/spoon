package spoon.test.template.testclasses.replace;

import java.util.function.Consumer;

import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern.PatternBuilderHelper;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;

public class NewPattern {

	/**
	 * The body of this method contains a model of transformed code 
	 */
	private void patternModel(OldPattern.Parameters params) throws Exception {
		elementPrinterHelper.printList(params.getIterable.S(), 
				params.startPrefixSpace, 
				params.start, 
				params.startSuffixSpace, 
				params.nextPrefixSpace, 
				params.next, 
				params.nextSuffixSpace, 
				params.endPrefixSpace, 
				params.end,
				v -> {
					params.statements.S();
				});
	}
	
	/**
	 * Creates a Pattern for this model
	 */
	public static Pattern createPatternFromNewPattern(Factory factory) {
		CtType<?> type = factory.Type().get(NewPattern.class);
		return PatternBuilder.create(new PatternBuilderHelper(type).setBodyOfMethod("patternModel").getPatternElements())
			.configurePatternParameters()
			.configurePatternParameters(pb -> {
				pb.parameter("statements").setContainerKind(ContainerKind.LIST);
			})
			.build();
	}
	
	/*
	 * Helper type members
	 */
	
	private ElementPrinterHelper elementPrinterHelper;
	
	interface Entity {
		Iterable<Item> $getItems$();
	}
	
	interface ElementPrinterHelper {
		void printList(Iterable<Item> $getItems$, 
				boolean startPrefixSpace, String start, boolean startSufficSpace, 
				boolean nextPrefixSpace, String next, boolean nextSuffixSpace, 
				boolean endPrefixSpace, String end, 
				Consumer<Item> consumer);
	}
}
