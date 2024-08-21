public sealed interface SealedInterfaceWithNestedSubclasses {

	final class NestedFinal extends SealedInterfaceWithNestedSubclasses {

	}

	non-sealed class NestedNonSealed extends SealedInterfaceWithNestedSubclasses {

	}
}
