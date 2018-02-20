package com.coalminesoftware.jstately.graph.composite;

import java.util.HashSet;
import java.util.Set;

import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.Transition;


/**
 * Defines a superstate that wraps multiple child states, allowing API users to define behavior for
 * when the group is entered or exited.  Composite states can also have transitions, which can be
 * traversed from any of the child states if none of their transitions are valid for a given input
 * but the composite state's transition is.
 */
public class CompositeState<TransitionInput> {
	private CompositeState<TransitionInput> parent;
	private Set<Transition<TransitionInput>> transitions = new HashSet<>();
	private String description;

	public CompositeState() { }

	public CompositeState(String description) {
		this.description = description;
	}

	public CompositeState(String description, State<TransitionInput>... states) {
		this(description);

		addStates(states);
	}

	public void addComposite(CompositeState<TransitionInput> composite) {
		composite.setParent(this);
	}

	public void addState(State<TransitionInput> state) {
		state.addComposite(this);
	}

	public void addStates(State<TransitionInput>... states) {
		for(State<TransitionInput> state : states) {
			addState(state);
		}
	}

	public void addTransition(Transition<TransitionInput> transition) {
		transitions.add(transition);
	}

	public Set<Transition<TransitionInput>> getTransitions() {
		return transitions;
	}

	public Transition<TransitionInput> findFirstValidTransition(TransitionInput input) {
		for(Transition<TransitionInput> transition : transitions) {
			if(transition.isValid(input)) {
				return transition;
			}
		}

		return null;
	}

	public Set<Transition<TransitionInput>> findValidTransitions(TransitionInput input) {
		Set<Transition<TransitionInput>> validTransitions = new HashSet<>();

		for(Transition<TransitionInput> transition : transitions) {
			if(transition.isValid(input)) {
				validTransitions.add(transition);
			}
		}

		return validTransitions;
	}

	public CompositeState<TransitionInput> getParent() {
		return parent;
	}
	public void setParent(CompositeState<TransitionInput> parent) {
		this.parent = parent;
	}

	public void onEnter() { }

	public void onExit() { }

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return super.toString()+"[description="+description+"]";
	}
}
