package com.coalminesoftware.jstately.machine;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Handles the queueing of machine inputs to a {@link StateMachine} and conversion to transition
 * inputs using the {@link InputAdapter} provided to the machine.
 */
class InputManager<MachineInput, TransitionInput> {
	private InputAdapter<MachineInput, TransitionInput> adapter;
	private Queue<MachineInput> inputs = new LinkedList<>();

	public void queueInput(MachineInput input) {
		inputs.add(input);
	}

	public boolean hasNext() {
		return adapter.hasNext() || advance();

	}

	public TransitionInput next() {
		if(!adapter.hasNext() && !advance()) {
			throw new IllegalStateException("No remaining inputs found.");
		}

		return adapter.next();
	}

	/**
	 * Advances through any remaining inputs until one is found that yields a output when given
	 * the adapter.
	 *
	 * @return Whether a value is available from {@link #next()} after advancing.
	 */
	private boolean advance() {
		while(!inputs.isEmpty()) {
			MachineInput input = inputs.remove();
			adapter.setInput(input);
			if(adapter.hasNext()) {
				return true;
			}
		}

		return false;
	}

	public void setInputAdapter(InputAdapter<MachineInput, TransitionInput> adapter) {
		this.adapter = adapter;
	}

	public boolean hasInputAdapter() {
		return adapter != null;
	}
}
