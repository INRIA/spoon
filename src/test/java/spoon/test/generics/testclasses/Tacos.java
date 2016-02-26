package spoon.test.generics.testclasses;

import javax.lang.model.util.SimpleTypeVisitor7;
import java.util.ArrayList;
import java.util.List;

public class Tacos<K, V extends String> implements ITacos<V> {

	public Tacos() {
		<String>this(1);
	}

	public <T> Tacos(int nbTacos) {
	}

	public void m() {
		List<String> l = new ArrayList<>();
		List l2;
		IBurritos<?, ?> burritos = new Burritos<>();
		List<?> l3 = new ArrayList<Object>();
		new <Integer>Tacos<Object, String>();
		new Tacos<>();
	}

	public void m2() {
		this.<String>makeTacos(null);
		this.makeTacos(null);
	}

	public void m3() {
		new SimpleTypeVisitor7<Tacos, Void>() {
		};
		new javax.lang.model.util.SimpleTypeVisitor7<Tacos, Void>() {
		};
	}

	public <V, C extends List<V>> void m4() {
		Tacos.<V, C>makeTacos();
		Tacos.makeTacos();
	}

	public static <V, C extends List<V>> List<C> makeTacos() {
		return null;
	}

	public <T> void makeTacos(T anObject) {
	}

	class Burritos<K, V> implements IBurritos<K, V> {
		Tacos<K, String>.Burritos<K, V> burritos;
		public Tacos<K, String>.Burritos<K, V> b() {
			new Burritos<K, V>();
			return null;
		}

		class Pozole {
			public Tacos<K, String>.Burritos<K, V>.Pozole p() {
				new Pozole();
				return null;
			}
		}

		@Override
		public IBurritos<K, V> make() {
			return new Burritos<K, V>() {};
		}
	}

	public class BeerFactory {
		public Beer newBeer() {
			return new Beer();
		}
	}

	class Beer {
	}
}
