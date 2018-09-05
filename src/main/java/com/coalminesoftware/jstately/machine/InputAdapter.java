package com.coalminesoftware.jstately.machine;

/**
 * Allows a machine's input type to differ from its graph's transitions' input type. Perhaps
 * the most common academic example of a state machine is one that takes a string as its input and
 * iterates over the string's characters. In that example, an API client would use an InputAdapter
 * with String as the machine input type and Character as the transition input type.
 * <p>
 * Users who would like to use the same input type for both the machine and its graph's
 * transitions will likely want to use {@link DefaultInputAdapter} rather than implementing their
 * own InputAdapater.
 */
public interface InputAdapter<MachineInput,TransitionInput> {
	void setInput(MachineInput machineInput);

	boolean hasNext();
	TransitionInput next();
}
