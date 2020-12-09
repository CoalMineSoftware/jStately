package com.coalminesoftware.jstately.collection;

import javax.annotation.Nullable;

/**
 * Wrapper for a single value.
 */
public class Holder<T> { 
	private T value;

	public Holder() { }

	public Holder(@Nullable T value) {
		this.value = value;
	}

	@Nullable
	public T getValue() {
		return value;
	}

	public void setValue(@Nullable T value) {
		this.value = value;
	}
}
