package com.coalminesoftware.jstately.machine;

import com.coalminesoftware.jstately.graph.StateGraph;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.machine.input.InputAdapter;
import com.coalminesoftware.jstately.machine.listener.StateMachineEventListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Subclass of {@link StateMachine} that synchronizes calls to its public methods. The machine
 * instance is used as the mutex object by default but a different object can be provided during
 * instantiation if needed.
 */
public class SynchronizedStateMachine<MachineInput,TransitionInput> extends StateMachine<MachineInput,TransitionInput> {
	private final Object mutex;

	SynchronizedStateMachine(StateGraph<TransitionInput> graph,
			InputAdapter<MachineInput, TransitionInput> inputAdapter,
			List<StateMachineEventListener<TransitionInput>> listeners,
			Object mutex) {
		super(graph, inputAdapter, listeners);
		this.mutex = mutex == null ? this : mutex;
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
	public boolean evaluateInput(@Nullable MachineInput machineInput) {
		synchronized (mutex) {
			return super.evaluateInput(machineInput);
		}
	}

	@Override
	public void evaluateInputOrThrow(@Nullable MachineInput machineInput) throws InterruptedException {
		synchronized (mutex) {
			super.evaluateInputOrThrow(machineInput);
		}
	}

	@Override
	public void transition(@Nullable State<TransitionInput> newState, @Nullable State<TransitionInput>... submachineStates) {
		synchronized (mutex) {
			super.transition(newState, submachineStates);
		}
	}

	@Override
	@Nullable
	public List<State<TransitionInput>> getStates() {
		synchronized (mutex) {
			return super.getStates();
		}
	}

	@Override
	@Nullable
	public State<TransitionInput> getState() {
		synchronized (mutex) {
			return super.getState();
		}
	}

	@Override
	public void addEventListener(@Nonnull StateMachineEventListener<TransitionInput> eventListener) {
		synchronized (mutex) {
			super.addEventListener(eventListener);
		}
	}

	@Override
	public void removeEventListener(@Nonnull StateMachineEventListener<TransitionInput> eventListener) {
		synchronized (mutex) {
			super.removeEventListener(eventListener);
		}
	}
}
