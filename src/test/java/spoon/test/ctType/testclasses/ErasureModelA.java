package spoon.test.ctType.testclasses;

public class ErasureModelA<A, B extends Exception, C extends B, D extends ErasureModelA<A,B,C,D>> {
	
	A paramA;
	B paramB;
	C paramC;
	D paramD;

	public <I, J extends C> ErasureModelA(I paramI, J paramJ, D paramD) {
	}

	public <I, J extends C> void method(I paramI, J paramJ, D paramD) {
	}
	
	static class ModelB<A2,B2 extends Exception, C2 extends B2, D2 extends ErasureModelA<A2,B2,C2,D2>> extends ErasureModelA<A2,B2,C2,D2> {
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

	static class ModelC extends ErasureModelA<Integer, RuntimeException, IllegalArgumentException, ModelC> {

		public ModelC(Float paramI, IllegalArgumentException paramJ, ModelC paramK) {
			super(paramI, paramJ, null);
		}
		
		public void method(Float paramI, IllegalArgumentException paramJ, ModelC paramK) {
		}
	}
}
