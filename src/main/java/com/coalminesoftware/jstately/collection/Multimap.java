package com.coalminesoftware.jstately.collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Minimal implementation of a map that allows multiple values for a single key.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class Multimap<K,V> {
	private final Map<K, Set<V>> valuesByKey = new HashMap<>();

	public boolean put(@Nullable K key, @Nullable V value) {
		return valuesByKey.computeIfAbsent(key, k -> new LinkedHashSet<>()).add(value);
	}

	@Nonnull
	public Set<V> get(@Nullable K key) {
		return valuesByKey.containsKey(key) ?
				unmodifiableCopy(valuesByKey.get(key)) :
				Collections.emptySet();
	}

	@Nonnull
	public List<V> values() {
		List<V> values = new ArrayList<>();
		for(Set<V> valueSet : valuesByKey.values()) {
			values.addAll(valueSet);
		}

		return values;
	}

	@Nonnull
	private Set<V> unmodifiableCopy(@Nonnull Set<V> set) {
		return Collections.unmodifiableSet(new LinkedHashSet<>(set));
	}
}
