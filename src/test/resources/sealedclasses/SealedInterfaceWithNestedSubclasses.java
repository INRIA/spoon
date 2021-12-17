public sealed interface SealedClassWithNestedSubclasses {

	final class NestedFinal extends SealedInterfaceWithNestedSubclasses {

	}

	non-sealed class NestedNonSealed extends SealedInterfaceWithNestedSubclasses {

	}
}
