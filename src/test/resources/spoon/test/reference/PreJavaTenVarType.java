package spoon.test.reference;

class PreJavaTenVarType {
	void useExplicitVarType() {
		try (var resource = new VarSubtype() {
		}) {
			resource.varTypeCall();
		}
	}
}

class var {
	void varTypeCall() {
	}
}

class VarSubtype extends var {
}
