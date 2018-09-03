package spoon.test.position.testclasses;

public class FooLabel {

	void m(boolean x) {
		label1: while(x) {};
		label2: getClass();
		labelx: label3: new String();
		getClass();
		label4: x = false;
		label5: /*c1*/ return;
	}
	
	void m2(boolean x) {
		label1: {label2: while(x);}
		label1: label2: while(x);
	}
	void m3() {
		label: {getClass();}
	}
	void m4() {
		label: {getClass();}
		label:;
	}
	void m5() {
		switch (1) {
			case 2:
				label:;
				laval3: label1: label2: while(true);
		}
	}
	void m6(boolean x) {
		labelW: while(x) {
			try { label2: while(true); } finally {}
		}
	}
}