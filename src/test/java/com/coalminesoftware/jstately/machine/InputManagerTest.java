package com.coalminesoftware.jstately.machine;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
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
		assertThat(1, is(manager.next()));
		assertThat(manager.hasNext(), is(true));
		assertThat(2, is(manager.next()));
		assertThat(manager.hasNext(), is(true));
		assertThat(3, is(manager.next()));
		assertThat(manager.hasNext(), is(true));
		assertThat(4, is(manager.next()));

		assertThat(manager.hasNext(), is(false));

		manager.queueInput(Arrays.asList(5, 6));

		assertThat(manager.hasNext(), is(true));
		assertThat(5, is(manager.next()));
		assertThat(manager.hasNext(), is(true));
		assertThat(6, is(manager.next()));
	}

	private class TestInputAdapter implements InputAdapter<List<Integer>,Integer> {
		Iterator<Integer> iterator;

		@Override
		public void setInput(List<Integer> input) {
			iterator = input.iterator();
		}

		@Override
		public boolean hasNext() {
			return iterator != null && iterator.hasNext();
		}

		@Override
		public Integer next() {
			return iterator.next();
		}
	}
}
