package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtPattern;
import spoon.reflect.code.CtRecordPattern;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

public class CtRecordPatternImpl extends CtExpressionImpl<Void> implements CtRecordPattern {

	@MetamodelPropertyField(role = CtRole.TYPE_REF)
	private CtTypeReference<?> recordType;
	@MetamodelPropertyField(role = CtRole.PATTERN)
	private List<CtPattern> patternList = CtElementImpl.emptyList();

	@Override
	public CtTypeReference<?> getRecordType() {
		return this.recordType;
	}

	@Override
	public CtRecordPattern setRecordType(CtTypeReference<?> recordType) {
		// TODO (440) model listener
		if (recordType != null) {
			recordType.setParent(this);
		}
		this.recordType = recordType;
		return this;
	}

	@Override
	public List<CtPattern> getPatternList() {
		return List.copyOf(this.patternList);
	}

	@Override
	public CtRecordPattern setPatternList(List<CtPattern> patternList) {
		// TODO (440) model listener, validation?
		this.patternList = new ArrayList<>(patternList);
		for (CtPattern pattern : this.patternList) {
			pattern.setParent(this);
		}
		return this;
	}

	@Override
	public CtRecordPattern addPattern(CtPattern pattern) {
		// TODO (440) model listener, validation?
		if (pattern == null) {
			return this;
		}
		if (this.patternList == CtElementImpl.<CtPattern>emptyList()) {
			this.patternList = new ArrayList<>();
		}
		pattern.setParent(this);
		this.patternList.add(pattern);
		return this;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtRecordPattern(this);
	}

	@Override
	public CtRecordPattern clone() {
		return (CtRecordPattern) super.clone();
	}
}
