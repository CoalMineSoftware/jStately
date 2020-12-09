package com.coalminesoftware.jstately.graph;

import com.coalminesoftware.jstately.graph.state.FinalStateBuilder;
import com.coalminesoftware.jstately.test.MockingUtils;
import org.junit.Test;

import static com.coalminesoftware.jstately.test.MockingUtils.mockObjectTransition;
import static com.coalminesoftware.jstately.test.MockingUtils.mockState;

public class StateGraphTest {
	@SuppressWarnings("ConstantConditions")
	@Test(expected = NullPointerException.class)
	public void testConstructionWithFinalState() {
		new StateGraphBuilder<>(new FinalStateBuilder<>(null).build())
				.build();
	}

	@Test(expected = NullPointerException.class)
	public void testAddTransitionWithTransitionWithNullTail() {
		new StateGraphBuilder<>(mockState())
				.addTransition(null, MockingUtils.mockObjectTransition(true))
				.build();
	}

	@Test(expected=NullPointerException.class)
	public void testAddSelfTransitionWithSelfTransitionWithNullHead() {
		new StateGraphBuilder<>(mockState())
				.addSelfTransition(mockObjectTransition(true, null))
				.build();
	}

//	@Test
//	public void testFindFirstValidTransitionFromState_onlyGlobalValidTransition() {
//		State<Object> state = new State.Builder<>().build();
//
//		// Wrap the state in nested composites
//		CompositeState<Object> firstComposite = new CompositeState.Builder<>()
//				.addState(state).build();
//
//		// Two more nested composites that overlap with the first two (share the state) but aren't nested
//		CompositeState<Object> secondInnerComposite = new CompositeState.Builder<>()
//				.addState(state).build();
//		CompositeState<Object> secondOuterComposite = new CompositeState.Builder<>()
//				.addCompositeState(secondInnerComposite).build();
//
//		// Only an invalid global transition
//		StateGraph<Object> graph = new StateGraph<>(mockState())
//				.addGlobalTransition(mockObjectTransition(false));
//		assertWithMessage("No valid transition should exist.")
//				.that(graph.findFirstValidTransitionFromState(state, null))
//				.isNull();
//
//		// With a valid global transition
//		Transition<Object> validGlobalTransition = mockObjectTransition(true);
//		graph.addGlobalTransition(validGlobalTransition);
//		assertWithMessage("The valid global transition should be returned.")
//				.that(graph.findFirstValidTransitionFromState(state, null))
//				.isEqualTo(validGlobalTransition);
//
//		// Matches in a state's composite ancestors should take precedence over global transitions... but still need to be valid.
//		secondOuterComposite.addTransition(mockObjectTransition(false));
//		assertWithMessage("The valid global transition should be returned.")
//				.that(graph.findFirstValidTransitionFromState(state, null))
//				.isEqualTo(validGlobalTransition);
//
//		Transition<Object> validSecondOuterCompositeTransition = mockObjectTransition(true);
//		secondOuterComposite.addTransition(validSecondOuterCompositeTransition);
//		assertWithMessage("Composite transitions should take precedence over global transitions.")
//				.that(graph.findFirstValidTransitionFromState(state, null))
//				.isEqualTo(validSecondOuterCompositeTransition);
//
//		// More immediate CompositeState ancestors should take precedence over more distant CompositeState ancestors
//		secondInnerComposite.addTransition(mockObjectTransition(false));
//		assertThat(graph.findFirstValidTransitionFromState(state, null)).isEqualTo(validSecondOuterCompositeTransition);
//
//		Transition<Object> validSecondInnerCompositeTransition = mockObjectTransition(true);
//		secondInnerComposite.addTransition(validSecondInnerCompositeTransition);
//		assertWithMessage("More immediate CompositeState ancestors should take precedence over more distant CompositeState ancestors")
//				.that(graph.findFirstValidTransitionFromState(state, null))
//				.isEqualTo(validSecondInnerCompositeTransition);
//
//		// When a state has multiple immediate CompositeStates, precedence should be given to Transitions from the CompositeState that the state was added to earliest.
//		firstComposite.addTransition(mockObjectTransition(false));
//		assertThat(graph.findFirstValidTransitionFromState(state, null)).isEqualTo(validSecondInnerCompositeTransition);
//
//		Transition<Object> validFirstCompositeTransition = mockObjectTransition(true);
//		firstComposite.addTransition(validFirstCompositeTransition);
//		assertWithMessage("When a state has multiple immediate CompositeStates, precedence should be given to Transitions from the CompositeState that the state was added to earliest")
//				.that(graph.findFirstValidTransitionFromState(state, null))
//				.isEqualTo(validFirstCompositeTransition);
//
//		// Now check the transitions on the state itself
//		graph.addTransition(state, mockObjectTransition(false));
//		assertThat(graph.findFirstValidTransitionFromState(state, null)).isEqualTo(validFirstCompositeTransition);
//
//		Transition<Object> validStateTransition = mockObjectTransition(true);
//		graph.addTransition(state, validStateTransition);
//		assertWithMessage("Valid transitions on the state itself should take precedence over global transitions and transitions on ancestor CompositeStates.")
//				.that(graph.findFirstValidTransitionFromState(state, null))
//				.isEqualTo(validStateTransition);
//	}
}
