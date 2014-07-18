package com.pseuco.project;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Eine Partition beschreibt die Einteilung der Zustände eines Lts in disjunkte
 * Blöcke.
 */
public class Partition {

	private final Collection<Block> blocks;
	private final Map<State, Block> stateToBlockMap =
			new HashMap<State, Block>();

	public Partition(final Collection<Block> blocks) {
		this.blocks = blocks;
		for (final Block b : blocks) {
			for (final State s : b.getStates()) {
				stateToBlockMap.put(s, b);
			}
		}
	}

	public Block getContainingBlock(final State s) {
		return stateToBlockMap.get(s);
	}

	@Override
	public String toString() {
		return blocks.toString();
	}

}
