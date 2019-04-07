package com.coalminesoftware.jstately.machine.input;

import com.coalminesoftware.jstately.collection.SingleValueIterator;

import java.util.Iterator;

/**
 * An input adapter with the same input and output types, that passes its inputs through as-is.
 */
public class PassthroughInputAdapter<InputType> implements InputAdapter<InputType,InputType> {
	@Override
	public Iterator<InputType> adaptInput(InputType input) {
		return new SingleValueIterator<>(input);
	}
}
