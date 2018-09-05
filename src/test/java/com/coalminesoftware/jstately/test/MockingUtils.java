package com.coalminesoftware.jstately.test;

import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.Transition;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public final class MockingUtils {
	private MockingUtils() { }

	public static Transition<Object> mockObjectTransition(boolean valid) {
		return mockTransition(Object.class, valid, mockState());
	}

	public static Transition<Object> mockObjectTransition(boolean valid, State<Object> state) {
		return mockTransition(Object.class, valid, state);
	}

	public static <Input> Transition<Input> mockTransition(Class<Input> transitionInputType, boolean valid, State<Input> head) {
		Transition<Input> transition = mock(Transition.class);

		doReturn(head).when(transition).getHead();
		doReturn(valid).when(transition).isValid((Input) isNull());
		doReturn(valid).when(transition).isValid(any(transitionInputType));

		return transition;
	}

	@SuppressWarnings("unchecked")
	public static <Input> State<Input> mockState() {
		return mock(State.class);
	}
}
