package com.coalminesoftware.jstately.collection;

/**
 * Wrapper for a single value.
 */
public class Holder<T> { 
	private T value;

	public Holder() { }

	public Holder(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
