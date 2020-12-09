package com.coalminesoftware.jstately.machine.input;

import com.coalminesoftware.jstately.collection.Holder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An input adapter with the same input and output types, that passes its inputs through as-is.
 */
public class PassthroughInputAdapter<InputType> implements InputAdapter<InputType, InputType> {
	@Override
	@Nonnull
	public Iterator<InputType> adaptInput(@Nullable InputType input) {
		return new SingleValueIterator<>(input);
	}

	/**
	 * "Iterates" over the single value provided during construction.
	 */
	private static class SingleValueIterator<T> implements Iterator<T> {
		private Holder<T> holder;

		public SingleValueIterator(@Nullable T value) {
			holder = new Holder<>(value);
		}

		@Override
		public boolean hasNext() {
			return holder != null;
		}

		@Override
		@Nullable
		public T next() {
			if(holder == null) {
				throw new NoSuchElementException();
			}

			T value = holder.getValue();
			holder = null;
			return value;
		}
	}
}
