package spoon.test.control;

public class Ifs {

	Token jj_nt;
	int jj_ntk;
	Token token;
	Token token_source;

	@SuppressWarnings({ "unused", "null" })
	final int jj_ntk() {
		int y, i, j, partitionNumber = 0, _imageRows = 0, _partitionRows = 0, _partitionColumns = 0, _imageColumns = 0;
		int[] image = null, _part = null;
		for (j = 0, partitionNumber = 0; j < _imageRows; j += _partitionRows) {
			for (i = 0; i < _imageColumns; i += _partitionColumns, partitionNumber++) {
				for (y = 0; y < _partitionRows; y++) {
					System.arraycopy(image[j + y], i, _part[y], 0,
							_partitionColumns);
				}

				// _partitions[partitionNumber] = new IntMatrixToken(_part);
			}
		}
		for (int k = 0; k < 12; k++) {

		}

		if ((jj_nt = token.next) == null) {
			return (jj_ntk = (token.next = token_source.getNextToken()).kind);
		}
		return (jj_ntk = jj_nt.kind);
	}

	static class Token {
		public Token next;

		public Token getNextToken() {
			return null;
		}

		public int kind;
	}

}
