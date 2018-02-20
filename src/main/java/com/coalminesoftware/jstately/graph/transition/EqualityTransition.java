package com.coalminesoftware.jstately.graph.transition;

import com.coalminesoftware.jstately.graph.state.State;

/**
 * A {@link Transition} whose validity is determined by the equality of an input and a given value.
 */
public class EqualityTransition<TransitionInput> extends AbstractTransition<TransitionInput> implements Transition<TransitionInput> {
	private TransitionInput validInput;

	public EqualityTransition() { }

	public EqualityTransition(State<TransitionInput> head, TransitionInput validInput) {
		this.head = head;
		this.validInput = validInput;
	}

	public boolean isValid(TransitionInput input) {
		return validInput==null?
				input==null :
				validInput.equals(input);
	}

	public TransitionInput getValidInput() {
		return validInput;
	}
	public void setValidInput(TransitionInput validInput) {
		this.validInput = validInput;
	}
}
