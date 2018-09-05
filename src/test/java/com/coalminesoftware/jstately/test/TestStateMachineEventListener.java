package com.coalminesoftware.jstately.test;

import com.coalminesoftware.jstately.collection.CollectionUtil;
import com.coalminesoftware.jstately.graph.composite.CompositeState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.Transition;
import com.coalminesoftware.jstately.machine.StateMachine;
import com.coalminesoftware.jstately.machine.listener.DefaultStateMachineEventListener;
import com.coalminesoftware.jstately.machine.listener.StateMachineEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.fail;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

/** {@link StateMachineEventListener} implementation for use in test cases. This listener stores events that occur,
 * allowing users to assert that a certain sequence of events occurred, using {@link #assertEventsOccurred(Event...)}. */
public class TestStateMachineEventListener<TransitionInput> extends DefaultStateMachineEventListener<TransitionInput> {
	private List<Event> observedEvents = new ArrayList<>();
	private Set<EventType> allowedEventTypes;

	/** Creates a listener that records only the given events, or all events if none are provided. */
	public TestStateMachineEventListener(EventType... allowedEventTypes) {
		if(allowedEventTypes.length == 0) {
			this.allowedEventTypes = null;
		} else {
			this.allowedEventTypes = CollectionUtil.asMutableSet(allowedEventTypes);
		}
	}

	/** Asserts that the given Events (and only the given Events) occurred in the given order. Keep in
	 * mind that the observed Events are limited to the EventTypes given when constructing the listener. */
	public void assertEventsOccurred(Event... expectedEvents) {
		assertEventsOccurred(true, expectedEvents);
	}

	/** Asserts that the given Events (and only the given Events) occurred in the given order. Keep
	 * in mind that the observed Events are limited to the EventTypes given when constructing the
	 * listener. This method also clears the list of observed events after making the assertion. */
	public void assertEventsOccurred(boolean ignoreMachine, Event... expectedEvents) {
		List<Event> expectedEventList = Arrays.asList(expectedEvents);
		assertThat("The exact sequence of expected events was not observed. Expected: " + expectedEventList + " but was: " + observedEvents,
				testEventListEquality(ignoreMachine, expectedEventList, observedEvents),
				is(true));

		clearObservedEvents();
	}

	/** Asserts that the given Event happened, ignoring the machine on which the event occurred. */
	public void assertEventOccurred(Event expectedEvent) {
		for(Event observedEvent : observedEvents) {
			if(areEventsEqual(true, expectedEvent, observedEvent)) {
				return;
			}
		}

		fail("The expected event was not observed");
	}

	public void clearObservedEvents() {
		observedEvents.clear();
	}

	private boolean testEventListEquality(boolean ignoreMachine, List<Event> firstEvents, List<Event> secondEvents) {
		if(firstEvents.size() != secondEvents.size()) {
			return false;
		}

		for(int i=0; i<firstEvents.size(); i++) {
			if(!areEventsEqual(ignoreMachine, firstEvents.get(i), secondEvents.get(i))) {
				return false;
			}
		}

		return true;
	}

	private boolean areEventsEqual(boolean ignoreMachine, Event firstEvent, Event secondEvent) {
		boolean typesEqual = firstEvent.getType().equals(secondEvent.getType());

		boolean valuesEqual = firstEvent.getValue()==null?
				secondEvent.getValue()==null :
				firstEvent.getValue().equals(secondEvent.getValue());

		boolean machinesEqual = ignoreMachine ||
				firstEvent.getMachine().equals(secondEvent.getMachine());

		return typesEqual && valuesEqual && machinesEqual;
	}

	@SuppressWarnings("unchecked")
	private void logEvent(EventType type, Object value, StateMachine<?, TransitionInput> machine) {
		if (allowedEventTypes == null || allowedEventTypes.contains(type)) {
			observedEvents.add(new Event(type, value, (StateMachine<?,Object>)machine));
		}
	}

	@Override
	public void beforeStateEntered(State<TransitionInput> state, StateMachine<?,TransitionInput> machine) {
		logEvent(EventType.STATE_ENTERED, state, machine);
	}

	@Override
	public void beforeStateExited(State<TransitionInput> state, StateMachine<?,TransitionInput> machine) {
		logEvent(EventType.STATE_EXITED, state, machine);
	}

	@Override
	public void beforeCompositeStateEntered(CompositeState<TransitionInput> composite, StateMachine<?,TransitionInput> machine) {
		logEvent(EventType.COMPOSITE_STATE_ENTERED, composite, machine);
	}

	@Override
	public void beforeCompositeStateExited(CompositeState<TransitionInput> composite, StateMachine<?,TransitionInput> machine) {
		logEvent(EventType.COMPOSITE_STATE_EXITED, composite, machine);
	}

	@Override
	public void beforeTransition(Transition<TransitionInput> transition, TransitionInput input, StateMachine<?,TransitionInput> machine) {
		logEvent(EventType.TRANSITION_FOLLOWED, transition, machine);
	}

	@Override
	public void noValidTransition(TransitionInput input, StateMachine<?,TransitionInput> machine) {
		logEvent(EventType.NO_VALID_TRANSITION_FOUND, input, machine);
	}

	@Override
	public void beforeEvaluatingInput(TransitionInput input, StateMachine<?, TransitionInput> machine) {
		logEvent(EventType.INPUT_EVALUATED, input, machine);
	}
}
