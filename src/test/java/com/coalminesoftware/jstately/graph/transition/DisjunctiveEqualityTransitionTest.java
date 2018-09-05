package com.coalminesoftware.jstately.graph.transition;

import com.coalminesoftware.jstately.collection.CollectionUtil;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class DisjunctiveEqualityTransitionTest {
	@Test
	public void testIsValid() {
		DisjunctiveEqualityTransition<Integer> transition = new DisjunctiveEqualityTransition<>();
		transition.setValidInputs(CollectionUtil.asMutableSet(1,2));

		assertThat("Transition should not be valid for an input not in its set of valid inputs",
				transition.isValid(0), is(false));
		assertThat("Transition should be valid for an input in its set of valid inputs",
				transition.isValid(1), is(true));
		assertThat("Transition should be valid for an input in its set of valid inputs",
				transition.isValid(2), is(true));
		assertThat("Transition should be valid for an input in its set of valid inputs",
				transition.isValid(null), is(false));
	}
}
