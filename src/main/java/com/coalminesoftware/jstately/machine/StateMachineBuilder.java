package com.coalminesoftware.jstately.machine;

import com.coalminesoftware.jstately.graph.StateGraph;
import com.coalminesoftware.jstately.machine.input.InputAdapter;
import com.coalminesoftware.jstately.machine.input.PassthroughInputAdapter;
import com.coalminesoftware.jstately.machine.listener.StateMachineEventListener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class StateMachineBuilder<MachineInput,TransitionInput> {
	private final StateGraph<TransitionInput> stateGraph;
	private final InputAdapter<MachineInput, TransitionInput> inputAdapter;
	private final List<StateMachineEventListener<TransitionInput>> eventListeners = new ArrayList<>();

	/**
	 * Builds a machine with the same input type as its graph’s transitions, and a {@link PassthroughInputAdapter} as its adapter.
	 */
	@Nonnull
	public static <T> StateMachineBuilder<T, T> forMatchingInputTypes(@Nonnull StateGraph<T> stateGraph) {
		return new StateMachineBuilder<>(stateGraph, new PassthroughInputAdapter<>());
	}

	@Nonnull
	public StateMachineBuilder(
			@Nonnull StateGraph<TransitionInput> stateGraph,
			@Nonnull InputAdapter<MachineInput, TransitionInput> inputAdapter) {
		this.stateGraph = requireNonNull(stateGraph, "State graph is required");
		this.inputAdapter = requireNonNull(inputAdapter, "Input adapter is required");
	}

	@Nonnull
	public StateMachineBuilder<MachineInput,TransitionInput> addEventListener(@Nonnull StateMachineEventListener<TransitionInput> listener) {
		eventListeners.add(requireNonNull(listener, "Listener is required"));
		return this;
	}

	@Nonnull
	public StateMachine<MachineInput,TransitionInput> build() {
		return new StateMachine<>(stateGraph, inputAdapter, eventListeners);
	}
}
