package com.coalminesoftware.jstately.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates over the single value provided during construction.
 */
public class SingleValueIterator<T> implements Iterator<T> {
	private Holder<T> holder;

	public SingleValueIterator(T value) {
		holder = new Holder<>(value);
	}

	@Override
	public boolean hasNext() {
		return holder != null;
	}

	@Override
	public T next() {
		if(holder == null) {
			throw new NoSuchElementException();
		}

		T value = holder.getValue();
		holder = null;
		return value;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
