package com.coalminesoftware.jstately.graph.state;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StateBuilder<TransitionInput> {
	private EntranceListener entranceListener;
	private ExitListener exitListener;
	private String description;

	@Nonnull
	public StateBuilder<TransitionInput> setEntranceListener(@Nullable EntranceListener entranceListener) {
		this.entranceListener = entranceListener;
		return this;
	}

	@Nonnull
	public StateBuilder<TransitionInput> setExitListener(@Nullable ExitListener exitListener) {
		this.exitListener = exitListener;
		return this;
	}

	@Nonnull
	public StateBuilder<TransitionInput> setDescription(@Nullable String description) {
		this.description = description;
		return this;
	}

	@Nonnull
	public State<TransitionInput> build() {
		return new State<>(entranceListener, exitListener, description);
	}
}
