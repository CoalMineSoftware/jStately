package com.coalminesoftware.jstately.machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.coalminesoftware.jstately.collection.CollectionUtil;
import com.coalminesoftware.jstately.graph.StateGraph;
import com.coalminesoftware.jstately.graph.composite.CompositeState;
import com.coalminesoftware.jstately.graph.state.FinalState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.state.SubmachineState;
import com.coalminesoftware.jstately.graph.transition.Transition;
import com.coalminesoftware.jstately.machine.listener.StateMachineEventListener;

import static com.coalminesoftware.jstately.validation.Validation.assertFieldNotNull;
import static com.coalminesoftware.jstately.validation.Validation.assertParameterNotNull;

/** Representation of a basic state machine. */
public class StateMachine<MachineInput,TransitionInput> {
	protected StateGraph<TransitionInput> stateGraph;
	protected State<TransitionInput> currentState;
	protected InputAdapter<MachineInput,TransitionInput> inputAdapter;
	protected List<StateMachineEventListener<TransitionInput>> eventListeners = new ArrayList<>();

	protected StateMachine<TransitionInput,TransitionInput> submachine;

	public StateMachine(StateGraph<TransitionInput> stateGraph, InputAdapter<MachineInput,TransitionInput> inputAdapter) {
		assertParameterNotNull("A state graph is required", stateGraph);
		assertParameterNotNull("An input adapter is required", inputAdapter);

		this.stateGraph = stateGraph;
		this.inputAdapter = inputAdapter;
	}

	/**
	 * Initialize the machine to its start state, calling its {@link State#onEnter()} method.
	 * 
	 * @throws IllegalStateException thrown if no start state was specified or if the machine has already been started.
	 */
	@SuppressWarnings("unchecked")
	public synchronized void start() {
		if(hasStarted()) {
			throw new IllegalStateException("Machine has already started.");
		}

		assertFieldNotNull("No start state set prior to starting.", stateGraph.getStartState());

		stateGraph.onStart();

		enterState(null, stateGraph.getStartState());
	}

	/** @return Whether the machine has a current state. */
	public synchronized boolean hasStarted() {
		return currentState != null;
	}

	/**
	 * Provides the input to the machine's {@link InputAdapter} and evaluates the resulting transition input(s).
	 * For each transition input, the machine follows the first transition that is valid according to its
	 * {@link Transition#isValid(Object)} implementation.
	 * <p>
	 * Note that recursive evaluation is not supported. If, while evaluating an input, transitioning causes another
	 * input to be evaluated immediately, the behavior is undefined.
	 * 
	 * @param machineInput Machine input from which Transition inputs are generated to evaluate.
	 * @return Whether any of the machine input's subsequent transition inputs were ignored (i.e.,
	 * no valid transition was found) while evaluating.
	 * @throws IllegalStateException Thrown if no {@link InputAdapter} has been set.
	 */
	public synchronized boolean evaluateInput(MachineInput machineInput) {
		boolean inputIgnored = false;
		inputAdapter.queueInput(machineInput);
		while(inputAdapter.hasNext()) {
			TransitionInput transitionInput = inputAdapter.next();

			for(StateMachineEventListener<TransitionInput> listener : eventListeners) {
				listener.beforeEvaluatingInput(transitionInput, this);
			}

			boolean evaluateInput = true;
			if(currentState instanceof SubmachineState) {
				// Delegate the evaluation of the input
				submachine.evaluateInput(transitionInput);

				// If the submachine transitioned to (or was left in) a final state, extract its result value to evaluate on this machine
				if(submachine.getState() instanceof FinalState) {
					FinalState<TransitionInput> finalState = (FinalState<TransitionInput>)submachine.getState();
					transitionInput = finalState.getResult();
				} else {
					evaluateInput = false;
				}
			}

			if(evaluateInput) {
				Transition<TransitionInput> validTransition = findFirstValidTransitionFromCurrentState(transitionInput);
				if(validTransition == null) {
					inputIgnored = true;

					for(StateMachineEventListener<TransitionInput> listener : eventListeners) {
						listener.noValidTransition(transitionInput, this);
					}
				} else {
					transition(validTransition,transitionInput);
				}
			}

			for(StateMachineEventListener<TransitionInput> listener : eventListeners) {
				listener.afterEvaluatingInput(transitionInput, this);
			}
		}

		return inputIgnored;
	}

	public synchronized Transition<TransitionInput> findFirstValidTransitionFromCurrentState(TransitionInput input) {
		if(!hasStarted()) {
			throw new IllegalStateException("Machine has not started.");
		}
		return stateGraph.findFirstValidTransitionFromState(currentState, input);
	}

