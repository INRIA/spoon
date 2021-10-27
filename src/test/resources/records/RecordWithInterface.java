package records;

public class RecordTest {

	public record SimpleRecord(int a) implements Supplier {

		@Override
		public Object get() {
			return null;
		}
	}
}