package com.coalminesoftware.jstately.graph.state;

/**
 * A state that provides a "result" value that can indicate the result of running a state machine.
 * This is used primarily for graphs that will be used in {@link SubmachineState}s, where the result
 * of running the "inner" state machine gets used as an input for the "outer" state machine.
 */
public interface FinalState<Result> extends State<Result> {
	Result getResult();
}
