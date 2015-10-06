
public class Foo extends Unknown {

	void method(){
		Unknown x = null;
		x.method();
	}

	void m1() throws UnknownException {
		// x is not known
		// as the other ones as well
		x.y.z.method();
	}

	void m2(){
		Unknown x = null;
		x.first().second().third();
	}

	void m3(){
		int x = first().field; // field after call
	}

}
