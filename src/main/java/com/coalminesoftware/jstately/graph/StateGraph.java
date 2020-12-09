package com.coalminesoftware.jstately.graph;

import com.coalminesoftware.jstately.collection.Multimap;
import com.coalminesoftware.jstately.graph.state.CompositeState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.Transition;
import com.coalminesoftware.jstately.machine.StateMachine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

/** Representation of a state graph. */
public class StateGraph<TransitionInput> {
	/** Key under which global transitions are stored in {@link #transitionsByTail}. */
	static final State<?> GLOBAL_TRANSITION_KEY = null;

	private final State<TransitionInput> startState;
	private final Multimap<State<TransitionInput>, Transition<TransitionInput>> transitionsByTail;
	private final StartListener startListener;

	StateGraph(@Nonnull State<TransitionInput> startState,
			@Nonnull Multimap<State<TransitionInput>, Transition<TransitionInput>> transitionsByTail,
			@Nullable StartListener startListener) {
		this.startState = requireNonNull(startState);
		this.transitionsByTail = requireNonNull(transitionsByTail);
		this.startListener = startListener;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public Transition<TransitionInput> findFirstValidTransitionFromState(
			@Nonnull State<TransitionInput> state,
			@Nullable TransitionInput input) {
		for(Transition<TransitionInput> transition : transitionsByTail.get(state)) {
			if(transition.isValid(input)) {
				return transition;
			}
		}

		// A state can belong to multiple unrelated composites, each of with might be nested in
		// another. For each composite, traverse to its root, looking for a valid transition at
		// each level.
		for(CompositeState<TransitionInput> composite : state.getComposites()) {
			while(composite != null) {
				Transition<TransitionInput> transition = composite.findFirstValidTransition(input);
				if(transition != null) {
					return transition;
				}
				
				composite = composite.getParent();
			}
		}

		for(Transition<TransitionInput> transition : transitionsByTail.get((State<TransitionInput>) GLOBAL_TRANSITION_KEY)) {
			if(transition.isValid(input)) {
				return transition;
			}
		}

		return null;
	}

	public void notifyStartListener() {
		if (startListener != null) {
			startListener.onStart();
		}
	}

	@Nonnull
	public State<TransitionInput> getStartState() {
		return startState;
	}

	public interface StartListener {
		/** Called when a machine traversing the graph starts. See {@link StateMachine#start()}. */
		void onStart();
	}
}
