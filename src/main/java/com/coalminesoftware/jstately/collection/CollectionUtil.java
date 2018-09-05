package com.coalminesoftware.jstately.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CollectionUtil {
	private CollectionUtil() { }

	/** @return A mutable Set with the given values. */
	public static <T> Set<T> asMutableSet(T... values) {
		Set<T> valueSet = new HashSet<>();
		Collections.addAll(valueSet, values);

		return valueSet;
	}

	/** @return A list containing the elements from the given list, in the opposite order. */
	public static <T> List<T> reverse(List<T> list) {
		List<T> reversedList = new ArrayList<>(list);
		Collections.reverse(reversedList);

		return reversedList;
	}
}
