package com.coalminesoftware.jstately.machine;

import com.coalminesoftware.jstately.graph.StateGraph;
import com.coalminesoftware.jstately.graph.state.DefaultState;
import com.coalminesoftware.jstately.graph.state.DefaultSubmachineState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.state.SubmachineState;
import com.coalminesoftware.jstately.graph.transition.EqualityTransition;
import com.coalminesoftware.jstately.machine.input.InputAdapter;
import com.coalminesoftware.jstately.test.Event;
import com.coalminesoftware.jstately.test.EventType;
import com.coalminesoftware.jstately.test.TestStateMachineEventListener;
import org.junit.Test;

import static com.coalminesoftware.jstately.test.MockingUtils.mockObjectTransition;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class StateMachineTest {
	@Test
	public void testHasStarted() {
		StateMachine<Object,Object> machine = createStateMachineWithMockDependencies();
		assertThat(machine.hasStarted(), is(false));

		machine.overrideState(new DefaultState<>());
		assertThat(machine.hasStarted(), is(true));
	}

	@Test(expected = IllegalStateException.class)
	public void testStart_graphWithoutStartState() {
		StateGraph<Object> graph = mock(StateGraph.class);
		doReturn(null).when(graph).getStartState();

		StateMachine<Object, Object> machine = StateMachine.create(graph);
		machine.start();
	}

	@Test
	public void testEvaluateInputWhileInSubmachineState() {
		// Test scenario where the machine is in a submachine state when evaluateInput()
		// is called, in which case it should delegate the input to the submachine.

		State<Object> innerState = new DefaultState<>();
		StateGraph<Object> innerGraph = new StateGraph<>(innerState);

		State<Object> intermediateState = new DefaultSubmachineState<>(null, innerGraph);
		StateGraph<Object> intermediateGraph = new StateGraph<>(intermediateState);

		State<Object> outerState = new DefaultSubmachineState<>(null, intermediateGraph);
		StateGraph<Object> outerGraph = new StateGraph<>(outerState);

		StateMachine<Object,Object> machine = StateMachine.create(outerGraph);
		machine.start();

		assertThat("Machine could't be initialized as expected.",
				machine.getStates(),
				is(asList(outerState, intermediateState, innerState)));

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

		StateMachine<Object,Object> machine = StateMachine.create(outerGraph);
		TestStateMachineEventListener<Object> listener = new TestStateMachineEventListener<>();
		machine.addEventListener(listener);

		machine.enterState(null, outerGraph.getStartState());

		assertThat("Expected to see each graph's start state, ordered from outer to inner",
				machine.getStates(),
				is(asList(outerGraph.getStartState(), intermediateGraph.getStartState(), innerGraph.getStartState())));

		listener.assertEventsOccurred(
				Event.forStateEntry(outerGraph.getStartState()),
				Event.forStateEntry(intermediateGraph.getStartState()),
				Event.forStateEntry(innerGraph.getStartState()));
	}

	private StateGraph<Object> createGraphWithSingleNonSubmachineState() {
		return new StateGraph<>(new DefaultState<>());
	}

	private StateGraph<Object> createStateGraphWithSubmachineState(StateGraph<Object> nestedStateGraph) {
		DefaultSubmachineState<Object> submachineState = new DefaultSubmachineState<>();
		submachineState.setStateGraph(nestedStateGraph);

		return new StateGraph<>(submachineState);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testEnterStateWithSubmachineStates() {
		// On a state graph with multiple levels of nested graphs, enter the top-level SubmachineState
		// and ensure that the machine initializes to the start state of the nested graphs

		State<Object> innerFirstState = new DefaultState<>("inner/first");
		State<Object> innerSecondState = new DefaultState<>("inner/second");
		StateGraph<Object> innerGraph = new StateGraph<>(innerFirstState)
				.addTransition(innerFirstState, mockObjectTransition(true, innerSecondState));

		State<Object> intermediateFirstState = new DefaultState<>("intermediate/first");
		SubmachineState<Object> intermediateSecondState = new DefaultSubmachineState<>("intermediate/second", innerGraph);
		StateGraph intermediateGraph = new StateGraph<>(intermediateSecondState)
				.addTransition(intermediateFirstState, mockObjectTransition(true, intermediateSecondState));

		State<Object> outerFirstState = new DefaultState<>("outer/first");
		SubmachineState<Object> outerSecondState = new DefaultSubmachineState<>("outer/second", intermediateGraph);
		StateGraph outerGraph = new StateGraph<>(outerSecondState)
				.addTransition(outerFirstState, mockObjectTransition(true, outerSecondState));

		StateMachine<Object,Object> machine = StateMachine.create(outerGraph);
		TestStateMachineEventListener<Object> listener = new TestStateMachineEventListener<>();
		machine.addEventListener(listener);

		machine.enterState(null, outerSecondState, intermediateSecondState, innerSecondState);

		assertThat("Expected the machine's states to match the states provided to enterState().",
				machine.getStates(),
				is(asList(outerSecondState, intermediateSecondState, innerSecondState)));

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

		StateMachine<Object,Object> machine = StateMachine.create(outerGraph);

		machine.start();
		assertThat("State machine could not be initialized for test.",
				machine.getStates(),
				is(asList(outerGraph.getStartState(), intermediateGraph.getStartState(), innerGraph.getStartState())));

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
		State<Integer> stateS = new DefaultState<>("S");
		State<Integer> stateA = new DefaultState<>("A");

		StateGraph<Integer> graph = new StateGraph<>(stateS)
				.addTransition(stateS, new EqualityTransition<>(stateA, null));
		StateMachine<Integer, Integer> machine = StateMachine.create(graph);
		machine.start();

		assertThat("Machine expected to start in its graph's start state",
				machine.getState(),
				is(graph.getStartState()));

		// Test input that should not cause a transition
		machine.evaluateInput(1);
		assertThat("Machine expected to stay in ",
				machine.getState(),
				is(graph.getStartState()));

		// Ensure that null input gets evaluated
		machine.evaluateInput(null);
		assertThat("Machine expected to have transitioned",
				machine.getState(),
				is(stateA));
	}

	private static StateMachine<Object, Object> createStateMachineWithMockDependencies() {
		return new StateMachine<Object,Object>(
				mock(StateGraph.class),
				mock(InputAdapter.class));
	}
}
