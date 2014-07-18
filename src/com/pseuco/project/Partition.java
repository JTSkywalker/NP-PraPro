package com.pseuco.project;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
/**
 * 
 * Eine Partition kann als unveränderliche Menge von Äquivalenzklassen einer
 * Bisimulation verwendet werden. Der Zugriff verbraucht benötigt Zeit.
 *
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
