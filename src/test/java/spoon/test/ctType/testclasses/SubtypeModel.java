package spoon.test.ctType.testclasses;

//We have to declare own implementations of these well known classes, to have them on class path :-)
interface List<E> {};
class ArrayList<E> implements List<E> {}

class ListOfX extends ArrayList<X> {}
class ListOfA1<A> extends ArrayList<A> {}
class ListOfA3<A,B,C> extends ArrayList<B> {}

public class SubtypeModel<A extends X> {

	void foo() {
		/*
		 * Following code (including comments!) is part of model, which is used to test by CtTypeTest#testIsSubTypeOfonTypeReferences
		 */
		List listRaw = new ArrayList();
		List<Object> listObject = new ArrayList<>();
		List<?> listAll = new ArrayList<>();
		List<X> listX = new ArrayList<>();
		ListOfX listOfX = new ListOfX();
		ListOfA1<X> listOfA1_X = new ListOfA1<>();
		ListOfA3<O<A>,X,O<Y>> listOfA3_X = new ListOfA3<>();
		List<Y> listY = new ArrayList<>();
		List<? extends X> listExtendsX = new ArrayList<>();
		List<? extends Y> listExtendsY = new ArrayList<>();
		List<? super X> listSuperX = new ArrayList<>();
		List<? super Y> listSuperY = new ArrayList<>();
		
		X x = null;
		Y y = null;
		
		listExtendsX = listOfA3_X;

		x = y;
//		y = x;
		
		listRaw = listObject; 
		listRaw = listAll; 
		listRaw = listX; 
		listRaw = listY; 
		listRaw = listExtendsX; 
		listRaw = listExtendsY;
		listRaw = listSuperX;
		listRaw = listSuperY;
		
		listObject = listRaw;
//		listObject = listAll; 
//		listObject = listX; 
//		listObject = listY; 
//		listObject = listExtendsX; 
//		listObject = listExtendsY;
//		listObject = listSuperX;
//		listObject = listSuperY;
		
		listAll = listRaw;
		listAll = listObject; 
		listAll = listX; 
		listAll = listY; 
		listAll = listExtendsX; 
		listAll = listExtendsY;
		listAll = listSuperX;
		listAll = listSuperY;

		listX = listRaw;
//		listX = listObject; 
//		listX = listAll; 
//		listX = listY; 
//		listX = listExtendsX; 
//		listX = listExtendsY;
//		listX = listSuperX;
//		listX = listSuperY;

		listY = listRaw;
//		listY = listObject; 
//		listY = listAll; 
//		listY = listX; 
//		listY = listExtendsX; 
//		listY = listExtendsY;
//		listY = listSuperX;
//		listY = listSuperY;

		listExtendsX = listRaw;
//		listExtendsX = listObject; 
//		listExtendsX = listAll; 
		listExtendsX = listX; 
		listExtendsX = listOfX;
		listExtendsX = listOfA1_X;
		listExtendsX = listOfA3_X;
		listExtendsX = listY; 
		listExtendsX = listExtendsY;
//		listExtendsX = listSuperX;
//		listExtendsX = listSuperY;

		listExtendsY = listRaw;
//		listExtendsY = listObject; 
//		listExtendsY = listAll; 
//		listExtendsY = listX; 
//		listExtendsY = listOfX; 
		listExtendsY = listY; 
//		listExtendsY = listExtendsX; 
//		listExtendsY = listSuperX;
//		listExtendsY = listSuperY;

		listSuperX = listRaw;
		listSuperX = listObject; 
//		listSuperX = listAll; 
		listSuperX = listX; 
//		listSuperX = listY; 
//		listSuperX = listExtendsX; 
//		listSuperX = listExtendsY;
//		listSuperX = listSuperY;

		listSuperY = listRaw;
		listSuperY = listObject; 
//		listSuperY = listAll; 
		listSuperY = listX; 
		listSuperY = listY; 
//		listSuperY = listExtendsX; 
//		listSuperY = listExtendsY;
		listSuperY = listSuperX;
	}
}
