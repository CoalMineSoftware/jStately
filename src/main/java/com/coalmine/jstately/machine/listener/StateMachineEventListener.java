package com.coalmine.jstately.machine.listener;

import com.coalmine.jstately.graph.composite.CompositeState;
import com.coalmine.jstately.graph.state.State;
import com.coalmine.jstately.graph.transition.Transition;
import com.coalmine.jstately.machine.StateMachine;

/** Listener than can be registered with a {@link StateMachine} to be notified of events that happen. */
public interface StateMachineEventListener<TransitionInput> {
	/** Called before a state machine begins evaluating an input */
	void beforeEvaluatingInput(TransitionInput input);

	/** Called after a state machine finishes evaluating an input */
	void afterEvaluatingInput(TransitionInput input);

	/** Called before a state machine enters a State. */
	void beforeStateEntered(State<TransitionInput> state);

	/** Called after a state machine enters a State. */
	void afterStateEntered(State<TransitionInput> state);

	/** Called before a state machine exits a State. */
	void beforeStateExited(State<TransitionInput> state);

	/** Called after a state machine exits a State. */
	void afterStateExited(State<TransitionInput> state);

	/** Called before a state machine transitions from one state to another. */
	void beforeTransition(Transition<TransitionInput> transition, TransitionInput input);

	/** Called after a state machine transitions from one state to another. */
	void afterTransition(Transition<TransitionInput> transition, TransitionInput input);

	/** Called when no valid transition is found for a given input. */
	void noValidTransition(TransitionInput input);

	/** Called before entering a CompositeState of a state graph. */
	void beforeCompositeStateEntered(CompositeState<TransitionInput> composite);

	/** Called after entering a CompositeState of a state graph. */
	void afterCompositeStateEntered(CompositeState<TransitionInput> composite);

	/** Called before exiting a CompositeState of a state graph. */
	void beforeCompositeStateExited(CompositeState<TransitionInput> composite);

	/** Called after exiting a CompositeState of a state graph. */
	void afterCompositeStateExited(CompositeState<TransitionInput> composite);
}