	/**
	 * Follows the given transition without checking its validity.  In the process, it calls {@link State#onExit()} on
	 * the current state, followed by {@link Transition#onTransition(Object)} on the given transition, followed by
	 * {@link State#onEnter()} on the machine's updated current state.
	 * 
	 * @param transition Transition to follow.
	 * @param input The input that caused the transition to occur
	 * @throws IllegalArgumentException thrown if the StateMachine has not started
	 */
	@SuppressWarnings("unchecked")
	protected void transition(Transition<TransitionInput> transition, TransitionInput input) {
		if(!hasStarted()) {
			throw new IllegalStateException("Machine has not started.");
		}

		State<TransitionInput> previousState = exitCurrentState(transition.getHead());

		for(StateMachineEventListener<TransitionInput> listener : eventListeners) {
			listener.beforeTransition(transition, input, this);
		}
		transition.onTransition(input);
		for(StateMachineEventListener<TransitionInput> listener : eventListeners) {
			listener.afterTransition(transition, input, this);
		}

		enterState(previousState, transition.getHead());
	}

	/**
	 * Exits the machine's current state and enters the given state.  Explicitly setting the machine's state should
	 * generally be avoided in favor of evaluating inputs.  However, this method is useful if, for example, your state
	 * machine corresponds to some external system/process that has changed and your application needs to get back in
	 * sync with it.
	 */
	public synchronized void transition(State<TransitionInput> newState, State<TransitionInput>... submachineStates) {
		assertParameterNotNull("New state cannot be null.", newState);

		State<TransitionInput> previousState = exitCurrentState(newState, submachineStates);
		enterState(previousState, newState, submachineStates);
	}

	/** Enters the given state, using previousState to determine what CompositeStates, if any, are being entered. */
	protected void enterState(State<TransitionInput> previousState, State<TransitionInput> newState, State<TransitionInput>... submachineStates) {
		// To accommodate submachines and avoid unnecessary callbacks when transitioning states within a submachine,
		// exitCurrentState() only exits currentState (and sets it to null) on this machine if the destination state/
		// path does not include this state, or the destination is this specific state without a nested state.
		// Similarly, this method only (re-)enters the given state if currentState was set to null by exitCurrentState().

		if(currentState == null || !currentState.equals(newState)) {
			for(CompositeState<TransitionInput> composite : determineCompositesBeingEntered(previousState, newState)) {
				enterCompositeState(composite);
			}

			for(StateMachineEventListener<TransitionInput> listener : eventListeners) {
				listener.beforeStateEntered(newState, this);
			}

			newState.onEnter();
			currentState = newState;
			newState.afterStateEntered();

			for(StateMachineEventListener<TransitionInput> listener : eventListeners) {
				listener.afterStateEntered(newState, this);
			}
		}

		if(newState instanceof SubmachineState) {
			SubmachineState<TransitionInput> submachineState = (SubmachineState<TransitionInput>)newState;
			initializeSubmachine(submachineState, submachineStates);
		}
	}

	private void initializeSubmachine(SubmachineState<TransitionInput> submachineState, State<TransitionInput>[] submachineStates) {
		submachine = new StateMachine<>(submachineState.getStateGraph(), new DefaultInputAdapter<TransitionInput>());
		submachine.eventListeners = eventListeners;

		if(submachineStates.length > 0) {
			submachine.enterState(null, getFirstState(submachineStates), getRemainingStates(submachineStates));
		} else { // No states to initialize nested state machine(s) to
			submachine.start();
		}
	}

	/** Determines which composite states are being entered when entering a state.
	 * @return A list of CompositeStates being entered in order they are being entered (from the root CompositeState to nested ones.) */
	private List<CompositeState<TransitionInput>> determineCompositesBeingEntered(State<TransitionInput> oldState, State<TransitionInput> newState) {
		List<CompositeState<TransitionInput>> newStateComposites = collectCompositeStates(newState);
		if(oldState == null) {
			return newStateComposites;
		}

		List<CompositeState<TransitionInput>> oldStateComposites = collectCompositeStates(oldState);
		newStateComposites.removeAll(oldStateComposites);
		return newStateComposites;
	}

	/** Determines which composite states are being exited when exiting a state.
	 * @return A list of CompositeStates being exited in order they are being exist (from the State's immediate CompositeState to its root CompositeState.) */
	private List<CompositeState<TransitionInput>> determineCompositeStatesBeingExited(State<TransitionInput> oldState, State<TransitionInput> newState) {
		List<CompositeState<TransitionInput>> oldStateComposites = collectCompositeStates(oldState);
		if(newState == null) {
			return CollectionUtil.reverse(oldStateComposites);
		}

		List<CompositeState<TransitionInput>> newStateComposites = collectCompositeStates(newState);
		oldStateComposites.removeAll(newStateComposites);
		return CollectionUtil.reverse(oldStateComposites);
	}

