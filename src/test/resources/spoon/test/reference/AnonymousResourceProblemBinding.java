package spoon.test.reference;

import ext.ImportedResource;
import ext.QualifiedOuter.Resource;

class AnonymousResourceProblemBinding {
	boolean selectFirst;

	void useConditionalAnonymousResourceAsConstructorArgument() {
		try (
			var resource = new MissingResource() {
			};
			var wrapper = new MissingConditionalWrapper(selectFirst ? resource : resource)
		) {
			wrapper.consume();
		}
	}

	void useConditionalAnonymousResourceAsMessageArgument() {
		try (var resource = new MissingResource() {
		}) {
			consumeConditional(selectFirst ? resource : resource);
		}
	}

	void useConditionalAnonymousResourceWithResolvedObjectParameter() {
		try (var resource = new MissingResource() {
		}) {
			acceptObject(selectFirst ? resource : resource);
		}
	}

	void useConditionalAnonymousResourceWithInapplicableMethod() {
		try (var resource = new MissingResource() {
		}) {
			acceptString(selectFirst ? resource : resource);
		}
	}

	void useConditionalAnonymousResourceWithInapplicableConstructor() {
		try (var resource = new MissingResource() {
		}) {
			new KnownStringWrapper(selectFirst ? resource : resource);
		}
	}

	void useDifferentConditionalAnonymousResourcesAsConstructorArgument() {
		try (
			var first = new MissingFirstResource() {
			};
			var second = new MissingSecondResource() {
			};
			var wrapper = new MissingMixedWrapper(selectFirst ? first : second)
		) {
			wrapper.consume();
		}
	}

	void useDifferentlyParameterizedConditionalAnonymousResourcesAsConstructorArgument() {
		try (
			var first = new MissingResource<String>() {
			};
			var second = new MissingResource<Integer>() {
			};
			var wrapper = new MissingGenericMixedWrapper(selectFirst ? first : second)
		) {
			wrapper.consume();
		}
	}

	void useNullableConditionalAnonymousResourcesAsConstructorArguments() {
		try (var resource = new MissingResource() {
		}) {
			new MissingNullableWrapper(selectFirst ? resource : null);
			new MissingNullFirstWrapper(selectFirst ? null : resource);
		}
	}

	void acceptObject(Object value) {
	}

	void acceptString(String value) {
	}

	void useAnonymousResourceAsConstructorArgument() {
		try (
			var resource = new MissingResource() {
			};
			var wrapper = new MissingWrapper(resource)
		) {
			wrapper.consume();
		}
	}

	void useAnonymousResourceAsMessageArgument() {
		try (var resource = new MissingResource() {
		}) {
			consume(resource);
		}
	}

	void useParameterizedAnonymousResourceAsConstructorArgument() {
		try (
			var resource = new MissingResource<String>() {
			};
			var wrapper = new MissingWrapper(resource);
			var parameterizedWrapper = new MissingParameterizedWrapper(resource)
		) {
			wrapper.consumeParameterized();
			parameterizedWrapper.consumeParameterized();
		}
	}

	void useMembersOfAnonymousResource() {
		try (var resource = new MissingNode() {
		}) {
			var field = resource.missingField;
			resource.missingField.consume();
			var callback = resource.missingField::consume;
		}
	}

	void useImportedAnonymousResourceAsConstructorArgument() {
		try (
			var resource = new ImportedResource() {
			};
			var wrapper = new MissingImportedWrapper(resource)
		) {
			wrapper.consumeImported();
		}
	}

	void useNestedAnonymousResourceAsConstructorArgument() {
		try (
			var resource = new Outer<String>().new Resource<Integer>() {
			};
			var wrapper = new MissingNestedWrapper(resource)
		) {
			wrapper.consumeNested();
		}
	}

	void useDirectlyImportedNestedAnonymousResourceAsConstructorArgument() {
		try (
			var resource = new Resource() {
			};
			var wrapper = new MissingDirectNestedWrapper(resource)
		) {
			wrapper.consumeDirectNested();
		}
	}

	void useLowercaseEnclosingAnonymousResourceAsConstructorArgument() {
		try (
			var resource = new lower.Resource() {
			};
			var wrapper = new MissingLowercaseWrapper(resource)
		) {
			wrapper.consumeLowercase();
		}
	}

	class Outer<T> {
		class Resource<U> {
		}
	}

	class lower {
		class Resource {
		}
	}

	class KnownStringWrapper {
		KnownStringWrapper(String value) {
		}
	}
}
