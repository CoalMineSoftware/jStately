package com.coalminesoftware.jstately.graph.composite;

import com.coalminesoftware.jstately.collection.CollectionUtil;
import com.coalminesoftware.jstately.graph.state.DefaultState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.Transition;
import com.coalminesoftware.jstately.test.DefaultTransition;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CompositeStateTest {
	@Test
	public void testAddComposite() {
		CompositeState<Object> parentComposite = new CompositeState<>();
		CompositeState<Object> childComposite = new CompositeState<>();

		parentComposite.addComposite(childComposite);

		assertEquals("When a child is added to a parent, that relationship is represented by the child maintaining a reference to its parent.",
				childComposite.getParent(),
				parentComposite);
	}

	@Test
	public void testAddState() {
		State<Object> state = new DefaultState<>();
		CompositeState<Object> firstCompositeState = new CompositeState<>();
		CompositeState<Object> secondCompositeState = new CompositeState<>();

		firstCompositeState.addState(state);

		assertEquals("The state should have a reference to only the composite that the state was added to",
				Arrays.asList(firstCompositeState),
				state.getComposites());

		secondCompositeState.addState(state);

		assertEquals("The state should have a reference to both of the composites that the state was added to",
				Arrays.asList(firstCompositeState, secondCompositeState),
				state.getComposites());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testAddStates() {
		State<Object> firstState = new DefaultState<>();
		State<Object> secondState = new DefaultState<>();
		CompositeState<Object> compositeState = new CompositeState<>();

		compositeState.addStates(firstState, secondState);

		assertEquals("After calling addStates(), all of the given states should have a reference to the composite.",
				Arrays.asList(compositeState),
				firstState.getComposites());
		assertEquals("After calling addStates(), all of the given states should have a reference to the composite.",
				Arrays.asList(compositeState),
				secondState.getComposites());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testAddTransition() {
		CompositeState<Object> composite = new CompositeState<>();
		Transition<Object> firstTransition = new DefaultTransition<>();

		composite.addTransition(firstTransition);

		assertEquals("A composite should maintain a reference to any transition added to it.",
				Collections.singleton(firstTransition),
				composite.getTransitions());

		Transition<Object> secondTransition = new DefaultTransition<>();
		composite.addTransition(secondTransition);

		assertEquals("A composite should maintain a reference to any transition added to it.",
				CollectionUtil.asMutableSet(firstTransition, secondTransition),
				composite.getTransitions());
	}

	@Test
	public void testFindFirstValidTransitionWithoutValidTransition() {
		CompositeState<Object> compositeState = new CompositeState<>();

		Transition<Object> invalidTransition = new DefaultTransition<>(false);
		compositeState.addTransition(invalidTransition);

		assertNull("No valid transitions should have been found",
				compositeState.findFirstValidTransition(null));
	}

	@Test
	public void testFindFirstValidTransitionWithValidTransition() {
		CompositeState<Object> compositeState = new CompositeState<>();

		Transition<Object> validTransition = new DefaultTransition<>(true);
		compositeState.addTransition(validTransition);

		assertEquals("The valid transition should have been returned",
				validTransition,
				compositeState.findFirstValidTransition(null));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFindValidTransitions() {
		CompositeState<Object> compositeState = new CompositeState<>();

		Transition<Object> firstValidTransition = new DefaultTransition<>(true);
		compositeState.addTransition(firstValidTransition);

		Transition<Object> secondValidTransition = new DefaultTransition<>(true);
		compositeState.addTransition(secondValidTransition);
 
		Transition<Object> firstInvalidTransition = new DefaultTransition<>(false);
		compositeState.addTransition(firstInvalidTransition);

		Transition<Object> secondInvalidTransition = new DefaultTransition<>(false);
		compositeState.addTransition(secondInvalidTransition);

		assertEquals("The valid transition should have been returned",
				CollectionUtil.asMutableSet(firstValidTransition, secondValidTransition),
				compositeState.findValidTransitions(null));
	}
}
