package com.coalminesoftware.jstately.machine.input;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

/**
 * Allows a machine's input type to differ from its graph's transitions' input type. Perhaps the
 * most common academic example of a state machine is one that takes a string as its input and
 * iterates over the string's characters. That example could be implemented with an InputAdapter
 * that takes a String as the machine input and outputs Character as the transition input.
 * <p>
 * Users who would like to use the same input type for both the machine and its graph's transitions
 * will likely want to use {@link PassthroughInputAdapter}.
 */
public interface InputAdapter<MachineInput,TransitionInput> {
	@Nonnull
	Iterator<TransitionInput> adaptInput(@Nullable MachineInput machineInput);
}
