package com.coalminesoftware.jstately.integration;

import com.coalminesoftware.jstately.graph.StateGraph;
import com.coalminesoftware.jstately.graph.state.DefaultState;
import com.coalminesoftware.jstately.graph.state.DefaultSubmachineState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.machine.DefaultInputAdapter;
import com.coalminesoftware.jstately.machine.StateMachine;
import com.coalminesoftware.jstately.test.DefaultTransition;
import com.coalminesoftware.jstately.test.Event;
import com.coalminesoftware.jstately.test.EventType;
import com.coalminesoftware.jstately.test.TestStateMachineEventListener;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class SubmachineTransitionTest {
	@Test
	@SuppressWarnings("unchecked")
	public void testTransitionToSameSubmachineState() {
		TestContext context = new TestContext();

		context.machine.transition(context.outerStateGraphStartState, context.firstInnerStateGraphStartState);

		context.listener.assertEventsOccurred(
				Event.forStateExit(context.firstInnerStateGraphStartState),
				Event.forStateEntry(context.firstInnerStateGraphStartState));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testTransitionToDifferentSubmachineState() {
		TestContext context = new TestContext();

		context.machine.transition(context.outerStateGraphStartState, context.firstInnerStateGraphSecondState);

		context.listener.assertEventsOccurred(
				Event.forStateExit(context.firstInnerStateGraphStartState),
				Event.forStateEntry(context.firstInnerStateGraphSecondState));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testTransitionToSameOuterMachineState() {
		TestContext context = new TestContext();

		context.machine.transition(context.outerStateGraphStartState);

		context.listener.assertEventsOccurred(
				Event.forStateExit(context.firstInnerStateGraphStartState),
				Event.forStateExit(context.outerStateGraphStartState),
				Event.forStateEntry(context.outerStateGraphStartState),
				Event.forStateEntry(context.firstInnerStateGraphStartState)); // Called implicitly when the outer machines submachine is started
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testTransitionToDifferentOuterMachineState() {
		TestContext context = new TestContext();

		context.machine.transition(context.outerStateGraphSecondState);

		context.listener.assertEventsOccurred(
				Event.forStateExit(context.firstInnerStateGraphStartState),
				Event.forStateExit(context.outerStateGraphStartState),
				Event.forStateEntry(context.outerStateGraphSecondState),
				Event.forStateEntry(context.secondInnerStateGraphStartState));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testTransitionToDifferentOuterMachineStateWithInnerMachineState() {
		TestContext context = new TestContext();

		context.machine.transition(context.outerStateGraphSecondState, context.secondInnerStateGraphStartState);

		context.listener.assertEventsOccurred(
				Event.forStateExit(context.firstInnerStateGraphStartState),
				Event.forStateExit(context.outerStateGraphStartState),
				Event.forStateEntry(context.outerStateGraphSecondState),
				Event.forStateEntry(context.secondInnerStateGraphStartState));
	}


	private static class TestContext {
		public StateGraph<Object> firstInnerStateGraph;
		public State<Object> firstInnerStateGraphStartState;
		public State<Object> firstInnerStateGraphSecondState;
		public StateGraph<Object> secondInnerStateGraph;
		public State<Object> secondInnerStateGraphStartState;
		public State<Object> secondInnerStateGraphSecondState;
		public StateGraph<Object> outerStateGraph;
		public State<Object> outerStateGraphStartState;
		public State<Object> outerStateGraphSecondState;
		public StateMachine<Object,Object> machine;
		public TestStateMachineEventListener<Object> listener;

		public TestContext() {
			firstInnerStateGraph = new StateGraph<>();
			firstInnerStateGraphStartState = new DefaultState<>("First inner start");
			firstInnerStateGraph.setStartState(firstInnerStateGraphStartState);
			firstInnerStateGraphSecondState = new DefaultState<>("First inner second");
			firstInnerStateGraph.addTransition(firstInnerStateGraphStartState, new DefaultTransition<>(firstInnerStateGraphSecondState));

			secondInnerStateGraph = new StateGraph<>();
			secondInnerStateGraphStartState = new DefaultState<>("Second inner start");
			secondInnerStateGraph.setStartState(secondInnerStateGraphStartState);
			secondInnerStateGraphSecondState = new DefaultState<>("Second inner second");
			secondInnerStateGraph.addTransition(secondInnerStateGraphStartState, new DefaultTransition<>(secondInnerStateGraphSecondState));

			outerStateGraph = new StateGraph<>();
			outerStateGraphStartState = new DefaultSubmachineState<>("Outer start", firstInnerStateGraph);
			outerStateGraph.setStartState(outerStateGraphStartState);
			outerStateGraphSecondState = new DefaultSubmachineState<>("Outer second", secondInnerStateGraph);
			outerStateGraph.addTransition(outerStateGraphStartState, new DefaultTransition<>(outerStateGraphSecondState));

			machine = new StateMachine<>(outerStateGraph, new DefaultInputAdapter<>());

			machine.start();
			assertEquals("Couldn't initialize test.  Please check for failures elsewhere.",
					Arrays.asList(outerStateGraphStartState, firstInnerStateGraphStartState),
					machine.getStates());

			listener = new TestStateMachineEventListener<>(EventType.STATE_ENTERED, EventType.STATE_EXITED);
			machine.addEventListener(listener);
		}
	}
}
