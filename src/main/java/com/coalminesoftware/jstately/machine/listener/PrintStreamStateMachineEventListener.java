package com.coalminesoftware.jstately.machine.listener;

import com.coalminesoftware.jstately.graph.state.CompositeState;
import com.coalminesoftware.jstately.graph.state.State;
import com.coalminesoftware.jstately.graph.transition.Transition;
import com.coalminesoftware.jstately.machine.StateMachine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.PrintStream;

import static java.util.Objects.requireNonNull;

/** Event listener that prints events to a PrintStream. */
public class PrintStreamStateMachineEventListener<TransitionInput> implements StateMachineEventListener<TransitionInput> {
	private final PrintStream printStream;

	public PrintStreamStateMachineEventListener() {
		this(System.out);
	}

	public PrintStreamStateMachineEventListener(@Nonnull PrintStream printStream) {
		this.printStream = requireNonNull(printStream, "Print stream is required");
	}

	@Override
	public void beforeEvaluatingInput(@Nullable TransitionInput input, @Nonnull StateMachine<?,TransitionInput> machine) {
		printStream.println("Before evaluating input ("+input+") on machine ("+machine+")");
	}

	@Override
	public void afterEvaluatingInput(@Nullable TransitionInput input, @Nonnull StateMachine<?,TransitionInput> machine) {
		printStream.println("After evaluating input ("+input+") on machine ("+machine+")");
	}

	@Override
	public void beforeStateEntered(@Nonnull State<TransitionInput> state, @Nonnull StateMachine<?,TransitionInput> machine) {
		printStream.println("Before state ("+state+") entered on machine ("+machine+")");
	}

	@Override
	public void afterStateEntered(@Nonnull State<TransitionInput> state, @Nonnull StateMachine<?,TransitionInput> machine) {
		printStream.println("After state ("+state+") entered on machine ("+machine+")");
	}

	@Override
	public void beforeStateExited(@Nonnull State<TransitionInput> state, @Nonnull StateMachine<?,TransitionInput> machine) {
		printStream.println("Before state ("+state+") exited on machine ("+machine+")");
	}

	@Override
	public void afterStateExited(@Nonnull State<TransitionInput> state, @Nonnull StateMachine<?,TransitionInput> machine) {
		printStream.println("After state ("+state+") exited on machine ("+machine+")");
	}

	@Override
	public void beforeTransition(@Nonnull Transition<TransitionInput> transition, @Nullable TransitionInput input, @Nonnull StateMachine<?,TransitionInput> machine) {
		printStream.println("Before following transition ("+transition+") for input ("+input+") on machine ("+machine+")");
	}

	@Override
	public void afterTransition(@Nonnull Transition<TransitionInput> transition, @Nullable TransitionInput input, @Nonnull StateMachine<?,TransitionInput> machine) {
		printStream.println("After following transition ("+transition+") for input ("+input+") on machine ("+machine+")");
	}

	@Override
	public void noValidTransition(@Nullable TransitionInput input, @Nonnull StateMachine<?,TransitionInput> machine) {
		printStream.println("No transition found for input ("+input+") on machine ("+machine+")");
	}

	@Override
	public void beforeCompositeStateEntered(@Nonnull CompositeState<TransitionInput> composite, @Nonnull StateMachine<?,TransitionInput> machine) {
		printStream.println("Before entering composite state ("+composite+") on machine ("+machine+")");
	}

	@Override
	public void afterCompositeStateEntered(@Nonnull CompositeState<TransitionInput> composite, @Nonnull StateMachine<?,TransitionInput> machine) {
		printStream.println("After entering composite state ("+composite+") on machine ("+machine+")");
	}

	@Override
	public void beforeCompositeStateExited(@Nonnull CompositeState<TransitionInput> composite, @Nonnull StateMachine<?,TransitionInput> machine) {
		printStream.println("Before exiting composite state ("+composite+") on machine ("+machine+")");
	}

	@Override
	public void afterCompositeStateExited(@Nonnull CompositeState<TransitionInput> composite, @Nonnull StateMachine<?,TransitionInput> machine) {
		printStream.println("After exiting composite state ("+composite+") on machine ("+machine+")");
	}
}
