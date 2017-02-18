package spoon.test.imports.testclasses.internal;

class SuperClass {
	protected class InnerClassProtected {
	}
	interface PackageProtectedInterface {
		interface NestedOfPackageProtectedInterface {
		}
		public interface NestedPublicInterface {
		}
	}
	protected interface ProtectedInterface {
		interface NestedOfProtectedInterface {
		}
		public interface NestedPublicInterface {
		}
	}
	public interface PublicInterface {
		interface NestedOfPublicInterface {
		}
		public interface NestedPublicInterface {
		}
	}
}
