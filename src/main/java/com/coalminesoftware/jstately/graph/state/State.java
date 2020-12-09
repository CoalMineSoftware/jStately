package com.coalminesoftware.jstately.graph.state;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

/** Representation of a state, with callbacks for when the state is entered and exited by a machine. */
public class State<TransitionInput> {
	private final List<CompositeState<TransitionInput>> composites = new ArrayList<>();
	private final EntranceListener entranceListener;
	private final ExitListener exitListener;
	private final String description;

	protected State(@Nullable EntranceListener entranceListener,
			@Nullable ExitListener exitListener,
			@Nullable String description) {
		this.entranceListener = entranceListener;
		this.exitListener = exitListener;
		this.description = description;
	}

	@Nonnull
	public List<CompositeState<TransitionInput>> getComposites() {
		return Collections.unmodifiableList(composites);
	}

	void addComposite(CompositeState<TransitionInput> composite) {
		composites.add(requireNonNull(composite, "Composite cannot be null"));
	}

	public void notifyEntranceListener() {
		if (entranceListener != null) {
			entranceListener.onEnter();
		}
	}

	public void notifyExitListener() {
		if (exitListener != null) {
			exitListener.onExit();
		}
	}

	@Nullable
	public String getDescription() {
		return description;
	}

	@Nonnull
	public String toString() {
		return super.toString() + "[description=" + getDescription() + "]";
	}
}
