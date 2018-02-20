package com.coalminesoftware.jstately.graph.transition;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EqualityTransitionTest {
	@Test
	public void testIsValidWithNonNullValidInput() {
		Transition<Integer> transition = new EqualityTransition<>(null, 1);

		assertFalse("Transition should not be valid", transition.isValid(null));
		assertFalse("Transition should not be valid", transition.isValid(0));
		assertTrue("Transition should be valid", transition.isValid(1));
	}

	@Test
	public void testIsValidWithNullValidInput() {
		Transition<Integer> transition = new EqualityTransition<>(null, null);

		assertTrue("Transition should be valid", transition.isValid(null));
		assertFalse("Transition should not be valid", transition.isValid(0));
		assertFalse("Transition should not be valid", transition.isValid(1));
	}
}
