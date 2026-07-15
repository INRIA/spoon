package spoon.test.reference;

import ext.ImportedResource;

class ProblemReferenceBinding {
	void useResource() {
		try (var resource = new MissingResource() {
			void call() {
			}
		}) {
			resource.call();
		}
	}

	void useParameterizedResource() {
		try (var resource = new MissingResource<String>() {
			void parameterizedCall() {
			}
		}) {
			resource.parameterizedCall();
		}
	}

	void useMultipleUnresolvedResources() {
		try (
			var resource = new MissingService(missingArgument()) {
				@Override
				protected MissingExecutor createExecutor() {
					return missingQueue.executor();
				}
			};
			var log = MissingLog.capture(MissingService.class)
		) {
			resource.configure(missingDependency(MissingDependency.class));
			resource.start();
		}
	}

	void useNestedResource() {
		try (var resource = new Outer.Resource() {
		}) {
			resource.nestedCall();
		}
	}

	void useNestedGenericResource() {
		try (var resource = new GenericOuter<String>().new Resource<Integer>() {
		}) {
			resource.nestedGenericCall();
		}
	}

	void useGenericEnclosingResource() {
		try (var resource = new GenericOuter<String>().new PlainResource() {
		}) {
			resource.genericEnclosingCall();
		}
	}

	void useImportedResource() {
		try (var resource = new ImportedResource() {
			void importedCall() {
			}
		}) {
			resource.importedCall();
		}
	}

	void useQualifiedNestedResource() {
		try (var resource = new ext.QualifiedOuter.Resource() {
		}) {
			resource.qualifiedNestedCall();
		}
	}

	void useLowercaseNestedResource() {
		try (var resource = new lower.Resource() {
		}) {
			resource.lowercaseNestedCall();
		}
	}

	void useExplicitResourceType() {
		try (BaseResource resource = new SubResource() {
		}) {
			resource.baseCall();
		}
	}
}

class Outer {
	static class Resource {
		void nestedCall() {
		}
	}
}

class GenericOuter<T> {
	class Resource<U> {
		void nestedGenericCall() {
		}
	}

	class PlainResource {
		void genericEnclosingCall() {
		}
	}
}

class lower {
	static class Resource {
		void lowercaseNestedCall() {
		}
	}
}

class BaseResource {
	void baseCall() {
	}
}

class SubResource extends BaseResource {
}
