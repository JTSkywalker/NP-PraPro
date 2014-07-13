package com.pseuco.project;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TopologicalOrder {

	private final Lts lts;
	private final List<State> order = new LinkedList<State>();
	private final Set<State> marked = new HashSet<State>();

	public TopologicalOrder(final Lts lts) {
		this.lts = lts;
		for (State s : lts.getStates()) {
			dfs(s);
		}
	}

	public Iterable<State> get() {
		return order;
	}

	private void dfs(State s) {
		if (marked.contains(s)) {
			return;
		}
		marked.add(s);
		for (State s2 : lts.post(s, Action.INTERNAL)) {
			dfs(s2);
		}
		order.add(s);
	}
}
