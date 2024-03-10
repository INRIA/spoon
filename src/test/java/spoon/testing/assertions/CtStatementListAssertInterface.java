package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
public interface CtStatementListAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtStatementList> extends SpoonAssert<A, W> , CtCodeElementAssertInterface<A, W> {
	default ListAssert<CtStatement> getStatements() {
		return Assertions.assertThat(actual().getStatements());
	}
}
