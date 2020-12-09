package com.coalminesoftware.jstately.graph.transition;

import com.coalminesoftware.jstately.graph.state.CompositeState;
import com.coalminesoftware.jstately.graph.state.State;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * Defines a transition to a {@link State} from either another state, a {@link CompositeState}
 * or globally (from any state if another valid transition is not found).
 * 
 * @see com.coalminesoftware.jstately.graph.StateGraphBuilder#addTransition(State, Transition)
 * @see com.coalminesoftware.jstately.graph.StateGraphBuilder#addGlobalTransition(Transition)
 * @see com.coalminesoftware.jstately.graph.state.CompositeStateBuilder#addTransition(Transition)
 */
public class Transition<TransitionInput> {
	private final State<TransitionInput> head;
	private final Predicate<TransitionInput> validityPredicate;
	private final TransitionListener<TransitionInput> transitionListener;

	Transition(@Nonnull State<TransitionInput> head,
			@Nonnull Predicate<TransitionInput> validityPredicate,
			@Nullable TransitionListener<TransitionInput> transitionListener) {
		this.head = requireNonNull(head, "Head is required");
		this.validityPredicate = requireNonNull(validityPredicate, "Validity predicate is required");
		this.transitionListener = transitionListener;
	}

	/** @return State that transition transitions to. */
	@Nonnull
	public State<TransitionInput> getHead() {
		return head;
	}

	/**
	 * @param input Input from a state machine used to determine which state (if any) the machine can transition to.
	 * @return Whether or not the transition is valid for the given input.
	 */
	public boolean isValid(@Nullable TransitionInput input) {
		return validityPredicate.test(input);
	}

	public void notifyTransitionListener(@Nullable TransitionInput input) {
		if (transitionListener != null) {
			transitionListener.onTransition(input);
		}
	}

	@Nonnull
	public String toString() {
		return super.toString() + "[head=" + head + "]";
	}
}
