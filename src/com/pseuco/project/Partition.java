package com.pseuco.project;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Partition {

	private final Collection<Collection<State>> blocks;
	private final Map<State, Collection<State>> stateToBlockMap =
			new HashMap<State, Collection<State>>();

	public Partition(final Collection<Collection<State>> blocks) {
		this.blocks = blocks;
		for (final Collection<State> b : blocks) {
			for (final State s : b) {
				stateToBlockMap.put(s, b);
			}
		}
	}

	public Collection<State> getContainingBlock(final State s) {
		return stateToBlockMap.get(s);
	}

	@Override
	public String toString() {
		return blocks.toString();
	}

}
