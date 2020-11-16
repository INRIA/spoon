package examples.spoon.blocks;

import java.util.List;
import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtReturn;
import spoon.reflect.visitor.filter.TypeFilter;

public class BlockChecks {

	@Architecture(modelNames = "blocks")
	public void noEmptyBlocks(CtModel model) {
		Precondition<CtBlock<?>> pre = Precondition.of(DefaultElementFilter.BLOCKS.getFilter());
		Constraint<CtBlock<?>> con = Constraint.of(new ExceptionError<>("Found an empty codeblock "), v -> v.getStatements().isEmpty());
		ArchitectureTest.of(pre, con).runCheck(model);
	}

	@Architecture(modelNames = "blocks")
	public void onlyLastStatementIsReturn(CtModel model) {
		Precondition<CtBlock<?>> pre = Precondition.of(DefaultElementFilter.BLOCKS.getFilter());
		Constraint<CtBlock<?>> con = Constraint.of(new ExceptionError<>("Found an empty codeblock "), v -> checkLastStatementIsReturn(v));
		ArchitectureTest.of(pre, con).runCheck(model);
	}

	private boolean checkLastStatementIsReturn(CtBlock<?> block) {
		// get all return statements
		List<CtReturn<?>> returns = block.getElements(new TypeFilter<>(CtReturn.class));
		switch (returns.size()) {
			// the block has no returns so we dont need to check more.
			case 0: return true;
			// now we check if the last statement is a real return and no throw expression.
			case 1: return block.getLastStatement().equals(returns.get(0));
			default: return false;
		}
	}
}
