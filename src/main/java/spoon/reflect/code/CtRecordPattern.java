package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.UnsettableProperty;

import java.util.List;

// TODO (440) metamodel

/**
 * This code element defines a record pattern, introduced in Java 21
 * by <a href=https://openjdk.java.net/jeps/440>JEP 440</a>.
 * <p>
 * Example:
 * <pre>
 *     Object obj = null;
 *     boolean longerThanTwo = false;
 *     record MyRecord(String value) {}
 *     // MyRecord(var string) is the record pattern
 *     if (obj instanceof MyRecord(var string)) {
 *         longerThanTwo = string.length() > 2;
 *     }
 * </pre>
 */
public interface CtRecordPattern extends CtPattern, CtExpression<Void> {
	
	@PropertyGetter(role = CtRole.TYPE_REF)
	CtTypeReference<?> getRecordType();

	@PropertySetter(role = CtRole.TYPE_REF)
	CtRecordPattern setRecordType(CtTypeReference<?> recordType);

	@PropertyGetter(role = CtRole.PATTERN)
	List<CtPattern> getPatternList();

	@PropertySetter(role = CtRole.PATTERN)
	CtRecordPattern setPatternList(List<CtPattern> patternList);

	@PropertySetter(role = CtRole.PATTERN)
	CtRecordPattern addPattern(CtPattern pattern);

	@Override
	CtRecordPattern clone();

	@Override
	@UnsettableProperty
	List<CtTypeReference<?>> getTypeCasts();

	@Override
	@UnsettableProperty
	<C extends CtExpression<Void>> C setTypeCasts(List<CtTypeReference<?>> types);

	@Override
	@UnsettableProperty
	<C extends CtExpression<Void>> C addTypeCast(CtTypeReference<?> type);
}
