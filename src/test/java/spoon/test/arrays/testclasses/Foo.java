package spoon.test.arrays.testclasses;

import java.io.EOFException;

public class Foo {
	public byte[] readByteArray(long byteCount) throws EOFException {
		byte[] result = new byte[(int) byteCount];
		return result;
	}
	String[] s = new String[] { "--help" };
	protected static final int grad4[][] = {
			{ 0, 1, 1, 1 }, { 0, 1, 1, -1 }, { 0, 1, -1, 1 }, { 0, 1, -1, -1 }, { 0, -1, 1, 1 }, { 0, -1, 1, -1 }, { 0, -1, -1, 1 }, { 0, -1, -1, -1 }, { 1, 0, 1, 1 }, { 1, 0, 1, -1 },
			{ 1, 0, -1, 1 }, { 1, 0, -1, -1 }, { -1, 0, 1, 1 }, { -1, 0, 1, -1 }, { -1, 0, -1, 1 }, { -1, 0, -1, -1 }, { 1, 1, 0, 1 }, { 1, 1, 0, -1 }, { 1, -1, 0, 1 }, { 1, -1, 0, -1 },
			{ -1, 1, 0, 1 }, { -1, 1, 0, -1 }, { -1, -1, 0, 1 }, { -1, -1, 0, -1 }, { 1, 1, 1, 0 }, { 1, 1, -1, 0 }, { 1, -1, 1, 0 }, { 1, -1, -1, 0 }, { -1, 1, 1, 0 }, { -1, 1, -1, 0 },
			{ -1, -1, 1, 0 }, { -1, -1, -1, 0 }
	};
}
