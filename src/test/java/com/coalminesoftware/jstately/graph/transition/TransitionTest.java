package com.coalminesoftware.jstately.graph.transition;

import com.coalminesoftware.jstately.graph.state.State;
import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Mockito.mock;

public class TransitionTest {
	@Test
	public void testIsValid_equalityTransition() {
		State<Integer> head = mock(State.class);
		Transition<Integer> transition = TransitionBuilder.forExpectedInputs(head, 1, 2).build();

		assertWithMessage("Transition should not be valid for an input not in its set of valid inputs")
				.that(transition.isValid(0)).isFalse();
		assertWithMessage("Transition should be valid for an input in its set of valid inputs")
				.that(transition.isValid(1)).isTrue();
		assertWithMessage("Transition should be valid for an input in its set of valid inputs")
				.that(transition.isValid(2)).isTrue();
		assertWithMessage("Transition should be valid for an input in its set of valid inputs")
				.that(transition.isValid(null))
				.isFalse();
	}
}
