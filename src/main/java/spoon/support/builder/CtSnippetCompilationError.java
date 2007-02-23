package spoon.support.builder;

import java.util.ArrayList;
import java.util.List;

public class CtSnippetCompilationError extends RuntimeException {

	private static final long serialVersionUID = 7805276558728052328L;

	public List<String> problems;

	public CtSnippetCompilationError(List<String> problems) {
		super();
		this.problems = problems;
	}

	public CtSnippetCompilationError(String string) {
		super();
		this.problems = new ArrayList<String>();
		this.problems.add(string);
			
	}

	
}
