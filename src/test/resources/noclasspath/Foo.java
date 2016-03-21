package com.example;

import de.rcenvironment.core.component.api.BatchedConsoleRowsProcessor;
import com.example.Type;
import com.example.Kuu;
import com.example.Bar;

public class Foo implements Kuu {
	private String[] commandLineArgs = new String[0];

	public Foo() {
		Bar.Inner<Type> variable = new Bar.Inner<Type>() {
			@Override
			public void method(List<Type> list) {
				Type[] array = list.toArray(new Type[list.size()]);
			}
		};
	}

}