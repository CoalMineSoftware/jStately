package com.coalminesoftware.jstately.graph.state;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * A state that provides a "result" value that can indicate the result of running a state machine.
 * This is used primarily for graphs that will be used in {@link SubmachineState}s, where the result
 * of running the "inner" state machine gets used as an input for the "outer" state machine.
 */
public class FinalState<Result> extends State<Result> {
	private final Result result;

	FinalState(@Nullable EntranceListener entranceListener,
			@Nullable ExitListener exitListener,
			@Nullable String description,
			@Nonnull Result result) {
		super(entranceListener, exitListener, description);
		this.result = requireNonNull(result, "Result is required");
	}

	@Nonnull
	public Result getResult() {
		return result;
	}
}
