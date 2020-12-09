package com.coalminesoftware.jstately.graph.state;

import com.coalminesoftware.jstately.graph.transition.Transition;
import org.junit.Test;

import static com.coalminesoftware.jstately.test.MockingUtils.mockObjectTransition;
import static com.google.common.truth.Truth.assertWithMessage;

public class CompositeStateTest {
	@Test
	public void testFindFirstValidTransitionWithoutValidTransition() {
		CompositeState<Object> compositeState = new CompositeStateBuilder<>()
				.addTransition(mockObjectTransition(false))
				.build();

		assertWithMessage("No valid transitions should have been found")
				.that(compositeState.findFirstValidTransition(null))
				.isNull();
	}

	@Test
	public void testFindFirstValidTransitionWithValidTransition() {
		Transition<Object> invalidTransition = mockObjectTransition(false);
		Transition<Object> firstValidTransition = mockObjectTransition(true);
		Transition<Object> secondValidTransition = mockObjectTransition(true);
		CompositeState<Object> compositeState = new CompositeStateBuilder<>()
				.addTransition(invalidTransition)
				.addTransition(firstValidTransition)
				.addTransition(secondValidTransition)
				.build();

		assertWithMessage("Valid transitions should be returned")
				.that(compositeState.findFirstValidTransition(null))
				.isEqualTo(firstValidTransition);
	}

	@Test
	public void testFindValidTransitions() {
		Transition<Object> firstValidTransition = mockObjectTransition(true);
		Transition<Object> secondValidTransition = mockObjectTransition(true);
		Transition<Object> firstInvalidTransition = mockObjectTransition(false);
		Transition<Object> secondInvalidTransition = mockObjectTransition(false);

		CompositeState<Object> compositeState = new CompositeStateBuilder<>()
				.addTransition(firstValidTransition)
				.addTransition(secondValidTransition)
				.addTransition(firstInvalidTransition)
				.addTransition(secondInvalidTransition)
				.build();

		assertWithMessage("The valid transition should have been returned")
				.that(compositeState.findValidTransitions(null))
				.containsExactly(firstValidTransition, secondValidTransition);
	}
}
