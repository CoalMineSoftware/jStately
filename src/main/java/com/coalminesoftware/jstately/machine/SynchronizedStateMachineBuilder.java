package com.coalminesoftware.jstately.machine;

import com.coalminesoftware.jstately.graph.StateGraph;
import com.coalminesoftware.jstately.machine.input.InputAdapter;
import com.coalminesoftware.jstately.machine.input.PassthroughInputAdapter;
import com.coalminesoftware.jstately.machine.listener.StateMachineEventListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class SynchronizedStateMachineBuilder<MachineInput,TransitionInput> {
	private final StateGraph<TransitionInput> stateGraph;
	private final InputAdapter<MachineInput, TransitionInput> inputAdapter;
	private final List<StateMachineEventListener<TransitionInput>> eventListeners = new ArrayList<>();
	private Object mutex;

	/**
	 * Builds a machine with the same input type as its graph’s transitions, and a {@link PassthroughInputAdapter} as its adapter.
	 */
	@Nonnull
	public static <T> SynchronizedStateMachineBuilder<T, T> forMatchingInputTypes(@Nonnull StateGraph<T> stateGraph) {
		return new SynchronizedStateMachineBuilder<>(stateGraph, new PassthroughInputAdapter<>());
	}

	public SynchronizedStateMachineBuilder(
			@Nonnull StateGraph<TransitionInput> stateGraph,
			@Nonnull InputAdapter<MachineInput, TransitionInput> inputAdapter) {
		this.stateGraph = requireNonNull(stateGraph, "State graph is required");
		this.inputAdapter = requireNonNull(inputAdapter, "Input adapter is required");
	}

	@Nonnull
	public SynchronizedStateMachineBuilder<MachineInput,TransitionInput> addEventListener(@Nonnull StateMachineEventListener<TransitionInput> listener) {
		eventListeners.add(requireNonNull(listener, "Listener is required"));
		return this;
	}

	@Nonnull
	public SynchronizedStateMachineBuilder<MachineInput,TransitionInput> setMutex(@Nullable Object mutex) {
		this.mutex = mutex;
		return this;
	}

	@Nonnull
	public StateMachine<MachineInput,TransitionInput> build() {
		return new SynchronizedStateMachine<>(stateGraph, inputAdapter, eventListeners, mutex);
	}
}
