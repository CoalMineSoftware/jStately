package com.coalminesoftware.jstately.machine;

import java.util.LinkedList;
import java.util.Queue;


/**
 * A simple InputAdapter implementation for machines that take the same input type as their
 * transitions.
 */
public class DefaultInputAdapter<InputType> implements InputAdapter<InputType,InputType> {
	private Queue<InputType> queue = new LinkedList<>();

	public void setInput(InputType input) {
		queue.add(input);
	}

	public boolean hasNext() {
		return !queue.isEmpty();
	}

	public InputType next() {
		return queue.poll();
	}
}
