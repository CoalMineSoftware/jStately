package com.coalminesoftware.jstately.integration;

import com.coalminesoftware.jstately.collection.Holder;
import com.coalminesoftware.jstately.graph.StateGraph;
import com.coalminesoftware.jstately.graph.StateGraphBuilder;
import com.coalminesoftware.jstately.graph.state.CompositeState;
import com.coalminesoftware.jstately.graph.state.CompositeStateBuilder;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.state.StateBuilder;
import com.coalminesoftware.jstately.graph.transition.Transition;
import com.coalminesoftware.jstately.graph.transition.TransitionBuilder;
import com.coalminesoftware.jstately.machine.StateMachine;
import com.coalminesoftware.jstately.machine.StateMachineBuilder;
import com.coalminesoftware.jstately.test.Event;
import com.coalminesoftware.jstately.test.EventType;
import com.coalminesoftware.jstately.test.TestStateMachineEventListener;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class IntegrationTest {
	private static State<Integer> stateA;
	private static State<Integer> stateB;
	private static State<Integer> stateC;
	private static State<Integer> stateD;

	private static Transition<Integer> transitionAB;
	private static Transition<Integer> transitionBC;
	private static Transition<Integer> transitionCD;

	private static CompositeState<Integer> compositeX;
	private static CompositeState<Integer> compositeX1;
	private static CompositeState<Integer> compositeX2;

	private static Transition<Integer> transitionX1A;
	private static Transition<Integer> transitionXA;

	private static CompositeState<Integer> compositeY;

	private static StateGraph<Integer> graph;

	@BeforeClass
	public static void setUpBeforeClass() {
		stateA = new StateBuilder<Integer>().setDescription("State A").build();

		stateB = new StateBuilder<Integer>().setDescription("State B").build();
		transitionAB = TransitionBuilder.forExpectedInputs(stateB, 1).build();

		stateC = new StateBuilder<Integer>().setDescription("State C").build();
		transitionBC = TransitionBuilder.forExpectedInputs(stateC, 2).build();

		stateD = new StateBuilder<Integer>().setDescription("State D").build();
		transitionCD = TransitionBuilder.forExpectedInputs(stateD, 3).build();

		graph = new StateGraphBuilder<>(stateA)
				.addTransition(stateA, transitionAB)
				.addTransition(stateB, transitionBC)
				.addTransition(stateC, transitionCD)
				.build();

		// First set of (nested) composites
		transitionX1A = TransitionBuilder.forExpectedInputs(stateA, 100).build();
		compositeX1 = new CompositeStateBuilder<Integer>()
				.setDescription("First inner composite")
				.addTransition(transitionX1A)
				.addState(stateB).build();

		compositeX2 = new CompositeStateBuilder<Integer>()
				.setDescription("Second inner composite")
				.addState(stateC).build();

		transitionXA = TransitionBuilder.forExpectedInputs(stateA, 100).build(); // The same expected input as transitionX1A, to ensure that transitionX1A takes priority
		compositeX = new CompositeStateBuilder<Integer>()
				.setDescription("Outer composite")
				.addTransition(transitionXA)
				.addCompositeState(compositeX1)
				.addCompositeState(compositeX2).build();

		// Second composite
		compositeY = new CompositeStateBuilder<Integer>()
				.setDescription("Overlapping outer composite")
				.addState(stateB)
				.addTransition(TransitionBuilder.forExpectedInputs(stateD, 200).build())
				.build();
	}

	@Test
	public void testStateMachineStateTransitioning() {
		StateMachine<Integer,Integer> machine = StateMachineBuilder.forMatchingInputTypes(graph).build();

		TestStateMachineEventListener<Integer> listener =
				new TestStateMachineEventListener<>(EventType.ALL_TYPES_EXCEPT_INPUT_VALIDATION);
		machine.addEventListener(listener);

		machine.start();
		listener.assertEventsOccurred(
				Event.forStateEntry(stateA));

		machine.evaluateInput(0);
		listener.assertEventsOccurred(
				Event.forNoTransitionFound(0));

		machine.evaluateInput(1);
		listener.assertEventsOccurred(
				Event.forStateExit(stateA),
				Event.forTransitionFollowed(transitionAB),
				Event.forCompositeStateEntry(compositeX),
				Event.forCompositeStateEntry(compositeX1),
				Event.forCompositeStateEntry(compositeY),
				Event.forStateEntry(stateB));

		machine.evaluateInput(2);
		listener.assertEventsOccurred(
				Event.forStateExit(stateB),
				Event.forCompositeStateExit(compositeY),
				Event.forCompositeStateExit(compositeX1),
				Event.forTransitionFollowed(transitionBC),
				Event.forCompositeStateEntry(compositeX2),
				Event.forStateEntry(stateC));

		machine.evaluateInput(3);
		listener.assertEventsOccurred(
				Event.forStateExit(stateC),
				Event.forCompositeStateExit(compositeX2),
				Event.forCompositeStateExit(compositeX),
				Event.forTransitionFollowed(transitionCD),
				Event.forStateEntry(stateD));
	}

	@Test
	public void testStateMachineStateTransitionPrecedence() {
		StateMachine<Integer,Integer> machine = StateMachineBuilder.forMatchingInputTypes(graph).build();
		machine.transition(stateB);

		TestStateMachineEventListener<Integer> listener = new TestStateMachineEventListener<>(EventType.ALL_TYPES_EXCEPT_INPUT_VALIDATION);
		machine.addEventListener(listener);

		machine.evaluateInput(100);
		listener.assertEventOccurred(Event.forTransitionFollowed(transitionX1A));

		machine.transition(stateC);
		listener.clearObservedEvents();
		machine.evaluateInput(100);
		listener.assertEventOccurred(Event.forTransitionFollowed(transitionXA));
	}

	@Test
	public void testRecursiveEvaluation() {
		// Defines a graph with a transition that evaluates another input. This tests that the
		// machine is able to queue the subsequent input rather than trying evaluate it
		// immediately.

		State<Integer> stateA = new StateBuilder<Integer>().setDescription("A").build();
		State<Integer> stateB = new StateBuilder<Integer>().setDescription("B").build();
		State<Integer> stateC = new StateBuilder<Integer>().setDescription("C").build();

		Holder<StateMachine<Integer, Integer>> machineHolder = new Holder<>();
		StateGraph<Integer> graph = new StateGraphBuilder<>(stateA)
				.addTransition(stateA, TransitionBuilder.forExpectedInputs(stateB, 1)
						.setTransitionListener(input -> machineHolder.getValue().evaluateInput(2))
						.build())
				.addTransition(stateB, TransitionBuilder.forExpectedInputs(stateC, 2).build())
				.build();
		StateMachine<Integer, Integer> machine = StateMachineBuilder.forMatchingInputTypes(graph).build();
		machineHolder.setValue(machine);

		machine.start();
		machine.evaluateInput(1);

		assertThat(machine.getState()).isEqualTo(stateC);
	}
}
