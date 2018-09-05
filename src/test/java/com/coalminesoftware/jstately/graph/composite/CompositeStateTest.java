package com.coalminesoftware.jstately.graph.composite;

import com.coalminesoftware.jstately.graph.state.DefaultState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.Transition;
import org.junit.Test;

import static com.coalminesoftware.jstately.collection.CollectionUtil.asMutableSet;
import static com.coalminesoftware.jstately.test.MockingUtils.mockObjectTransition;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class CompositeStateTest {
	@Test
	public void testAddComposite() {
		CompositeState<Object> parentComposite = new CompositeState<>();
		CompositeState<Object> childComposite = new CompositeState<>();

		parentComposite.addComposite(childComposite);

		assertThat("When a child is added to a parent, that relationship should be represented by the child maintaining a reference to its parent.",
				childComposite.getParent(),
				is(parentComposite));
	}

	@Test
	public void testAddState() {
		State<Object> state = new DefaultState<>();
		CompositeState<Object> firstCompositeState = new CompositeState<>();
		CompositeState<Object> secondCompositeState = new CompositeState<>();

		firstCompositeState.addState(state);

		assertThat("The state should have a reference to only the composite that the state was added to",
				state.getComposites(),
				is(asList(firstCompositeState)));

		secondCompositeState.addState(state);

		assertThat("The state should have a reference to both of the composites that the state was added to",
				state.getComposites(),
				is(asList(firstCompositeState, secondCompositeState)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testAddStates() {
		State<Object> firstState = new DefaultState<>();
		State<Object> secondState = new DefaultState<>();
		CompositeState<Object> compositeState = new CompositeState<>();

		compositeState.addStates(firstState, secondState);

		assertThat("After calling addStates(), all of the given states should have a reference to the composite.",
				firstState.getComposites(),
				is(asList(compositeState)));
		assertThat("After calling addStates(), all of the given states should have a reference to the composite.",
				secondState.getComposites(),
				is(asList(compositeState)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testAddTransition() {
		CompositeState<Object> composite = new CompositeState<>();
		Transition<Object> firstTransition = mockObjectTransition(true);

		composite.addTransition(firstTransition);

		assertThat("A composite should maintain a reference to any transition added to it.",
				composite.getTransitions(),
				is(singleton(firstTransition)));

		Transition<Object> secondTransition = mockObjectTransition(true);
		composite.addTransition(secondTransition);

		assertThat("A composite should maintain a reference to any transition added to it.",
				composite.getTransitions(),
				is(asMutableSet(firstTransition, secondTransition)));
	}

	@Test
	public void testFindFirstValidTransitionWithoutValidTransition() {
		CompositeState<Object> compositeState = new CompositeState<>();

		Transition<Object> invalidTransition = mockObjectTransition(false);
		compositeState.addTransition(invalidTransition);

		assertThat("No valid transitions should have been found",
				compositeState.findFirstValidTransition(null),
				nullValue());
	}

	@Test
	public void testFindFirstValidTransitionWithValidTransition() {
		CompositeState<Object> compositeState = new CompositeState<>();

		Transition<Object> validTransition = mockObjectTransition(true);
		compositeState.addTransition(validTransition);

		assertThat("The valid transition should have been returned",
				compositeState.findFirstValidTransition(null),
				is(validTransition));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFindValidTransitions() {
		CompositeState<Object> compositeState = new CompositeState<>();

		Transition<Object> firstValidTransition = mockObjectTransition(true);
		compositeState.addTransition(firstValidTransition);

		Transition<Object> secondValidTransition = mockObjectTransition(true);
		compositeState.addTransition(secondValidTransition);
 
		Transition<Object> firstInvalidTransition = mockObjectTransition(false);
		compositeState.addTransition(firstInvalidTransition);

		Transition<Object> secondInvalidTransition = mockObjectTransition(false);
		compositeState.addTransition(secondInvalidTransition);

		assertThat("The valid transition should have been returned",
				compositeState.findValidTransitions(null),
				is(asMutableSet(firstValidTransition, secondValidTransition)));
	}
}
