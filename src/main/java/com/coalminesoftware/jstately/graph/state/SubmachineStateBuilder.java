package com.coalminesoftware.jstately.graph.state;

import com.coalminesoftware.jstately.graph.StateGraph;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

public class SubmachineStateBuilder<TransitionInput> {
	private EntranceListener entranceListener;
	private ExitListener exitListener;
	private String description;
	private final StateGraph<TransitionInput> stateGraph;

	public SubmachineStateBuilder(@Nonnull StateGraph<TransitionInput> stateGraph) {
		this.stateGraph = requireNonNull(stateGraph, "A state graph is required.");
	}

	@Nonnull
	public SubmachineStateBuilder<TransitionInput> setEntranceListener(@Nullable EntranceListener entranceListener) {
		this.entranceListener = entranceListener;
		return this;
	}

	@Nonnull
	public SubmachineStateBuilder<TransitionInput> setExitListener(@Nullable ExitListener exitListener) {
		this.exitListener = exitListener;
		return this;
	}

	@Nonnull
	public SubmachineStateBuilder<TransitionInput> setDescription(@Nullable String description) {
		this.description = description;
		return this;
	}

	@Nonnull
	public SubmachineState<TransitionInput> build() {
		return new SubmachineState<>(entranceListener, exitListener, description, stateGraph);
	}
}
