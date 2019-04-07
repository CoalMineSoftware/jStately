package com.coalminesoftware.jstately.machine.input;

import com.coalminesoftware.jstately.machine.StateMachine;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import static com.coalminesoftware.jstately.ParameterValidation.assertNotNull;

/**
 * Queues a {@link StateMachine}'s inputs and convert them to transition inputs using the provided
 * {@link InputAdapter}.
 */
public class InputManager<MachineInput,TransitionInput> {
	private Queue<MachineInput> machineInputs = new LinkedList<>();
	private InputAdapter<MachineInput,TransitionInput> inputAdapter;
	private Iterator<TransitionInput> transitionInputs;

	public void queueInput(MachineInput input) {
		machineInputs.add(input);
	}

	public boolean hasNext() {
		return (transitionInputs != null && transitionInputs.hasNext()) || advance();
	}

	public TransitionInput next() {
		if(!hasNext()) {
			throw new NoSuchElementException("No remaining inputs.");
		}

		return transitionInputs.next();
	}

	/**
	 * Advances through any remaining machine inputs until one is found that yields a transition
	 * input.
	 *
	 * @return Whether a value is available from {@link #next()} after advancing.
	 */
	private boolean advance() {
		while(!machineInputs.isEmpty()) {
			transitionInputs = inputAdapter.adaptInput(machineInputs.remove());
			if(transitionInputs.hasNext()) {
				return true;
			}
		}

		return false;
	}

	public void setInputAdapter(InputAdapter<MachineInput, TransitionInput> inputAdapter) {
		assertNotNull("An input adapter is required", inputAdapter);
		this.inputAdapter = inputAdapter;
	}

	public boolean hasInputAdapter() {
		return inputAdapter != null;
	}
}
