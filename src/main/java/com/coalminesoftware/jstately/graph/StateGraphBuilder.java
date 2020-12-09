package com.coalminesoftware.jstately.graph;

import com.coalminesoftware.jstately.collection.Multimap;
import com.coalminesoftware.jstately.graph.state.CompositeState;
import com.coalminesoftware.jstately.graph.state.FinalState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.Transition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

public class StateGraphBuilder<TransitionInput> {
	private final State<TransitionInput> startState;
	private final Multimap<State<TransitionInput>, Transition<TransitionInput>> transitionsByTail = new Multimap<>();
	private StateGraph.StartListener startListener;

	public StateGraphBuilder(@Nonnull State<TransitionInput> startState) {
		if(startState instanceof FinalState) {
			throw new IllegalArgumentException("Start state cannot be an instance of FinalState");
		}
		this.startState = requireNonNull(startState, "Start state is required");
	}

	@Nonnull
	public StateGraphBuilder<TransitionInput> addTransition(
			@Nonnull State<TransitionInput> transitionTail,
			@Nonnull Transition<TransitionInput> transition) {
		transitionsByTail.put(
				requireNonNull(transitionTail, "Tail state is required"),
				requireNonNull(transition, "Transition is required"));

		return this;
	}

	@Nonnull
	public StateGraphBuilder<TransitionInput> addSelfTransition(@Nonnull Transition<TransitionInput> transition) {
		requireNonNull(transition, "Transition is required");
		return addTransition(transition.getHead(), transition);
	}

	/**
	 * Adds a transitions that will be evaluated if no valid transition is found for the given
	 * input from the current state or enclosing {@link CompositeState}.
	 */
	@SuppressWarnings("unchecked")
	@Nonnull
	public StateGraphBuilder<TransitionInput> addGlobalTransition(Transition<TransitionInput> transition) {
		transitionsByTail.put(
				(State<TransitionInput>) StateGraph.GLOBAL_TRANSITION_KEY,
				requireNonNull(transition, "Transition is required"));

		return this;
	}

	@Nonnull
	public StateGraphBuilder<TransitionInput> setStartListener(@Nullable StateGraph.StartListener listener) {
		startListener = listener;
		return this;
	}

	@Nonnull
	public StateGraph<TransitionInput> build() {
		return new StateGraph<>(startState, transitionsByTail, startListener);
	}
}
