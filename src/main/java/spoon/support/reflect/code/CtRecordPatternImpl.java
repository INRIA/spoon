/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
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

import static spoon.reflect.path.CtRole.PATTERN;

public class CtRecordPatternImpl extends CtExpressionImpl<Void> implements CtRecordPattern {

	@MetamodelPropertyField(role = CtRole.TYPE_REF)
	private CtTypeReference<?> recordType;
	@MetamodelPropertyField(role = PATTERN)
	private List<CtPattern> patternList = CtElementImpl.emptyList();

	@Override
	public CtTypeReference<?> getRecordType() {
		return this.recordType;
	}

	@Override
	public CtRecordPattern setRecordType(CtTypeReference<?> recordType) {
		if (recordType != null) {
			recordType.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener()
			.onObjectUpdate(this, CtRole.TYPE_REF, recordType, this.recordType);
		this.recordType = recordType;
		return this;
	}

	@Override
	public List<CtPattern> getPatternList() {
		return List.copyOf(this.patternList);
	}

	@Override
	public CtRecordPattern setPatternList(List<CtPattern> patternList) {
		getFactory().getEnvironment().getModelChangeListener()
			.onListDeleteAll(this, PATTERN, this.patternList, new ArrayList<>(this.patternList));
		this.patternList.clear();
		for (CtPattern pattern : patternList) {
			addPattern(pattern);
		}
		return this;
	}

	@Override
	public CtRecordPattern addPattern(CtPattern pattern) {
		if (pattern == null) {
			return this;
		}
		if (this.patternList == CtElementImpl.<CtPattern>emptyList()) {
			this.patternList = new ArrayList<>();
		}
		pattern.setParent(this);
		getFactory().getEnvironment().getModelChangeListener()
			.onListAdd(this, PATTERN, this.patternList, pattern);
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
