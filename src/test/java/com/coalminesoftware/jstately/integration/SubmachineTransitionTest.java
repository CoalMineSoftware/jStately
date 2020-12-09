package com.coalminesoftware.jstately.integration;

import com.coalminesoftware.jstately.graph.StateGraph;
import com.coalminesoftware.jstately.graph.StateGraphBuilder;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.state.StateBuilder;
import com.coalminesoftware.jstately.graph.state.SubmachineStateBuilder;
import com.coalminesoftware.jstately.machine.StateMachine;
import com.coalminesoftware.jstately.machine.StateMachineBuilder;
import com.coalminesoftware.jstately.test.Event;
import com.coalminesoftware.jstately.test.EventType;
import com.coalminesoftware.jstately.test.TestStateMachineEventListener;
import org.junit.Test;

import static com.coalminesoftware.jstately.test.MockingUtils.mockObjectTransition;
import static com.google.common.truth.Truth.assertWithMessage;

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
			firstInnerStateGraphStartState = new StateBuilder<>().setDescription("First inner start").build();
			firstInnerStateGraphSecondState = new StateBuilder<>().setDescription("First inner second").build();
			firstInnerStateGraph = new StateGraphBuilder<>(firstInnerStateGraphStartState)
					.addTransition(firstInnerStateGraphStartState, mockObjectTransition(true, firstInnerStateGraphSecondState))
					.build();

			secondInnerStateGraphStartState = new StateBuilder<>().setDescription("Second inner start").build();
			secondInnerStateGraphSecondState = new StateBuilder<>().setDescription("Second inner second").build();
			secondInnerStateGraph = new StateGraphBuilder<>(secondInnerStateGraphStartState)
					.addTransition(secondInnerStateGraphStartState, mockObjectTransition(true, secondInnerStateGraphSecondState))
					.build();

			outerStateGraphStartState = new SubmachineStateBuilder<>(firstInnerStateGraph).setDescription("Outer start").build();
			outerStateGraphSecondState = new SubmachineStateBuilder<>(secondInnerStateGraph).setDescription("Outer second").build();
			outerStateGraph = new StateGraphBuilder<>(outerStateGraphStartState)
					.addTransition(outerStateGraphStartState, mockObjectTransition(true, outerStateGraphSecondState))
					.build();

			machine = StateMachineBuilder.forMatchingInputTypes(outerStateGraph).build();

			machine.start();
			assertWithMessage("Couldn't initialize test.")
					.that(machine.getStates())
					.containsExactly(outerStateGraphStartState, firstInnerStateGraphStartState)
					.inOrder();

			listener = new TestStateMachineEventListener<>(EventType.STATE_ENTERED, EventType.STATE_EXITED);
			machine.addEventListener(listener);
		}
	}
}
