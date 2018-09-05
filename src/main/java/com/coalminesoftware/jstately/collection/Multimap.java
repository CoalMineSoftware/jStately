package com.coalminesoftware.jstately.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Minimal implementation of a map that allows multiple values for a single key.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class Multimap<K,V> {
	Map<K, Set<V>> mValuesByKey = new HashMap<>();

	public boolean put(K key, V value) {
		Set<V> values = mValuesByKey.get(key);
		if(values == null) {
			values = new LinkedHashSet<>();
			mValuesByKey.put(key, values);
		}

		return values.add(value);
	}

	public Set<V> get(K key) {
		return mValuesByKey.containsKey(key) ?
				makeUnmodifiableCopy(mValuesByKey.get(key)) :
				Collections.<V>emptySet();
	}

	public Collection<V> values() {
		List<V> values = new ArrayList<>();

		for(Set<V> valueSet : mValuesByKey.values()) {
			values.addAll(valueSet);
		}

		return values;
	}

	private Set<V> makeUnmodifiableCopy(Set<V> set) {
		return Collections.unmodifiableSet(new LinkedHashSet<>(set));
	}
}
