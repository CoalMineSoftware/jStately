package com.coalminesoftware.jstately.graph.transition;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class EqualityTransitionTest {
	@Test
	public void testIsValidWithNonNullValidInput() {
		Transition<Integer> transition = new EqualityTransition<>(null, 1);

		assertThat("Transition should not be valid", transition.isValid(null), is(false));
		assertThat("Transition should not be valid", transition.isValid(0), is(false));
		assertThat("Transition should be valid", transition.isValid(1), is(true));
	}

	@Test
	public void testIsValidWithNullValidInput() {
		Transition<Integer> transition = new EqualityTransition<>(null, null);

		assertThat("Transition should be valid", transition.isValid(null), is(true));
		assertThat("Transition should not be valid", transition.isValid(0), is(false));
		assertThat("Transition should not be valid", transition.isValid(1), is(false));
	}
}
