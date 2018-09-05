package com.coalminesoftware.jstately.machine.listener;

import com.coalminesoftware.jstately.graph.composite.CompositeState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.Transition;
import com.coalminesoftware.jstately.machine.StateMachine;

/** Event listener that logs events to System.out. */
public class ConsoleStateMachineEventListener<TransitionInput> implements StateMachineEventListener<TransitionInput> {
	@Override
	public void beforeEvaluatingInput(TransitionInput input, StateMachine<?,TransitionInput> machine) {
		System.out.println("Before evaluating input ("+input+") on machine ("+machine+")");
	}

	@Override
	public void afterEvaluatingInput(TransitionInput input, StateMachine<?,TransitionInput> machine) {
		System.out.println("After evaluating input ("+input+") on machine ("+machine+")");
	}

	@Override
	public void beforeStateEntered(State<TransitionInput> state, StateMachine<?,TransitionInput> machine) {
		System.out.println("Before state ("+state+") entered on machine ("+machine+")");
	}

	@Override
	public void afterStateEntered(State<TransitionInput> state, StateMachine<?,TransitionInput> machine) {
		System.out.println("After state ("+state+") entered on machine ("+machine+")");
	}

	@Override
	public void beforeStateExited(State<TransitionInput> state, StateMachine<?,TransitionInput> machine) {
		System.out.println("Before state ("+state+") exited on machine ("+machine+")");
	}

	@Override
	public void afterStateExited(State<TransitionInput> state, StateMachine<?,TransitionInput> machine) {
		System.out.println("After state ("+state+") exited on machine ("+machine+")");
	}

	@Override
	public void beforeTransition(Transition<TransitionInput> transition, TransitionInput input, StateMachine<?,TransitionInput> machine) {
		System.out.println("Before following transition ("+transition+") for input ("+input+") on machine ("+machine+")");
	}

	@Override
	public void afterTransition(Transition<TransitionInput> transition, TransitionInput input, StateMachine<?,TransitionInput> machine) {
		System.out.println("After following transition ("+transition+") for input ("+input+") on machine ("+machine+")");
	}

	@Override
	public void noValidTransition(TransitionInput input, StateMachine<?,TransitionInput> machine) {
		System.out.println("No transition found for input ("+input+") on machine ("+machine+")");
	}

	@Override
	public void beforeCompositeStateEntered(CompositeState<TransitionInput> composite, StateMachine<?,TransitionInput> machine) {
		System.out.println("Before entering composite state ("+composite+") on machine ("+machine+")");
	}

	@Override
	public void afterCompositeStateEntered(CompositeState<TransitionInput> composite, StateMachine<?,TransitionInput> machine) {
		System.out.println("After entering composite state ("+composite+") on machine ("+machine+")");
	}

	@Override
	public void beforeCompositeStateExited(CompositeState<TransitionInput> composite, StateMachine<?,TransitionInput> machine) {
		System.out.println("Before exiting composite state ("+composite+") on machine ("+machine+")");
	}

	@Override
	public void afterCompositeStateExited(CompositeState<TransitionInput> composite, StateMachine<?,TransitionInput> machine) {
		System.out.println("After exiting composite state ("+composite+") on machine ("+machine+")");
	}
}
