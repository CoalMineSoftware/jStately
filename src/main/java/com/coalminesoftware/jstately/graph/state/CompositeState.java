package com.coalminesoftware.jstately.graph.state;

import com.coalminesoftware.jstately.graph.transition.Transition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.coalminesoftware.jstately.collection.CollectionUtil.unmodifiableCopy;
import static java.util.Objects.requireNonNull;

/**
 * Defines a superstate containing multiple child states. Composite states can be nested.
 * <p>
 * When a state machine's current state has no valid transition for a given input, the machine
 * evaluates the enclosing composite state's transitions, followed by the composite state's
 * enclosing state, and so on.
 */
public class CompositeState<TransitionInput> {
	private CompositeState<TransitionInput> parent;
	private final List<Transition<TransitionInput>> transitions;
	private final EntranceListener entranceListener;
	private final ExitListener exitListener;
	private final String description;

	CompositeState(List<State<TransitionInput>> states,
			@Nonnull List<CompositeState<TransitionInput>> composites,
			@Nonnull List<Transition<TransitionInput>> transitions,
			@Nullable EntranceListener entranceListener,
			@Nullable ExitListener exitListener,
			@Nullable String description) {
		this.transitions = unmodifiableCopy(requireNonNull(transitions, "Transitions are required"));
		this.entranceListener = entranceListener;
		this.exitListener = exitListener;
		this.description = description;

		for(State<TransitionInput> state : states) {
			state.addComposite(this);
		}

		for(CompositeState<TransitionInput> composite : composites) {
			if (composite.parent != null) {
				throw new IllegalStateException("Composite already has a parent.");
			}
			composite.parent = this;
		}
	}

	@Nullable
	public Transition<TransitionInput> findFirstValidTransition(@Nullable TransitionInput input) {
		return transitions.stream()
				.filter(transition -> transition.isValid(input)).findFirst()
				.orElse(null);
	}

	@Nonnull
	public Set<Transition<TransitionInput>> findValidTransitions(@Nullable TransitionInput input) {
		return transitions.stream()
				.filter(transition -> transition.isValid(input))
				.collect(Collectors.toSet());
	}

	@Nullable
	public CompositeState<TransitionInput> getParent() {
		return parent;
	}

	public void notifyEntranceListener() {
		if (entranceListener != null) {
			entranceListener.onEnter();
		}
	}

	public void notifyExitListener() {
		if (exitListener != null) {
			exitListener.onExit();
		}
	}

	@Nullable
	public String getDescription() {
		return description;
	}

	@Nonnull
	@Override
	public String toString() {
		return super.toString() + "[description=" + getDescription() + "]";
	}
}
