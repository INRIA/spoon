package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtPattern;
import spoon.reflect.code.CtRecordPattern;
import spoon.reflect.reference.CtTypeReference;
public interface CtRecordPatternAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtRecordPattern> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> , CtPatternAssertInterface<A, W> {
	default ListAssert<CtPattern> getPatternList() {
		return Assertions.assertThat(actual().getPatternList());
	}

	default CtTypeReferenceAssertInterface<?, ?> getRecordType() {
		return SpoonAssertions.assertThat(actual().getRecordType());
	}

	default ListAssert<CtTypeReference<?>> getTypeCasts() {
		return Assertions.assertThat(actual().getTypeCasts());
	}
}
