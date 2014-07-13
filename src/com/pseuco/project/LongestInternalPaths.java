package com.pseuco.project;

import java.util.HashMap;
import java.util.Map;

public class LongestInternalPaths {

	private final Map<State, Map<State, Integer>> lengthMap =
			new HashMap<State, Map<State, Integer>>();

	public LongestInternalPaths(final Lts lts) {
		Iterable<State> order = new TopologicalOrder(lts).get();
		for (State s : order) {
			lengthMap.put(s, new HashMap<State, Integer>());
			lengthMap.get(s).put(s, 0);
			for (State s2 : lts.post(s, Action.INTERNAL)) {
				for (State s3 : lengthMap.get(s2).keySet()) {
					lengthMap.get(s).put(s3, Math.max(getLength(s, s3),
							getLength(s2, s3) + 1));
				}
			}
		}
	}

	public int getLength(State a, State b) {
		if (lengthMap.containsKey(a) && lengthMap.get(a).containsKey(b)) {
			return lengthMap.get(a).get(b);
		} else {
			return 0;
		}
	}
}
