package spoon.refactoring;

import spoon.SpoonException;

public class RefactoringException extends SpoonException {
	private static final long serialVersionUID = 1L;

	public RefactoringException() {
	}

	public RefactoringException(String msg) {
		super(msg);
	}

	public RefactoringException(Throwable e) {
		super(e);
	}

	public RefactoringException(String msg, Throwable e) {
		super(msg, e);
	}

}
