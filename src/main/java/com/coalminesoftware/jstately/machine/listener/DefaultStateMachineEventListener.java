package com.coalminesoftware.jstately.machine.listener;

import com.coalminesoftware.jstately.graph.composite.CompositeState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.Transition;
import com.coalminesoftware.jstately.machine.StateMachine;

/**
 * Convenience implementation of {@link StateMachineEventListener} with no-op methods that can be
 * overridden as needed.
 */
public class DefaultStateMachineEventListener<TransitionInput> implements StateMachineEventListener<TransitionInput> {
	@Override
	public void beforeEvaluatingInput(TransitionInput input, StateMachine<?,TransitionInput> machine) { }
	@Override
	public void afterEvaluatingInput(TransitionInput input, StateMachine<?,TransitionInput> machine) { }

	@Override
	public void beforeStateEntered(State<TransitionInput> state, StateMachine<?,TransitionInput> machine) { }
	@Override
	public void afterStateEntered(State<TransitionInput> state, StateMachine<?,TransitionInput> machine) { }

	@Override
	public void beforeStateExited(State<TransitionInput> state, StateMachine<?,TransitionInput> machine) { }
	@Override
	public void afterStateExited(State<TransitionInput> state, StateMachine<?,TransitionInput> machine) { }

	@Override
	public void beforeTransition(Transition<TransitionInput> transition, TransitionInput input, StateMachine<?,TransitionInput> machine) { }
	@Override
	public void afterTransition(Transition<TransitionInput> transition, TransitionInput input, StateMachine<?,TransitionInput> machine) { }

	@Override
	public void noValidTransition(TransitionInput input, StateMachine<?,TransitionInput> machine) { }

	@Override
	public void beforeCompositeStateEntered(CompositeState<TransitionInput> composite, StateMachine<?,TransitionInput> machine) { }
	@Override
	public void afterCompositeStateEntered(CompositeState<TransitionInput> composite, StateMachine<?,TransitionInput> machine) { }

	@Override
	public void beforeCompositeStateExited(CompositeState<TransitionInput> composite, StateMachine<?,TransitionInput> machine) { }
	@Override
	public void afterCompositeStateExited(CompositeState<TransitionInput> composite, StateMachine<?,TransitionInput> machine) { }
}
