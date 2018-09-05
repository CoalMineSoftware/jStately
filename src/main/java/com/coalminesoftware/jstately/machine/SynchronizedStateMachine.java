package com.coalminesoftware.jstately.machine;

import com.coalminesoftware.jstately.graph.StateGraph;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.Transition;
import com.coalminesoftware.jstately.machine.listener.StateMachineEventListener;

import java.util.List;

/**
 * Subclass of {@link StateMachine} that synchronizes calls to its public methods. The machine
 * instance is used as the mutex object by default but a different object can be provided during
 * instantiation if needed.
 */
public class SynchronizedStateMachine<MachineInput,TransitionInput>
		extends StateMachine<MachineInput,TransitionInput> {
	private final Object mutex;

	/**
	 * Instantiate a machine with the same input type as its graph’s transitions, and a
	 * {@link DefaultInputAdapter} instance as its adapter.
	 */
	public static <T> SynchronizedStateMachine<T, T> newStateMachine(StateGraph<T> stateGraph) {
		return new SynchronizedStateMachine<>(stateGraph, new DefaultInputAdapter<T>());
	}

	public SynchronizedStateMachine(StateGraph<TransitionInput> stateGraph,
			InputAdapter<MachineInput, TransitionInput> inputAdapter) {
		super(stateGraph, inputAdapter);
		mutex = this;
	}

	public SynchronizedStateMachine(StateGraph<TransitionInput> stateGraph,
			InputAdapter<MachineInput, TransitionInput> inputAdapter, Object mutex) {
		super(stateGraph, inputAdapter);
		this.mutex = mutex;
	}

	@Override
	public void start() {
		synchronized (mutex) {
			super.start();
		}
	}

	@Override
	public boolean hasStarted() {
		synchronized (mutex) {
			return super.hasStarted();
		}
	}

	@Override
	public boolean evaluateInput(MachineInput machineInput) {
		synchronized (mutex) {
			return super.evaluateInput(machineInput);
		}
	}

	@Override
	public void evaluateInputOrThrow(MachineInput machineInput) throws InterruptedException {
		synchronized (mutex) {
			super.evaluateInputOrThrow(machineInput);
		}
	}

	@Override
	public Transition<TransitionInput> findFirstValidTransitionFromCurrentState(TransitionInput transitionInput) {
		synchronized (mutex) {
			return super.findFirstValidTransitionFromCurrentState(transitionInput);
		}
	}

	@Override
	public void transition(State<TransitionInput> newState, State<TransitionInput>... submachineStates) {
		synchronized (mutex) {
			super.transition(newState, submachineStates);
		}
	}

	@Override
	public List<State<TransitionInput>> getStates() {
		synchronized (mutex) {
			return super.getStates();
		}
	}

	@Override
	public State<TransitionInput> getState() {
		synchronized (mutex) {
			return super.getState();
		}
	}

	@Override
	public void addEventListener(StateMachineEventListener<TransitionInput> eventListener) {
		synchronized (mutex) {
			super.addEventListener(eventListener);
		}
	}

	@Override
	public void removeEventListener(StateMachineEventListener<TransitionInput> eventListener) {
		synchronized (mutex) {
			super.removeEventListener(eventListener);
		}
	}
}
