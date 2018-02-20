package com.coalminesoftware.jstately.graph;

import java.util.HashSet;
import java.util.Set;

import com.coalminesoftware.jstately.collection.Multimap;
import com.coalminesoftware.jstately.graph.composite.CompositeState;
import com.coalminesoftware.jstately.graph.state.FinalState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.Transition;

/** Representation of a state graph/diagram. */
public class StateGraph<TransitionInput> {
	/** Key under which global transitions are stored in {@link #transitionsByTail}. */
	private final State<TransitionInput> GLOBAL_TRANSITION_KEY = null;

	private State<TransitionInput> startState;
	private Multimap<State<TransitionInput>,Transition<TransitionInput>> transitionsByTail = new Multimap<>();

	public State<TransitionInput> getStartState() {
		return startState;
	}

	public void setStartState(State<TransitionInput> startState) {
		if(startState instanceof FinalState) {
			throw new IllegalArgumentException("A graph's start state cannot be a final state.");
		}
		this.startState = startState;
	}

	public Set<Transition<TransitionInput>> getTransitions() {
		return new HashSet<>(transitionsByTail.values());
	}

	public void addTransition(State<TransitionInput> transitionTail, Transition<TransitionInput> transition) {
		if(transitionTail==null) {
			throw new IllegalArgumentException("Tail cannot be be null.");
		}
		transitionsByTail.put(transitionTail,transition);
	}

	public void addSelfTransition(Transition<TransitionInput> transition) {
		if(transition.getHead()==null) {
			throw new IllegalArgumentException("A Transition's head/tail cannot be be null.");
		}
		transitionsByTail.put(transition.getHead(),transition);
	}

	/**
	 * Adds a transitions that will be evaluated if no valid transition is found for the given
	 * input from the current state or enclosing CompositeState.
	 */
	public void addGlobalTransition(Transition<TransitionInput> transition) {
		transitionsByTail.put(GLOBAL_TRANSITION_KEY, transition);
	}

	/** @return All of the graph's transitions that apply from any state. */
	public Set<Transition<TransitionInput>> getGlobalTransitions() {
		return transitionsByTail.get(GLOBAL_TRANSITION_KEY);
	}

	public Transition<TransitionInput> findFirstValidTransitionFromState(State<TransitionInput> tailState, TransitionInput input) {
		for(Transition<TransitionInput> transition : transitionsByTail.get(tailState)) {
			if(transition.isValid(input)) {
				return transition;
			}
		}

		for(CompositeState<TransitionInput> composite : tailState.getComposites()) {
			while(composite != null) {
				Transition<TransitionInput> transition = composite.findFirstValidTransition(input);
				if(transition != null) {
					return transition;
				}
				
				composite = composite.getParent();
			}
		}

		for(Transition<TransitionInput> transition : transitionsByTail.get(null)) {
			if(transition.isValid(input)) {
				return transition;
			}
		}

		return null;
	}

	/** Called when the machine operating on the graph starts. */
	public void onStart() {}
}
