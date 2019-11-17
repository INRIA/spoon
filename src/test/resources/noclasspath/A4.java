import java.util.List;
import java.util.ArrayList;

public class A4 {

	int field;

	public static boolean b(Integer param) {
		return param > 1;
	}

	public void c(int param) {
		List<Integer> list = new ArrayList();
		long l = list.stream().filter(A4::b).count();
	}
}