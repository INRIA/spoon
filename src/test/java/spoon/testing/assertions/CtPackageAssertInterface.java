package spoon.testing.assertions;
import java.util.Collection;
import org.assertj.core.api.AbstractCollectionAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
public interface CtPackageAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtPackage> extends SpoonAssert<A, W> , CtNamedElementAssertInterface<A, W> , CtShadowableAssertInterface<A, W> {
	default AbstractCollectionAssert<?, Collection<? extends CtPackage>, CtPackage, ?> getPackages() {
		return Assertions.assertThat(actual().getPackages());
	}

	default AbstractCollectionAssert<?, Collection<? extends CtType<?>>, CtType<?>, ?> getTypes() {
		return Assertions.assertThat(actual().getTypes());
	}
}
