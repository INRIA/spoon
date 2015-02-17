package spoon.test.annotation.testclasses;

import java.util.ArrayList;
import java.util.List;

public class AnnotationsAppliedOnAnyTypeInAClass {

	public void m() throws @TypeAnnotation Exception {
	}

	public String m2() {
		Object s = new @TypeAnnotation String();
		return (@TypeAnnotation String) s;
	}

	public @TypeAnnotation String m3() {
		return "";
	}

	public <@TypeAnnotation T> void m4() {
		List<@TypeAnnotation T> list = new ArrayList<>();
		List<@TypeAnnotation ?> list2 = new ArrayList<>();
		List<@TypeAnnotation BasicAnnotation> list3 = new ArrayList<@TypeAnnotation BasicAnnotation>();
	}

	public <T> void m5() {
		List<@TypeAnnotation(integer=1) T> list;
		List<@TypeAnnotation(integers={1}) T> list2;
		List<@TypeAnnotation(string="") T> list3;
		List<@TypeAnnotation(strings={""}) T> list4;
		List<@TypeAnnotation(clazz=String.class) T> list5;
		List<@TypeAnnotation(classes={String.class}) T> list6;
		List<@TypeAnnotation(b=true) T> list7;
		List<@TypeAnnotation(e=AnnotParamTypeEnum.R) T> list8;
		List<@TypeAnnotation(ia=@InnerAnnot("")) T> list9;
		List<@TypeAnnotation(ias={@InnerAnnot("")}) T> list10;
		List<@TypeAnnotation(inceptions={@Inception(value = @InnerAnnot(""), values={@InnerAnnot("")})}) T> list11;
	}

	public void m6(@TypeAnnotation String param) {
		@TypeAnnotation String s = "";
	}

	public enum DummyEnum implements @TypeAnnotation BasicAnnotation {
	}

	public interface DummyInterface extends @TypeAnnotation BasicAnnotation {
	}

	public class DummyClass extends @TypeAnnotation AnnotArrayInnerClass implements @TypeAnnotation BasicAnnotation {
	}

	public class DummyGenericClass<@TypeAnnotation T, @TypeAnnotation K> implements BasicAnnotation<@TypeAnnotation T> {
	}
}
