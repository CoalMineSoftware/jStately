package com.coalminesoftware.jstately.collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static java.util.Objects.requireNonNull;

public final class CollectionUtil {
	private CollectionUtil() { }

	/** Pre-Java 10 alternative to List.copyOf(items) */
	@Nonnull
	public static <T> List<T> unmodifiableCopy(@Nonnull Collection<T> items) {
		return Collections.unmodifiableList(new ArrayList<>(items));
	}

	/** @return A mutable Set with the given values. */
	@SafeVarargs
	@Nonnull
	public static <T> Set<T> asMutableSet(@Nonnull T... values) {
		requireNonNull(values);
		Set<T> valueSet = new HashSet<>();
		Collections.addAll(valueSet, values);

		return valueSet;
	}

	/** @return A copy of the given list, in the opposite order. */
	@Nonnull
	public static <T> List<T> reversedCopy(@Nonnull List<T> list) {
		List<T> reversedList = new ArrayList<>(list);
		Collections.reverse(reversedList);

		return reversedList;
	}

	public static <T> boolean isEmpty(@Nullable T[] array) {
		return array == null || array.length == 0;
	}
}
