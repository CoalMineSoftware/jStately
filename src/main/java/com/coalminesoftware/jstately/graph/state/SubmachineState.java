package com.coalminesoftware.jstately.graph.state;

import com.coalminesoftware.jstately.graph.StateGraph;

/**
 * Interface for a state that causes a "nested" state machine to be run.  When a SubmachineState is
 * entered, the state graph provided by {@link #getStateGraph()} is run in a new state machine.
 * The evaluation of inputs on the outer machine is delegated to the nested machine until the
 * nested machine reaches a {@link FinalState}, at which point the value of
 * {@link FinalState#getResult()} is evaluated as an input on the outer machine.  The state graph
 * provided by {@link #getStateGraph()} can also contain SubmachineStates, allowing a practically
 * unlimited amount of nesting.
 */
public interface SubmachineState <InputType> extends State<InputType> {
	StateGraph<InputType> getStateGraph();
}
