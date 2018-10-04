package spoon.test.ctType.testclasses;

import java.io.Serializable;
import java.util.List;

public class ErasureModelA<A, B extends Exception, C extends B, D extends List<B>> {
	
	A paramA;
	B paramB;
	C paramC;
	D paramD;

	public <I, J extends C> ErasureModelA(I paramI, J paramJ, D paramD) {
	}

	public <I, J extends C> void method(I paramI, J paramJ, D paramD) {
	}
	
	public <I, J extends C> void method2(I paramI, J paramJ, D paramD) {
	}
	
	public <I, J extends C, K extends ErasureModelA<A,B,C,D>&Serializable> void method3(I paramI, J paramJ, D paramD, K paramK) {
	}

	public <I> void wildCardMethod(I paramI, ErasureModelA<? extends I, B, C, D> extendsI) {
	}

	// simple case
	public void list(List<Object> x, List<List<Object>> y, List<String> z) {
	}
	
	public <I, J extends C> void methodWithArray(I[] paramI, J... paramJ) {}

	static class ModelB<A2,B2 extends Exception, C2 extends B2, D2 extends List<B2>> extends ErasureModelA<A2,B2,C2,D2> {
		A2 paramA2;
		B2 paramB2;
		C2 paramC2;
		D2 paramD2;

		public <I, J extends C2> ModelB(I paramI, J paramJ, D2 paramD2) {
			super(paramI, paramJ, paramD2);
		}
			
		@Override
		public <I, J extends C2> void method(I paramI, J paramJ, D2 paramD2) {
		}
	}

	static class ModelC extends ErasureModelA<Integer, RuntimeException, IllegalArgumentException, List<RuntimeException>> {

		public ModelC(Float paramI, IllegalArgumentException paramJ, ModelC paramK) {
			super(paramI, paramJ, null);
		}
		
		public void method(Float paramI, IllegalArgumentException paramJ, ModelC paramK) {
		}
	}
}
