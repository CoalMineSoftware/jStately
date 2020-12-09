package com.coalminesoftware.jstately.graph.state;

import com.coalminesoftware.jstately.graph.transition.Transition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class CompositeStateBuilder<TransitionInput> {
	private final List<State<TransitionInput>> states = new ArrayList<>();
	private final List<CompositeState<TransitionInput>> composites = new ArrayList<>();
	private final List<Transition<TransitionInput>> transitions = new ArrayList<>();
	private EntranceListener entranceListener;
	private ExitListener exitListener;
	private String description;

	@Nonnull
	public CompositeStateBuilder<TransitionInput> addState(@Nonnull State<TransitionInput> state) {
		states.add(requireNonNull(state, "State is required"));
		return this;
	}

	@Nonnull
	public CompositeStateBuilder<TransitionInput> addCompositeState(@Nonnull CompositeState<TransitionInput> composite) {
		composites.add(requireNonNull(composite, "Composite state is required"));
		return this;
	}

	@Nonnull
	public CompositeStateBuilder<TransitionInput> addTransition(@Nonnull Transition<TransitionInput> transition) {
		transitions.add(requireNonNull(transition, "Transition is required"));
		return this;
	}

	@Nonnull
	public CompositeStateBuilder<TransitionInput> setEntranceListener(@Nullable EntranceListener entranceListener) {
		this.entranceListener = entranceListener;
		return this;
	}

	@Nonnull
	public CompositeStateBuilder<TransitionInput> setExitListener(@Nullable ExitListener exitListener) {
		this.exitListener = exitListener;
		return this;
	}

	@Nonnull
	public CompositeStateBuilder<TransitionInput> setDescription(@Nullable String description) {
		this.description = description;
		return this;
	}

	@Nonnull
	public CompositeState<TransitionInput> build() {
		return new CompositeState<>(states, composites, transitions, entranceListener, exitListener, description);
	}
}
