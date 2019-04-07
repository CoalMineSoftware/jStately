package com.coalminesoftware.jstately.machine.input;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InputManagerTest {
	@Test
	public void testNextAndHasNext() {
		InputManager<List<Integer>, Integer> manager = new InputManager<>();
		manager.setInputAdapter(new TestInputAdapter());

		assertThat(manager.hasNext(), is(false));

		manager.queueInput(Arrays.asList(1, 2));
		manager.queueInput(Collections.<Integer>emptyList());
		manager.queueInput(Arrays.asList(3));
		manager.queueInput(Arrays.asList(4));

		assertThat(manager.hasNext(), is(true));
		assertThat(manager.next(), is(1));
		assertThat(manager.hasNext(), is(true));
		assertThat(manager.next(), is(2));
		assertThat(manager.hasNext(), is(true));
		assertThat(manager.next(), is(3));
		assertThat(manager.hasNext(), is(true));
		assertThat(manager.next(), is(4));

		assertThat(manager.hasNext(), is(false));

		manager.queueInput(Arrays.asList(5, 6));

		assertThat(manager.hasNext(), is(true));
		assertThat(manager.next(), is(5));
		assertThat(manager.hasNext(), is(true));
		assertThat(manager.next(), is(6));

		assertThat(manager.hasNext(), is(false));
	}

	private class TestInputAdapter implements InputAdapter<List<Integer>,Integer> {
		@Override
		public Iterator<Integer> adaptInput(List<Integer> integers) {
			return integers.iterator();
		}
	}
}