	/** @return All of the CompositeStates that enclose the given State.  The values are returned in the order returned
	 * by {@link State#getComposites()}, with nested composites ordered from the State's outer-most composite to its
	 * immediate parent composite. */
	private List<CompositeState<TransitionInput>> collectCompositeStates(State<TransitionInput> state) {
		List<CompositeState<TransitionInput>> composites = new ArrayList<>();

		for(CompositeState<TransitionInput> composite : state.getComposites()) {
			int insertionPosition = composites.size();
			while(composite != null) {
				composites.add(insertionPosition,composite);
				composite = composite.getParent();
			}
		}

		return composites;
	}

	private void enterCompositeState(CompositeState<TransitionInput> composite) {
		for(StateMachineEventListener<TransitionInput> eventListener : eventListeners) {
			eventListener.beforeCompositeStateEntered(composite, this);
		}

		composite.onEnter();

		for(StateMachineEventListener<TransitionInput> eventListener : eventListeners) {
			eventListener.afterCompositeStateEntered(composite, this);
		}
	}

	private void exitCompositeState(CompositeState<TransitionInput> composite) {
		for(StateMachineEventListener<TransitionInput> eventListener : eventListeners) {
			eventListener.beforeCompositeStateExited(composite, this);
		}

		composite.onExit();

		for(StateMachineEventListener<TransitionInput> eventListener : eventListeners) {
			eventListener.afterCompositeStateExited(composite, this);
		}
	}

	/** Transitions from currentState, using newState to determine which CompositeState's (if any) are being left.
	 * If the current state is a SubmachineState, its current state is exited before the SubmachineState is. */
	protected State<TransitionInput> exitCurrentState(State<TransitionInput> newState, State<TransitionInput>... submachineStates) {
		if(currentState == null) {
			return null;
		}
		State<TransitionInput> previousState = currentState;

		if(submachine != null) {
			submachine.exitCurrentState(getFirstState(submachineStates), getRemainingStates(submachineStates));
			submachine = null;
		}

		if(!currentState.equals(newState) || submachineStates.length == 0) {
			for(StateMachineEventListener<TransitionInput> listener : eventListeners) {
				listener.beforeStateExited(currentState, this);
			}
	
			currentState.onExit();

			for(StateMachineEventListener<TransitionInput> listener : eventListeners) {
				listener.afterStateExited(currentState, this);
			}
	
			for(CompositeState<TransitionInput> composite : determineCompositeStatesBeingExited(currentState,newState)) {
				exitCompositeState(composite);
			}

			currentState = null;
		}

		return previousState;
	}

	private State<TransitionInput> getFirstState(State<TransitionInput>[] states) {
		return states.length == 0?
				null :
				states[0];
	}

	private State<TransitionInput>[] getRemainingStates(State<TransitionInput>[] states) {
		return states.length == 0?
				states :
				Arrays.copyOfRange(states, 1, states.length);
	}

	public StateGraph<TransitionInput> getStateGraph() {
		return stateGraph;
	}

	public InputAdapter<MachineInput,TransitionInput> getInputAdapter() {
		return inputAdapter;
	}

	/**
	 * Returns the state of the machine, including the state of any submachines that may be running.  The first
	 * {@link State} in the list is that of the machine on which getStates() is being called, followed by the state of
	 * nested machines.
	 * <p>
	 * For graphs not using {@link SubmachineState}s, it may be more convenient to use {@link #getState()}.
	 */
	public synchronized List<State<TransitionInput>> getStates() {
		List<State<TransitionInput>> states = new ArrayList<>();
		return appendCurrentState(states);
	}

	/** Recursively adds the current state of the machine and any submachines to the given list. */
	private List<State<TransitionInput>> appendCurrentState(List<State<TransitionInput>> states) {
		states.add(currentState);

		return submachine == null?
				states :
				submachine.appendCurrentState(states);
	}

	/** Gets only the State of the machine, without the state of any SubmachineStates that may be running.
	 * @see #getStates() */
	public synchronized State<TransitionInput> getState() {
		return currentState;
	}

	/** Simply sets the machine's state, without calling event methods like {@link State#onEnter()}
	 * or {@link State#onExit()}.  It also does not setup the nested state machine if newState is a
	 * SubmachineState. This method was added for testing purposes and should be avoided by API users,
	 * who will likely find {@link #transition(State, State...)} much more useful. */
	protected void overrideState(State<TransitionInput> newState) {
		currentState = newState;
	}

	public void addEventListener(StateMachineEventListener<TransitionInput> eventListener) {
		eventListeners.add(eventListener);
	}

	public void removeEventListener(StateMachineEventListener<TransitionInput> eventListener) {
		eventListeners.remove(eventListener);
	}
}
