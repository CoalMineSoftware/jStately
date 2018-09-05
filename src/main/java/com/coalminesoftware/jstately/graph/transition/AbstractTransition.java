package com.coalminesoftware.jstately.graph.transition;

import com.coalminesoftware.jstately.graph.state.State;

/** Provides a basic Transition implementation with a head but no {@link #isValid(Object)} method. */
public abstract class AbstractTransition<TransitionInput> implements Transition<TransitionInput> {
	protected State<TransitionInput> head;

	public AbstractTransition() { }

	public AbstractTransition(State<TransitionInput> head) {
		this.head = head;
	}

	public State<TransitionInput> getHead() {
		return head;
	}
	public void setHead(State<TransitionInput> head) {
		this.head = head;
	}

	public void onTransition(TransitionInput input) { }

	public String toString() {
		return super.toString()+"[head="+head.toString()+"]";
	}
}
