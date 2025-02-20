package com.foo.bar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiamondConstructorCallTypeInference.java {
	// Whatever does not exist.
	private final List<? extends Whatever> items;

	public DiamondConstructorCallTypeInference.java(Collection<? extends Whatever> items) {
		this.items = new ArrayList<>(items);
	}
}
