package com.coalminesoftware.jstately.integration;

import com.coalminesoftware.jstately.graph.StateGraph;
import com.coalminesoftware.jstately.graph.state.DefaultState;
import com.coalminesoftware.jstately.graph.state.DefaultSubmachineState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.machine.StateMachine;
import com.coalminesoftware.jstately.test.Event;
import com.coalminesoftware.jstately.test.EventType;
import com.coalminesoftware.jstately.test.TestStateMachineEventListener;
import org.junit.Test;

import static com.coalminesoftware.jstately.test.MockingUtils.mockObjectTransition;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

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
			firstInnerStateGraphStartState = new DefaultState<>("First inner start");
			firstInnerStateGraphSecondState = new DefaultState<>("First inner second");
			firstInnerStateGraph = new StateGraph<>(firstInnerStateGraphStartState)
					.addTransition(firstInnerStateGraphStartState, mockObjectTransition(true, firstInnerStateGraphSecondState));

			secondInnerStateGraphStartState = new DefaultState<>("Second inner start");
			secondInnerStateGraphSecondState = new DefaultState<>("Second inner second");
			secondInnerStateGraph = new StateGraph<>(secondInnerStateGraphStartState)
					.addTransition(secondInnerStateGraphStartState, mockObjectTransition(true, secondInnerStateGraphSecondState));

			outerStateGraphStartState = new DefaultSubmachineState<>("Outer start", firstInnerStateGraph);
			outerStateGraphSecondState = new DefaultSubmachineState<>("Outer second", secondInnerStateGraph);
			outerStateGraph = new StateGraph<>(outerStateGraphStartState)
					.addTransition(outerStateGraphStartState, mockObjectTransition(true, outerStateGraphSecondState));

			machine = StateMachine.newStateMachine(outerStateGraph);

			machine.start();
			assertThat("Couldn't initialize test.",
					machine.getStates(),
					is(asList(outerStateGraphStartState, firstInnerStateGraphStartState)));

			listener = new TestStateMachineEventListener<>(EventType.STATE_ENTERED, EventType.STATE_EXITED);
			machine.addEventListener(listener);
		}
	}
}
