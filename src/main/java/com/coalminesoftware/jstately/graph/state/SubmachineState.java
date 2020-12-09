package com.coalminesoftware.jstately.graph.state;

import com.coalminesoftware.jstately.graph.StateGraph;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * When a SubmachineState is entered, the state graph provided by {@link #getStateGraph()} is run
 * in a new state machine. When the enclosing machine evaluates an input, it is delegated to
 * nested machine until the nested machine reaches a {@link FinalState}, at which point the value
 * of {@link FinalState#getResult()} is evaluated as an input on the enclosing machine.
 * <p>
 * The state graph provided by {@link #getStateGraph()} can also contain SubmachineStates, allowing
 * a practically unlimited amount of nesting.
 */
public class SubmachineState <TransitionInput> extends State<TransitionInput> {
	private final StateGraph<TransitionInput> stateGraph;

	SubmachineState(@Nullable EntranceListener entranceListener,
			@Nullable ExitListener exitListener,
			@Nullable String description,
			@Nonnull StateGraph<TransitionInput> stateGraph) {
		super(entranceListener, exitListener, description);
		this.stateGraph = requireNonNull(stateGraph, "A state graph is required");
	}

	@Nonnull
	public StateGraph<TransitionInput> getStateGraph() {
		return stateGraph;
	}
}
