package com.coalminesoftware.jstately.graph.transition;

import com.coalminesoftware.jstately.collection.CollectionUtil;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DisjunctiveEqualityTransitionTest {
	@Test
	public void testIsValid() {
		DisjunctiveEqualityTransition<Integer> transition = new DisjunctiveEqualityTransition<>();
		transition.setValidInputs(CollectionUtil.asMutableSet(1,2));

		assertFalse("Transition should not be valid for an input not in its set of valid inputs", transition.isValid(0));
		assertTrue("Transition should be valid for an input in its set of valid inputs", transition.isValid(1));
		assertTrue("Transition should be valid for an input in its set of valid inputs", transition.isValid(2));
		assertFalse("Transition should be valid for an input in its set of valid inputs", transition.isValid(null));
	}
}
