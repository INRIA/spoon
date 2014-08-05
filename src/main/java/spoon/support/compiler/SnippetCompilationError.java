package spoon.support.compiler;

import java.util.ArrayList;
import java.util.List;

import spoon.SpoonException;

public class SnippetCompilationError extends SpoonException {

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
