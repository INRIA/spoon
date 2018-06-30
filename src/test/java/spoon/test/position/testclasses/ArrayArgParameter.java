package spoon.test.position.testclasses;

public interface ArrayArgParameter {
	void m1(String[] arg);
	void m2(String []arg);
	void m3(String arg[]);
	void m4(/*1*/ String /*2*/ arg /*3*/ [ /*4*/ ] /* 5 */);
	void m5(/*1*/ String /*2*/ arg /*3*/ [ /*4 []*/ ] /* 5 */[][]/**/ []);
	void m6(String[]//[]
			p[]);
	void m7(String...arg);
	void m8(String[]...arg);
}