
import static pack1.A.staticMethod;
import static Tacos.Burritos.makeBurritos;
import static pack2.C.D.staticD;
import static pack3.E.*;

public class Tacos {
	public void m() {
		A.staticMethod("");
		staticMethod("");
		String s = A.staticMethod("");
		String s1 = staticMethod(1);
	}

	public void m2() {
		Burritos.makeBurritos(1);
		makeBurritos(2);
		String s = Burritos.makeBurritos(3);
		String s = makeBurritos(4);
	}

	public void m3() {
		C.D.staticD(1);
		staticD(2);
		String s = C.D.staticD(3);
		String s = staticD(4);
	}

	public void m4() {
		E.staticE(1);
		staticE(2);
		String s = E.staticE(3);
		String s = staticE(4);
	}

	public static class Burritos {
		public static String makeBurritos(int o) {
		}
	}
}