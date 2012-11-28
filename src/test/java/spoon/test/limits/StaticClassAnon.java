package spoon.test.limits;

public class StaticClassAnon {
	
	static void methode(){
		
	}

	static{
		class StaticIntern{
			public void hasAMethod() {
				StaticClassAnon.methode();
			}
		}
	}
	
}


