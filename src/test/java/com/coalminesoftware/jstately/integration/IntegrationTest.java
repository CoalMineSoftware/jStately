package com.coalminesoftware.jstately.integration;

import org.junit.BeforeClass;
import org.junit.Test;

import com.coalminesoftware.jstately.graph.StateGraph;
import com.coalminesoftware.jstately.graph.composite.CompositeState;
import com.coalminesoftware.jstately.graph.state.DefaultState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.EqualityTransition;
import com.coalminesoftware.jstately.graph.transition.Transition;
import com.coalminesoftware.jstately.machine.DefaultInputAdapter;
import com.coalminesoftware.jstately.machine.StateMachine;
import com.coalminesoftware.jstately.test.Event;
import com.coalminesoftware.jstately.test.EventType;
import com.coalminesoftware.jstately.test.TestStateMachineEventListener;

import static org.junit.Assert.assertEquals;

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
		graph = new StateGraph<>();

		stateA = new DefaultState<>("State A");
		graph.setStartState(stateA);

		stateB = new DefaultState<>("State B");
		transitionAB = new EqualityTransition<>(stateB, 1);
		graph.addTransition(stateA, transitionAB);

		stateC = new DefaultState<>("State C");
		transitionBC = new EqualityTransition<>(stateC, 2);
		graph.addTransition(stateB, transitionBC);

		stateD = new DefaultState<>("State D");
		transitionCD = new EqualityTransition<>(stateD, 3);
		graph.addTransition(stateC, transitionCD);

		// First set of (nested) composites
		compositeX1 = new CompositeState<>("First inner composite");
		compositeX1.addState(stateB);
		transitionX1A = new EqualityTransition<>(stateA, 100);
		compositeX1.addTransition(transitionX1A);

		compositeX2 = new CompositeState<>("Second inner composite");
		compositeX2.addState(stateC);

		compositeX = new CompositeState<>("Outer composite");
		compositeX.addComposite(compositeX1);
		compositeX.addComposite(compositeX2);
		transitionXA = new EqualityTransition<>(stateA, 100); // The same expected input as transitionX1A, to ensure that transitionX1A takes priority
		compositeX.addTransition(transitionXA);

		// Second composite
		compositeY = new CompositeState<>("Overlapping outer composite");
		compositeY.addState(stateB);
		compositeY.addTransition(new EqualityTransition<>(stateD, 200));
	}

	@Test
	public void testStateMachineStateTransitioning() throws InterruptedException {
		StateMachine<Integer,Integer> machine = new StateMachine<>(graph, new DefaultInputAdapter<Integer>());

		TestStateMachineEventListener<Integer> listener = new TestStateMachineEventListener<>(EventType.ALL_TYPES_EXCEPT_INPUT_VALIDATION);
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
	public void testStateMachineStateTransitionPrecedence() throws InterruptedException {
		StateMachine<Integer,Integer> machine = new StateMachine<>(graph, new DefaultInputAdapter<Integer>());
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
	public void testRecursiveEvaluation() throws InterruptedException {
		// Defines a graph with a transition that evaluates another input. This tests that the
		// machine is able to queue the subsequent input rather than trying evaluate it
		// immediately.

		StateGraph<Integer> graph = new StateGraph<>();
		final StateMachine<Integer, Integer> machine = new StateMachine<>(
				graph, new DefaultInputAdapter<Integer>());

		State<Integer> stateA = new DefaultState<>("A");
		graph.setStartState(stateA);

		State<Integer> stateB = new DefaultState<>("B");
		graph.addTransition(stateA, new EqualityTransition<Integer>(stateB, 1) {
			@Override
			public void onTransition(Integer integer) {
				try {
					machine.evaluateInput(2);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});

		State<Integer> stateC = new DefaultState<>("C");
		graph.addTransition(stateB, new EqualityTransition<>(stateC, 2));

		machine.start();
		machine.evaluateInput(1);

		assertEquals(stateC, machine.getState());
	}
}
