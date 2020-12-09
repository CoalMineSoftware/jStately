package com.coalminesoftware.jstately.test;

import com.coalminesoftware.jstately.graph.state.CompositeState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.Transition;
import com.coalminesoftware.jstately.machine.StateMachine;

public class Event {
	private EventType type;
	private Object value;
	private StateMachine<?,Object> machine;

	protected Event(EventType type, Object value, StateMachine<?,Object> machine) {
		this.type = type;
		this.value = value;
		this.machine = machine;
	}

	public EventType getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public StateMachine<?, Object> getMachine() {
		return machine;
	}

	public static Event forInputEvaluated(Object input, StateMachine<?,Object> machine) {
		return new Event(EventType.INPUT_EVALUATED, input, machine);
	}

	public static Event forInputEvaluated(Object input) {
		return forInputEvaluated(input, null);
	}

	public static Event forStateEntry(State<?> state, StateMachine<?,Object> machine) {
		return new Event(EventType.STATE_ENTERED, state, machine);
	}

	public static Event forStateEntry(State<?> state) {
		return forStateEntry(state, null);
	}

	public static Event forStateExit(State<?> state, StateMachine<?,Object> machine) {
		return new Event(EventType.STATE_EXITED, state, machine);
	}

	public static Event forStateExit(State<?> state) {
		return forStateExit(state, null);
	}

	public static Event forCompositeStateEntry(CompositeState<?> composite, StateMachine<?,Object> machine) {
		return new Event(EventType.COMPOSITE_STATE_ENTERED, composite, machine);
	}

	public static Event forCompositeStateEntry(CompositeState<?> composite) {
		return forCompositeStateEntry(composite, null);
	}

	public static Event forCompositeStateExit(CompositeState<?> composite, StateMachine<?,Object> machine) {
		return new Event(EventType.COMPOSITE_STATE_EXITED, composite, machine);
	}

	public static Event forCompositeStateExit(CompositeState<?> composite) {
		return forCompositeStateExit(composite, null);
	}

	public static Event forTransitionFollowed(Transition<?> transition, StateMachine<?,Object> machine) {
		return new Event(EventType.TRANSITION_FOLLOWED, transition, machine);
	}

	public static Event forTransitionFollowed(Transition<?> transition) {
		return forTransitionFollowed(transition, null);
	}

	public static Event forNoTransitionFound(StateMachine<?,Object> machine, Object expectedInput) {
		return new Event(EventType.NO_VALID_TRANSITION_FOUND, expectedInput, machine);
	}

	public static Event forNoTransitionFound(Object expectedInput) {
		return forNoTransitionFound(null, expectedInput);
	}

	@Override
	public String toString() {
		return super.toString()+"[type="+type.name()+",value="+value+",machine="+machine+"]";
	}
}
