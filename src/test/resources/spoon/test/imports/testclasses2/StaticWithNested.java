package spoon.test.imports.testclasses2;

abstract class StaticWithNested<K, V>
{
	private static class StaticNested<K> {
		private static class StaticNested2<K> {
			
		}
		void fnc() {
			new StaticNested2<K>();
		}
	}
}
