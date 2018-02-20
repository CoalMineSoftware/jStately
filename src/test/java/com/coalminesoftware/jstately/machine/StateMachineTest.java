package com.coalminesoftware.jstately.machine;

import com.coalminesoftware.jstately.graph.StateGraph;
import com.coalminesoftware.jstately.graph.state.DefaultState;
import com.coalminesoftware.jstately.graph.state.DefaultSubmachineState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.EqualityTransition;
import com.coalminesoftware.jstately.test.*;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class StateMachineTest {
	@Test(expected=IllegalStateException.class)
	public void testStartWhenRunning() {
		StateMachine<Object,Object> machine = new StateMachine<>();
		machine.overrideState(new DefaultState<>());

		machine.start();
	}

	@Test(expected=IllegalStateException.class)
	public void testStartWithoutGraph() {
		StateMachine<Object,Object> machine = new StateMachine<>();

		machine.start();
	}

	@Test(expected=IllegalStateException.class)
	public void testStartWithoutGraphStartState() {
		StateMachine<Object,Object> machine = new StateMachine<>();
		machine.setStateGraph(new StateGraph<>());

		machine.start();
	}

	@Test
	public void testHasStarted() {
		StateMachine<Object,Object> machine = new StateMachine<>();
		assertFalse(machine.hasStarted());

		machine.overrideState(new DefaultState<>());
		assertTrue(machine.hasStarted());
	}

	@Test(expected=IllegalStateException.class)
	public void testEvaluateInputWithoutInputAdapter() throws InterruptedException {
		StateMachine<Object,Object> machine = new StateMachine<>();
		machine.evaluateInput(null);
	}

	@Test
	public void testEvaluateInputWhileInSubmachineState() throws InterruptedException {
		// Test scenario where the machine is in a submachine state when evaluateInput()
		// is called, in which case it should delegate the input to the submachine.

		State<Object> innerState = new DefaultState<>();
		StateGraph<Object> innerGraph = new StateGraph<>();
		innerGraph.setStartState(innerState);

		State<Object> intermediateState = new DefaultSubmachineState<>(null, innerGraph);
		StateGraph<Object> intermediateGraph = new StateGraph<>();
		intermediateGraph.setStartState(intermediateState);

		State<Object> outerState = new DefaultSubmachineState<>(null, intermediateGraph);
		StateGraph<Object> outerGraph = new StateGraph<>();
		outerGraph.setStartState(outerState);

		StateMachine<Object,Object> machine = new StateMachine<>(outerGraph, new DefaultInputAdapter<>());
		machine.start();

		assertEquals("Machine could't be initialized as expected.",
				Arrays.asList(outerState, intermediateState, innerState),
				machine.getStates());

		TestStateMachineEventListener<Object> listener = new TestStateMachineEventListener<>(EventType.INPUT_EVALUATED);
		machine.addEventListener(listener);

		Object input = "";

		machine.evaluateInput(input);

		// TODO This assertion verifies that the input was evaluated three times - once per (sub)machine - but doesn't verify that they happened on different machines or the ordering.  Improve it.

		listener.assertEventsOccurred(
				Event.forInputEvaluated(input),
				Event.forInputEvaluated(input),
				Event.forInputEvaluated(input));
	}

	@Test(expected=IllegalStateException.class)
	public void testFindFirstValidTransitionFromCurrentStatePriorToStarting() {
		StateMachine<Object,Object> machine = new StateMachine<>();

		machine.findFirstValidTransitionFromCurrentState(null);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testEnterStateWithTopLevelSubmachineState() {
		// On a state graph with multiple levels of nested graphs, enter the top-level SubmachineState (without 
		// specifying submachine states) and ensure that submachines were initialized to their graph's start states.

		StateGraph<Object> innerGraph = createGraphWithSingleNonSubmachineState();
		StateGraph<Object> intermediateGraph = createStateGraphWithSubmachineState(innerGraph);
		StateGraph<Object> outerGraph = createStateGraphWithSubmachineState(intermediateGraph);

		StateMachine<Object,Object> machine = new StateMachine<>(outerGraph, new DefaultInputAdapter<>());
		TestStateMachineEventListener<Object> listener = new TestStateMachineEventListener<>();
		machine.addEventListener(listener);

		machine.enterState(null, outerGraph.getStartState());

		assertEquals("Expected to see each graph's start state, ordered from outer to inner",
				Arrays.asList(outerGraph.getStartState(), intermediateGraph.getStartState(), innerGraph.getStartState()),
				machine.getStates());

		listener.assertEventsOccurred(
				Event.forStateEntry(outerGraph.getStartState()),
				Event.forStateEntry(intermediateGraph.getStartState()),
				Event.forStateEntry(innerGraph.getStartState()));
	}

	private StateGraph<Object> createGraphWithSingleNonSubmachineState() {
		StateGraph<Object> graph = new StateGraph<>();
		graph.setStartState(new DefaultState<>());

		return graph;
	}

	private StateGraph<Object> createStateGraphWithSubmachineState(StateGraph<Object> nestedStateGraph) {
		DefaultSubmachineState<Object> submachineState = new DefaultSubmachineState<>();
		submachineState.setStateGraph(nestedStateGraph);

		StateGraph<Object> graph = new StateGraph<>();
		graph.setStartState(submachineState);

		return graph;
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testEnterStateWithSubmachineStates() {
		// On a state graph with multiple levels of nested graphs, enter the top-level SubmachineState
		// and ensure that the machine initializes to the start state of the nested graphs

		TwoStateStateGraph innerGraph = new TwoStateStateGraph("inner");
		TwoStateStateGraphWithSubmachineState intermediateGraph = new TwoStateStateGraphWithSubmachineState(innerGraph, "intermediate");
		TwoStateStateGraphWithSubmachineState outerGraph = new TwoStateStateGraphWithSubmachineState(intermediateGraph, "outer");

		StateMachine<Object,Object> machine = new StateMachine<>(outerGraph, new DefaultInputAdapter<>());
		TestStateMachineEventListener<Object> listener = new TestStateMachineEventListener<>();
		machine.addEventListener(listener);

		machine.enterState(null, outerGraph.getSecondState(), intermediateGraph.getSecondState(), innerGraph.getSecondState());

		assertEquals("Expected the machine's states to match the states provided to enterState().",
				Arrays.asList(outerGraph.getSecondState(), intermediateGraph.getSecondState(), innerGraph.getSecondState()),
				machine.getStates());

		listener.assertEventsOccurred(
				Event.forStateEntry(outerGraph.getSecondState()),
				Event.forStateEntry(intermediateGraph.getSecondState()),
				Event.forStateEntry(innerGraph.getSecondState()));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testExitCurrentState() {
		StateGraph<Object> innerGraph = createGraphWithSingleNonSubmachineState();
		StateGraph<Object> intermediateGraph = createStateGraphWithSubmachineState(innerGraph);
		StateGraph<Object> outerGraph = createStateGraphWithSubmachineState(intermediateGraph);

		StateMachine<Object,Object> machine = new StateMachine<>(outerGraph, new DefaultInputAdapter<>());

		machine.start();
		assertEquals("State machine could not be initialized for test.",
				Arrays.asList(outerGraph.getStartState(), intermediateGraph.getStartState(), innerGraph.getStartState()),
				machine.getStates());

		TestStateMachineEventListener<Object> listener = new TestStateMachineEventListener<>();
		machine.addEventListener(listener);

		machine.exitCurrentState(null);

		listener.assertEventsOccurred(
				Event.forStateExit(innerGraph.getStartState()),
				Event.forStateExit(intermediateGraph.getStartState()),
				Event.forStateExit(outerGraph.getStartState()));
	}

	@Test
	public void testEvaluateInputWithNullInput() throws InterruptedException {
		StateGraph<Integer> graph = new StateGraph<>();

		State<Integer> stateS = new DefaultState<>("S");
		graph.setStartState(stateS);

		State<Integer> stateA = new DefaultState<>("A");
		graph.addTransition(stateS, new EqualityTransition<>(stateA, null));

		StateMachine<Integer, Integer> machine = new StateMachine<>(graph, new DefaultInputAdapter<Integer>());

		machine.start();
		assertEquals("Machine expected to start in its graph's start state",
				graph.getStartState(),
				machine.getState());

		// Test input that should not cause a transition
		machine.evaluateInput(1);
		assertEquals("Machine expected to stay in ",
				graph.getStartState(),
				machine.getState());

		// Ensure that null input gets evaluated
		machine.evaluateInput(null);
		assertEquals("Machine expected to have transitioned",
				stateA,
				machine.getState());
	}
}
