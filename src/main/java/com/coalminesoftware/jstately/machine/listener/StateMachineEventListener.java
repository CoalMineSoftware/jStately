package com.coalminesoftware.jstately.machine.listener;

import com.coalminesoftware.jstately.graph.composite.CompositeState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.Transition;
import com.coalminesoftware.jstately.machine.StateMachine;

/**
 * Listener than can be registered with {@link StateMachine#addEventListener(StateMachineEventListener)}
 * to be notified of events that happen.
 */
public interface StateMachineEventListener<TransitionInput> {
	/** Called before a state machine begins evaluating an input */
	void beforeEvaluatingInput(TransitionInput input, StateMachine<?,TransitionInput> machine);

	/** Called after a state machine finishes evaluating an input */
	void afterEvaluatingInput(TransitionInput input, StateMachine<?,TransitionInput> machine);

	/** Called before a state machine enters a State. */
	void beforeStateEntered(State<TransitionInput> state, StateMachine<?,TransitionInput> machine);

	/** Called after a state machine enters a State. */
	void afterStateEntered(State<TransitionInput> state, StateMachine<?,TransitionInput> machine);

	/** Called before a state machine exits a State. */
	void beforeStateExited(State<TransitionInput> state, StateMachine<?,TransitionInput> machine);

	/** Called after a state machine exits a State. */
	void afterStateExited(State<TransitionInput> state, StateMachine<?,TransitionInput> machine);

	/** Called before a state machine transitions from one state to another. */
	void beforeTransition(Transition<TransitionInput> transition, TransitionInput input, StateMachine<?,TransitionInput> machine);

	/** Called after a state machine transitions from one state to another. */
	void afterTransition(Transition<TransitionInput> transition, TransitionInput input, StateMachine<?,TransitionInput> machine);

	/** Called when no valid transition is found for a given input. */
	void noValidTransition(TransitionInput input, StateMachine<?,TransitionInput> machine);

	/** Called before entering a CompositeState of a state graph. */
	void beforeCompositeStateEntered(CompositeState<TransitionInput> composite, StateMachine<?,TransitionInput> machine);

	/** Called after entering a CompositeState of a state graph. */
	void afterCompositeStateEntered(CompositeState<TransitionInput> composite, StateMachine<?,TransitionInput> machine);

	/** Called before exiting a CompositeState of a state graph. */
	void beforeCompositeStateExited(CompositeState<TransitionInput> composite, StateMachine<?,TransitionInput> machine);

	/** Called after exiting a CompositeState of a state graph. */
	void afterCompositeStateExited(CompositeState<TransitionInput> composite, StateMachine<?,TransitionInput> machine);
}
