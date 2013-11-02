package spoon.support.compiler;

import java.util.ArrayList;
import java.util.List;

public class SnippetCompilationError extends RuntimeException {

	private static final long serialVersionUID = 7805276558728052328L;

	public List<String> problems;

	public SnippetCompilationError(List<String> problems) {
		super();
		this.problems = problems;
	}

	public SnippetCompilationError(String string) {
		super();
		this.problems = new ArrayList<String>();
		this.problems.add(string);
			
	}

	
}
