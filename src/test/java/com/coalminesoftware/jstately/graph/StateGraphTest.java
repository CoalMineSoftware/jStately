package com.coalminesoftware.jstately.graph;

import com.coalminesoftware.jstately.graph.composite.CompositeState;
import com.coalminesoftware.jstately.graph.state.DefaultFinalState;
import com.coalminesoftware.jstately.graph.state.DefaultState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.Transition;
import com.coalminesoftware.jstately.test.MockingUtils;
import org.junit.Test;

import static com.coalminesoftware.jstately.test.MockingUtils.mockObjectTransition;
import static com.coalminesoftware.jstately.test.MockingUtils.mockState;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class StateGraphTest {
	@Test(expected = IllegalArgumentException.class)
	public void testConstructionWithFinalState() {
		new StateGraph<>(new DefaultFinalState<>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddTransitionWithTransitionWithNullTail() {
		new StateGraph<>(mockState())
				.addTransition(null, MockingUtils.mockObjectTransition(true));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testAddSelfTransitionWithSelfTransitionWithNullHead() {
		new StateGraph<>(mockState())
				.addSelfTransition(mockObjectTransition(true, null));
	}

	@Test
	public void testFindFirstValidTransitionFromState_onlyGlobalValidTransition() {
		State<Object> state = new DefaultState<>();

		// Wrap the state in nested composites
		CompositeState<Object> firstComposite = new CompositeState<>();
		firstComposite.addState(state);

		// Two more nested composites that overlap with the first two (share the state) but aren't nested
		CompositeState<Object> secondInnerComposite = new CompositeState<>();
		secondInnerComposite.addState(state);
		CompositeState<Object> secondOuterComposite = new CompositeState<>();
		secondOuterComposite.addComposite(secondInnerComposite);

		// Only an invalid global transition
		StateGraph<Object> graph = new StateGraph<>(mockState())
				.addGlobalTransition(mockObjectTransition(false));
		assertThat("No valid transition should exist.",
				graph.findFirstValidTransitionFromState(state, null),
				nullValue());

		// With a valid global transition
		Transition<Object> validGlobalTransition = mockObjectTransition(true);
		graph.addGlobalTransition(validGlobalTransition);
		assertThat("The valid global transition should be returned.",
				graph.findFirstValidTransitionFromState(state, null),
				is(validGlobalTransition));

		// Matches in a state's composite ancestors should take precedence over global transitions... but still need to be valid.
		secondOuterComposite.addTransition(mockObjectTransition(false));
		assertThat("The valid global transition should be returned.",
				graph.findFirstValidTransitionFromState(state, null),
				is(validGlobalTransition));

		Transition<Object> validSecondOuterCompositeTransition = mockObjectTransition(true);
		secondOuterComposite.addTransition(validSecondOuterCompositeTransition);
		assertThat("Composite transitions should take precedence over global transitions.",
				graph.findFirstValidTransitionFromState(state, null),
				is(validSecondOuterCompositeTransition));

		// More immediate CompositeState ancestors should take precedence over more distant CompositeState ancestors
		secondInnerComposite.addTransition(mockObjectTransition(false));
		assertThat("", // ...still
				graph.findFirstValidTransitionFromState(state, null),
				is(validSecondOuterCompositeTransition));

		Transition<Object> validSecondInnerCompositeTransition = mockObjectTransition(true);
		secondInnerComposite.addTransition(validSecondInnerCompositeTransition);
		assertThat("More immediate CompositeState ancestors should take precedence over more distant CompositeState ancestors",
				graph.findFirstValidTransitionFromState(state, null),
				is(validSecondInnerCompositeTransition));

		// When a state has multiple immediate CompositeStates, precedence should be given to Transitions from the CompositeState that the state was added to earliest.
		firstComposite.addTransition(mockObjectTransition(false));
		assertThat("", // ...still
				graph.findFirstValidTransitionFromState(state, null),
				is(validSecondInnerCompositeTransition));

		Transition<Object> validFirstCompositeTransition = mockObjectTransition(true);
		firstComposite.addTransition(validFirstCompositeTransition);
		assertThat("When a state has multiple immediate CompositeStates, precedence should be given to Transitions from the CompositeState that the state was added to earliest",
				graph.findFirstValidTransitionFromState(state, null),
				is(validFirstCompositeTransition));

		// Now check the transitions on the state itself
		graph.addTransition(state, mockObjectTransition(false));
		assertThat("", // ...still
				graph.findFirstValidTransitionFromState(state, null),
				is(validFirstCompositeTransition));

		Transition<Object> validStateTransition = mockObjectTransition(true);
		graph.addTransition(state, validStateTransition);
		assertThat("Valid transitions on the state itself should take precedence over global transitions and transitions on ancestor CompositeStates.",
				graph.findFirstValidTransitionFromState(state, null),
				is(validStateTransition));
	}
}
