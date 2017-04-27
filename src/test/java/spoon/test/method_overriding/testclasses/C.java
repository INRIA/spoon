package spoon.test.method_overriding.testclasses;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.util.List;

public class C extends B<InputStream, FilterInputStream> {

	public C() {
	}

	@Override
	B m1(C c){
		return null;
	}
	
	@Override
	B<InputStream, FilterInputStream> m2(C c){
		return null;
	}
	
	@Override
	void m3(List<? super C> c) {
		// TODO Auto-generated method stub
		super.m3(c);
	}
	
	@Override
	void m4(List<? extends A<InputStream>> c){
	}
	
	@Override
	void m5(InputStream u) {
		super.m5(u);
	}
	
}
