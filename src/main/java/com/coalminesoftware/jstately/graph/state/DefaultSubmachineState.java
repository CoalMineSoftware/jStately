package com.coalminesoftware.jstately.graph.state;

import com.coalminesoftware.jstately.graph.StateGraph;


/**
 * Basic {@link SubmachineState} implementation that provides the given StateGraph when a state machine
 * enters the state and sets up the state machine that will evaluate the SubmachineState's graph.
 */
public class DefaultSubmachineState<TransitionInput> extends DefaultState<TransitionInput> implements SubmachineState<TransitionInput> {
	private StateGraph<TransitionInput> stateGraph;

	public DefaultSubmachineState() { }

	public DefaultSubmachineState(String description, StateGraph<TransitionInput> stateGraph) {
		super(description);
		this.stateGraph = stateGraph;
	}

	@Override
	public StateGraph<TransitionInput> getStateGraph() {
		return stateGraph;
	}
	public void setStateGraph(StateGraph<TransitionInput> stateGraph) {
		this.stateGraph = stateGraph;
	}
}
