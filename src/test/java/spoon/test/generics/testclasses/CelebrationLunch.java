package spoon.test.generics.testclasses;

public class CelebrationLunch<K,L,M> extends Lunch<M,K> {
	public class WeddingLunch<X> extends CelebrationLunch<Tacos, Paella, X> {
		class Section<Y> {
			<S> void reserve(S section) {}
		}
		@Override
		<C> void eatMe(X paramA, Tacos paramB, C paramC){}
	}
	public class WeddingLunch2<X> {
		class Section<Y> {
			<S> void reserve(S section) {}
		}
	}
	@Override
	<C> void eatMe(M paramA, K paramB, C paramC){}
	
	<R> void prepare(R cook) {
	}
	
	void celebrate() {
		CelebrationLunch<Integer,Long,Double> cl = new CelebrationLunch<>();
		CelebrationLunch<Integer,Long,Double>.WeddingLunch<Mole> disgust = cl.new WeddingLunch<>();
		disgust.<Tacos>prepare(new Tacos());
		CelebrationLunch<Integer,Long,Double>.WeddingLunch<Mole>.Section<Paella> section = disgust.new Section<>();
		section.<Tacos>reserve(null);
	}
}

class SubCelebrationLunch<K2,L2,M2> extends CelebrationLunch<K2,L2,M2> {
	public class SubWeddingLunch<X2> extends WeddingLunch2<X2> {}
}

class SubSubCelebrationLunch<K2,L2,M2> extends SubCelebrationLunch<K2,L2,M2> {
	public class SubSubWeddingLunch<X2> extends SubWeddingLunch<X2> {
		class SubSection<Y2> extends Section<Y2> {
		}
	}
}