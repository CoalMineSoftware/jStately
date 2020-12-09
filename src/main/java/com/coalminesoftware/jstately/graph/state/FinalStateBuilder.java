package com.coalminesoftware.jstately.graph.state;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

public class FinalStateBuilder<Result> {
	private final Result result;
	private EntranceListener entranceListener;
	private ExitListener exitListener;
	private String description;

	public FinalStateBuilder(@Nonnull Result result) {
		this.result = requireNonNull(result, "Result is required");
	}

	@Nonnull public FinalStateBuilder<Result> setEntranceListener(@Nullable EntranceListener entranceListener) {
		this.entranceListener = entranceListener;
		return this;
	}

	@Nonnull public FinalStateBuilder<Result> setExitListener(@Nullable ExitListener exitListener) {
		this.exitListener = exitListener;
		return this;
	}

	@Nonnull public FinalStateBuilder<Result> setDescription(@Nullable String description) {
		this.description = description;
		return this;
	}

	@Nonnull
	public FinalState<Result> build() {
		return new FinalState<>(entranceListener, exitListener, description, result);
	}
}
