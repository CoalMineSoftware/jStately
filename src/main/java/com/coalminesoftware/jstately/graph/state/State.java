package com.coalminesoftware.jstately.graph.state;

import java.util.List;

import com.coalminesoftware.jstately.graph.composite.CompositeState;

/** Representation of a state, with callbacks for when the state is entered and exited by a machine. */
public interface State<TransitionInput> {
	/** Implementors must always return a list. */
	List<CompositeState<TransitionInput>> getComposites();
	void addComposite(CompositeState<TransitionInput> composite);

	/** Called by a state machine when the machine enters the state. */
	void onEnter();

	/** Called by a state machine after the machine has entered the state */
	void afterStateEntered();

	/** Called by a state machine when the machine exits the state. */
	void onExit();
}
