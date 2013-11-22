package com.coalmine.jstately.graph.state;

import com.coalmine.jstately.graph.StateGraph;


public class DefaultSubmachineState<TransitionInput> extends DefaultState<TransitionInput> implements SubmachineState<TransitionInput> {
	private StateGraph<TransitionInput> stateGraph;

	public DefaultSubmachineState() { }

	public DefaultSubmachineState(String description, StateGraph<TransitionInput> stateGraph) {
		super(description);
		this.stateGraph = stateGraph;
	}


	public StateGraph<TransitionInput> getStateGraph() {
		return stateGraph;
	}
	public void setStateGraph(StateGraph<TransitionInput> stateGraph) {
		this.stateGraph = stateGraph;
	}
}

