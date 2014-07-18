package com.pseuco.project;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

public class BisimilarCondensation {
	/**
	 * Gibt das LTS zurück, bei dem alle Blöcke aus partition durch einen
	 * Zustand repräsentiert sind, der zu allen Zusänden des Blocks stark
	 * bisimilar ist. Vorraussetzung: Alle Zustände in einem Block von partition
	 * sind stark bisimilar.
	 */
	public static Lts call(Lts lts, Partition partition) {
		return new BisimilarCondensation().calculate(lts, partition);
	}

	private Lts weakLts;
	private Partition partition;
	private int stateCounter = 0;
	private final Map<Block, State> blockToStateMap =
			new HashMap<Block, State>();
	private final Collection<State> states = new LinkedList<State>();
	private final Collection<Transition> transitions =
			new HashSet<Transition>();

	private Lts calculate(final Lts weakLts, final Partition partition) {
		this.weakLts = weakLts;
		this.partition = partition;
		final State originalInitialState = weakLts.getInitialState();
		final Block initialBlock =
				partition.getContainingBlock(originalInitialState);
		explore(initialBlock);
		return new Lts(states, weakLts.getActions(), transitions,
				blockToStateMap.get(initialBlock));
	}

	private void explore(final Block block) {
		if (blockToStateMap.containsKey(block)) {
			return;
		}
		final State newState =
				new State(new Integer(stateCounter++).toString());
		states.add(newState);
		blockToStateMap.put(block, newState);
		final State representative = block.getStates().iterator().next();
		for (final Transition t : weakLts.outTransitions(representative)) {
			final Block targetBlock =
					partition.getContainingBlock(t.getTarget());
			explore(targetBlock);
			State targetState = blockToStateMap.get(targetBlock);
			transitions.add(new Transition(newState, t.getLabel(),
					targetState));
		}
	}
}
