package spoon.test.refactoring.testclasses;

public class GenericRenaming<SomeIdentifier, SomeOther> {
	private SomeIdentifier content;
	private SomeOther header;

	public GenericRenaming() {
		new SomeNestedType();
	}

	public void setContent(SomeIdentifier content, SomeOther header) {
		this.content = content;
		this.header = header;
	}

	public <SomeMethodGeneric extends SomeOther> SomeMethodGeneric doTheThing() {
		return (SomeMethodGeneric) header;
	}

	public SomeIdentifier getContent() {
		return content;
	}

	public SomeOther getHeader() {
		return header;
	}

	public class SomeNestedType {
	}
}
