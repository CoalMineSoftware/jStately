package com.coalminesoftware.jstately.machine;

import com.coalminesoftware.jstately.graph.StateGraph;
import com.coalminesoftware.jstately.graph.StateGraphBuilder;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.state.StateBuilder;
import com.coalminesoftware.jstately.graph.state.SubmachineState;
import com.coalminesoftware.jstately.graph.state.SubmachineStateBuilder;
import com.coalminesoftware.jstately.graph.transition.TransitionBuilder;
import com.coalminesoftware.jstately.machine.input.InputAdapter;
import com.coalminesoftware.jstately.test.Event;
import com.coalminesoftware.jstately.test.EventType;
import com.coalminesoftware.jstately.test.TestStateMachineEventListener;
import org.junit.Test;

import static com.coalminesoftware.jstately.test.MockingUtils.mockObjectTransition;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Mockito.mock;

public class StateMachineTest {
	@Test
	public void testHasStarted() {
		StateMachine<Object,Object> machine = createStateMachineWithMockDependencies();
		assertThat(machine.hasStarted()).isFalse();

		machine.overrideState(new StateBuilder<>().build());
		assertThat(machine.hasStarted()).isTrue();
	}

	@Test
	public void testEvaluateInputWhileInSubmachineState() {
		// Test scenario where the machine is in a submachine state when evaluateInput()
		// is called, in which case it should delegate the input to the submachine.

		State<Object> innerState = new StateBuilder<>().build();
		StateGraph<Object> innerGraph = new StateGraphBuilder<>(innerState).build();

		State<Object> intermediateState = new SubmachineStateBuilder<>(innerGraph).build();
		StateGraph<Object> intermediateGraph = new StateGraphBuilder<>(intermediateState).build();

		State<Object> outerState = new SubmachineStateBuilder<>(intermediateGraph).build();
		StateGraph<Object> outerGraph = new StateGraphBuilder<>(outerState).build();

		StateMachine<Object,Object> machine = StateMachineBuilder.forMatchingInputTypes(outerGraph).build();
		machine.start();

		assertWithMessage("Machine couldn't be initialized as expected.")
				.that(machine.getStates()).containsExactly(outerState, intermediateState, innerState).inOrder();

		TestStateMachineEventListener<Object> listener = new TestStateMachineEventListener<>(EventType.INPUT_EVALUATED);
		machine.addEventListener(listener);

		Object input = "";

		machine.evaluateInput(input);

		// TODO This assertion verifies that the input was evaluated three times - once per (sub)machine - but doesn't verify that they happened on different machines or the ordering. Improve it.

		listener.assertEventsOccurred(
				Event.forInputEvaluated(input),
				Event.forInputEvaluated(input),
				Event.forInputEvaluated(input));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testEnterStateWithTopLevelSubmachineState() {
		// On a state graph with multiple levels of nested graphs, enter the top-level SubmachineState (without 
		// specifying submachine states) and ensure that submachines were initialized to their graph's start states.

		StateGraph<Object> innerGraph = createGraphWithSingleNonSubmachineState();
		StateGraph<Object> intermediateGraph = createStateGraphWithSubmachineState(innerGraph);
		StateGraph<Object> outerGraph = createStateGraphWithSubmachineState(intermediateGraph);

		StateMachine<Object,Object> machine = StateMachineBuilder.forMatchingInputTypes(outerGraph).build();
		TestStateMachineEventListener<Object> listener = new TestStateMachineEventListener<>();
		machine.addEventListener(listener);

		machine.enterState(null, outerGraph.getStartState());

		assertWithMessage("Expected to see each graph's start state, ordered from outer to inner")
				.that(machine.getStates())
				.containsExactly(outerGraph.getStartState(), intermediateGraph.getStartState(), innerGraph.getStartState())
				.inOrder();

		listener.assertEventsOccurred(
				Event.forStateEntry(outerGraph.getStartState()),
				Event.forStateEntry(intermediateGraph.getStartState()),
				Event.forStateEntry(innerGraph.getStartState()));
	}

	private StateGraph<Object> createGraphWithSingleNonSubmachineState() {
		return new StateGraphBuilder<>(new StateBuilder<>().build()).build();
	}

	private StateGraph<Object> createStateGraphWithSubmachineState(StateGraph<Object> nestedStateGraph) {
		SubmachineState<Object> submachineState = new SubmachineStateBuilder<>(nestedStateGraph).build();

		return new StateGraphBuilder<>(submachineState).build();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testEnterStateWithSubmachineStates() {
		// On a state graph with multiple levels of nested graphs, enter the top-level SubmachineState
		// and ensure that the machine initializes to the start state of the nested graphs

		State<Object> innerFirstState = new StateBuilder<>().setDescription("inner/first").build();
		State<Object> innerSecondState = new StateBuilder<>().setDescription("inner/second").build();
		StateGraph<Object> innerGraph = new StateGraphBuilder<>(innerFirstState)
				.addTransition(innerFirstState, mockObjectTransition(true, innerSecondState))
				.build();

		State<Object> intermediateFirstState = new StateBuilder<>().setDescription("intermediate/first").build();
		SubmachineState<Object> intermediateSecondState = new SubmachineStateBuilder<>(innerGraph).setDescription("intermediate/second").build();
		StateGraph<Object> intermediateGraph = new StateGraphBuilder<>(intermediateSecondState)
				.addTransition(intermediateFirstState, mockObjectTransition(true, intermediateSecondState))
				.build();

		State<Object> outerFirstState = new StateBuilder<>().setDescription("outer/first").build();
		SubmachineState<Object> outerSecondState = new SubmachineStateBuilder<>(intermediateGraph).setDescription("outer/second").build();
		StateGraph<Object> outerGraph = new StateGraphBuilder<>(outerSecondState)
				.addTransition(outerFirstState, mockObjectTransition(true, outerSecondState))
				.build();

		StateMachine<Object,Object> machine = StateMachineBuilder.forMatchingInputTypes(outerGraph).build();
		TestStateMachineEventListener<Object> listener = new TestStateMachineEventListener<>();
		machine.addEventListener(listener);

		machine.enterState(null, outerSecondState, intermediateSecondState, innerSecondState);

		assertWithMessage("Expected the machine's states to match the states provided to enterState().")
				.that(machine.getStates())
				.containsExactly(outerSecondState, intermediateSecondState, innerSecondState)
				.inOrder();

		listener.assertEventsOccurred(
				Event.forStateEntry(outerSecondState),
				Event.forStateEntry(intermediateSecondState),
				Event.forStateEntry(innerSecondState));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testExitCurrentState() {
		StateGraph<Object> innerGraph = createGraphWithSingleNonSubmachineState();
		StateGraph<Object> intermediateGraph = createStateGraphWithSubmachineState(innerGraph);
		StateGraph<Object> outerGraph = createStateGraphWithSubmachineState(intermediateGraph);

		StateMachine<Object,Object> machine = StateMachineBuilder.forMatchingInputTypes(outerGraph).build();

		machine.start();
		assertWithMessage("State machine could not be initialized for test.")
				.that(machine.getStates())
				.containsExactly(outerGraph.getStartState(), intermediateGraph.getStartState(), innerGraph.getStartState())
				.inOrder();

		TestStateMachineEventListener<Object> listener = new TestStateMachineEventListener<>();
		machine.addEventListener(listener);

		machine.exitCurrentState(null);

		listener.assertEventsOccurred(
				Event.forStateExit(innerGraph.getStartState()),
				Event.forStateExit(intermediateGraph.getStartState()),
				Event.forStateExit(outerGraph.getStartState()));
	}

	@Test
	public void testEvaluateInputWithNullInput() {
		State<Integer> stateS = new StateBuilder<Integer>().setDescription("S").build();
		State<Integer> stateA = new StateBuilder<Integer>().setDescription("A").build();

		StateGraph<Integer> graph = new StateGraphBuilder<>(stateS)
				.addTransition(stateS, TransitionBuilder.forExpectedInputs(stateA, null, null).build())
				.build();
		StateMachine<Integer, Integer> machine = StateMachineBuilder.forMatchingInputTypes(graph).build();
		machine.start();

		assertWithMessage("Machine expected to start in its graph's start state")
				.that(machine.getState())
				.isEqualTo(graph.getStartState());

		// Test input that should not cause a transition
		machine.evaluateInput(1);
		assertWithMessage("Machine expected to stay in ")
				.that(machine.getState()).isEqualTo(graph.getStartState());

		// Ensure that null input gets evaluated
		machine.evaluateInput(null);
		assertWithMessage("Machine expected to have transitioned")
				.that(machine.getState())
				.isEqualTo(stateA);
	}

	private static StateMachine<Object, Object> createStateMachineWithMockDependencies() {
		return new StateMachineBuilder<Object,Object>(mock(StateGraph.class), mock(InputAdapter.class)).build();
	}
}
