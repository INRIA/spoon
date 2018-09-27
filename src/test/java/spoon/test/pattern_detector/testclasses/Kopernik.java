package spoon.test.pattern_detector.testclasses;

import java.util.Collections;
import java.util.List;

import spoon.reflect.code.CtStatement;

public class Kopernik {
	class A {
		int mars(int x) {
			System.out.println("I am Mars with temperature: " + x);
			return x + 0;
		}
	}
	class B {
		int saturn(int y) {
			System.out.println("I am Saturn with temperature: " + y);
			return y + 0;
		}
	}
	class C {
		int merkur(int temparature) {
			System.out.println("I am Merku with temperature: " + temparature);
			return temparature + 0;
		}
	}
	class D {
		int mars(int x) {
			System.out.println("I am Mars with temperature: " + x);
			return x + 1;
		}
	}
	class E {
		Integer mars(int x) {
			System.out.println("I am Mars with temperature: " + x);
			return x + 0;
		}
	}
	class F {
		Integer saturn() {
			System.out.println("I am Saturn");
			return null;
		}
	}
	class Literals {
		void m() {
			int a = 0;
			int b = 0;
			int c = 0;
		}
	}
	class Literals_Same {
		void m() {
			int a = 1;
			int b = 1;
			int c = 0;
		}
	}
	class Literals_Different {
		void m() {
			int a = 0;
			int b = 1;
			int c = 1;
		}
	}
	class ReturnField_1 {
		List<String> a;
		List<String> getA() {
			return a;
		}
	}
	class ReturnField_2 {
		List<Integer> b;
		List<Integer> getB() {
			return b;
		}
	}
	class ThisAccess_1 {
		List lst;
		public void thisAccess(CtStatement statement) {
			if (this.lst == Collections.emptyList()) {
				return;
			}
		}
	}
	class ThisAccess_2 {
		List lst;
		public void thisAccess(CtStatement statement) {
			if (lst == Collections.emptyList()) {
				return;
			}
		}
	}
	class Comments_1 {
		public void comment() {
			//xx
		}
	}
	class Comments_2 {
		public void comment() {
		}
	}
}
