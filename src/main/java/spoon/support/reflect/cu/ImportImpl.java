package spoon.support.reflect.cu;

import spoon.reflect.cu.Import;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;

public class ImportImpl implements Import {

	public ImportImpl(CtTypeReference<?> type) {
		reference = type;
	}

	public ImportImpl(CtPackageReference pack) {
		reference = pack;
	}

	public ImportImpl(CtFieldReference<?> field) {
		reference = field;
	}

	CtReference reference;

	@Override
	public String toString() {
		String str = "import " + reference.toString();
		if (reference instanceof CtPackageReference) {
			str += ".*";
		}
		return str;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Import) {
			return reference.equals(((Import) obj).getReference());
		}
		return false;
	}

	public CtReference getReference() {
		return reference;
	}

}
