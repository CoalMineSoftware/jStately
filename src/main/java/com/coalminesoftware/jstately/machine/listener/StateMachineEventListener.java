package com.coalminesoftware.jstately.machine.listener;

import com.coalminesoftware.jstately.graph.state.CompositeState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.Transition;
import com.coalminesoftware.jstately.machine.StateMachine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Listener than can be registered with {@link StateMachine#addEventListener(StateMachineEventListener)}
 * to be notified of events that happen.
 */
public interface StateMachineEventListener<TransitionInput> {
	/** Called before a state machine begins evaluating an input */
	default void beforeEvaluatingInput(@Nullable TransitionInput input, @Nonnull StateMachine<?,TransitionInput> machine) {};

	/** Called after a state machine finishes evaluating an input */
	default void afterEvaluatingInput(@Nullable TransitionInput input, @Nonnull StateMachine<?,TransitionInput> machine) {};

	/** Called before a state machine enters a State. */
	default void beforeStateEntered(@Nonnull State<TransitionInput> state, @Nonnull StateMachine<?,TransitionInput> machine) {};

	/** Called after a state machine enters a State. */
	default void afterStateEntered(@Nonnull State<TransitionInput> state, @Nonnull StateMachine<?,TransitionInput> machine) {};

	/** Called before a state machine exits a State. */
	default void beforeStateExited(@Nonnull State<TransitionInput> state, @Nonnull StateMachine<?,TransitionInput> machine) {};

	/** Called after a state machine exits a State. */
	default void afterStateExited(@Nonnull State<TransitionInput> state, @Nonnull StateMachine<?,TransitionInput> machine) {};

	/** Called before a state machine transitions from one state to another. */
	default void beforeTransition(@Nonnull Transition<TransitionInput> transition, @Nullable TransitionInput input, @Nonnull StateMachine<?,TransitionInput> machine) {};

	/** Called after a state machine transitions from one state to another. */
	default void afterTransition(@Nonnull Transition<TransitionInput> transition, @Nullable TransitionInput input, @Nonnull StateMachine<?,TransitionInput> machine) {};

	/** Called when no valid transition is found for a given input. */
	default void noValidTransition(@Nullable TransitionInput input, @Nonnull StateMachine<?,TransitionInput> machine) {};

	/** Called before entering a CompositeState of a state graph. */
	default void beforeCompositeStateEntered(@Nonnull CompositeState<TransitionInput> composite, @Nonnull StateMachine<?,TransitionInput> machine) {};

	/** Called after entering a CompositeState of a state graph. */
	default void afterCompositeStateEntered(@Nonnull CompositeState<TransitionInput> composite, @Nonnull StateMachine<?,TransitionInput> machine) {};

	/** Called before exiting a CompositeState of a state graph. */
	default void  beforeCompositeStateExited(@Nonnull CompositeState<TransitionInput> composite, @Nonnull StateMachine<?,TransitionInput> machine) {};

	/** Called after exiting a CompositeState of a state graph. */
	default void afterCompositeStateExited(@Nonnull CompositeState<TransitionInput> composite, @Nonnull StateMachine<?,TransitionInput> machine) {};
}
