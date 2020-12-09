package com.coalminesoftware.jstately.graph.transition;

import com.coalminesoftware.jstately.graph.state.State;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static com.coalminesoftware.jstately.collection.CollectionUtil.isEmpty;
import static java.util.Objects.requireNonNull;

public class TransitionBuilder<TransitionInput> {
	private final State<TransitionInput> head;
	private final Predicate<TransitionInput> validityPredicate;
	private TransitionListener<TransitionInput> transitionListener;

	public TransitionBuilder(@Nonnull State<TransitionInput> head,
			@Nonnull Predicate<TransitionInput> validityPredicate) {
		this.head = requireNonNull(head, "Head is required");
		this.validityPredicate = requireNonNull(validityPredicate, "Validity predicate is required");
	}

	@SafeVarargs
	@Nonnull
	public static <TransitionInput> TransitionBuilder<TransitionInput> forExpectedInputs(
			@Nonnull State<TransitionInput> head,
			@Nonnull TransitionInput... validInputs) {
		if(isEmpty(validInputs)) {
			throw new IllegalArgumentException("Valid inputs are required");
		}

		Set<TransitionInput> inputSet = new HashSet<>(validInputs.length);
		Collections.addAll(inputSet, validInputs);

		return new TransitionBuilder<>(head, inputSet::contains);
	}

	@Nonnull
	public TransitionBuilder<TransitionInput> setTransitionListener(@Nullable TransitionListener<TransitionInput> listener) {
		transitionListener = listener;
		return this;
	}

	@Nonnull
	public Transition<TransitionInput> build() {
		return new Transition<>(head, validityPredicate, transitionListener);
	}
}
