package com.hs2n.exercise.lifegame.util;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class MapCounter<K> {

    private Map<K, Integer> counts = new HashMap<>();

    @SafeVarargs
    public MapCounter(K... keys) {
        if (keys.length == 0) {
            throw new IllegalArgumentException();
        }
        Stream.of(keys).forEach(key -> counts.put(key, 0));
    }

    public void increment(K key) {
        validateKey(key);
        counts.put(key, counts.get(key) + 1);
    }

    public int getCount(K key) {
        validateKey(key);
        return counts.get(key);
    }

    private void validateKey(K key) throws IllegalArgumentException {
        if (!counts.containsKey(key)) {
            throw new IllegalArgumentException();
        }
    }

}
