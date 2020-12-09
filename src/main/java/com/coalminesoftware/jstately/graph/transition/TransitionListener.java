package com.coalminesoftware.jstately.graph.transition;

public interface TransitionListener<TransitionInput> {
	/**
	 * Called by a state machine when transitioning.
	 * @param input Input that caused the transition.
	 */
	void onTransition(TransitionInput input);
}
