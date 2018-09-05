package com.coalminesoftware.jstately.graph;

import com.coalminesoftware.jstately.collection.Multimap;
import com.coalminesoftware.jstately.graph.composite.CompositeState;
import com.coalminesoftware.jstately.graph.state.FinalState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.Transition;
import com.coalminesoftware.jstately.machine.StateMachine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.coalminesoftware.jstately.ParameterValidation.assertNotNull;

/** Representation of a state graph. */
public class StateGraph<TransitionInput> {
	/** Key under which global transitions are stored in {@link #transitionsByTail}. */
	private static final State GLOBAL_TRANSITION_KEY = null;

	private State<TransitionInput> startState;
	private Multimap<State<TransitionInput>, Transition<TransitionInput>> transitionsByTail = new Multimap<>();

	public StateGraph() { }

	public StateGraph(State<TransitionInput> startState) {
		setStartState(startState);
	}

	public State<TransitionInput> getStartState() {
		return startState;
	}

	public StateGraph<TransitionInput> setStartState(State<TransitionInput> startState) {
		if(startState instanceof FinalState) {
			throw new IllegalArgumentException("Start state cannot be an instance of FinalState");
		}

		this.startState = startState;

		return this;
	}

	public StateGraph<TransitionInput> addTransition(State<TransitionInput> transitionTail, Transition<TransitionInput> transition) {
		assertNotNull("Tail state is required.", transitionTail);
		assertTransitionValidity(transition);
		transitionsByTail.put(transitionTail,transition);

		return this;
	}

	public StateGraph<TransitionInput> addSelfTransition(Transition<TransitionInput> transition) {
		assertTransitionValidity(transition);
		transitionsByTail.put(transition.getHead(),transition);

		return this;
	}

	public Set<Transition<TransitionInput>> getTransitions() {
		return new HashSet<>(transitionsByTail.values());
	}

	/**
	 * Adds a transitions that will be evaluated if no valid transition is found for the given
	 * input from the current state or enclosing {@link CompositeState}.
	 */
	@SuppressWarnings("unchecked")
	public StateGraph<TransitionInput> addGlobalTransition(Transition<TransitionInput> transition) {
		assertTransitionValidity(transition);
		transitionsByTail.put((State<TransitionInput>) GLOBAL_TRANSITION_KEY, transition);

		return this;
	}

	/** @return All of the graph's transitions that apply from any state. */
	@SuppressWarnings("unchecked")
	public Set<Transition<TransitionInput>> getGlobalTransitions() {
		return Collections.unmodifiableSet(
				transitionsByTail.get((State<TransitionInput>) GLOBAL_TRANSITION_KEY));
	}

	@SuppressWarnings("unchecked")
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

		for(Transition<TransitionInput> transition : transitionsByTail.get((State<TransitionInput>) GLOBAL_TRANSITION_KEY)) {
			if(transition.isValid(input)) {
				return transition;
			}
		}

		return null;
	}

	private void assertTransitionValidity(Transition<TransitionInput> transition) {
		assertNotNull("Transition is required.", transition);
		assertNotNull("A transition head is required.", transition.getHead());
	}

	/** Called when a machine traversing the graph starts. See {@link StateMachine#start()}. */
	public void onStart() {}
}
