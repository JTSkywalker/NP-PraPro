package com.pseuco.project;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

public class ObservationalCongruentCondensation {

	private final Lts weakLts, strongLts;
	private final Partition partition;
	private int stateCounter = 0;
	private final Map<Collection<State>, State> blockToStateMap =
			new HashMap<Collection<State>, State>();
	private final Collection<State> states = new LinkedList<State>();
	private final Collection<Transition> transitions =
			new HashSet<Transition>();

	public ObservationalCongruentCondensation(final Lts strongLts,
			final Lts weakLts, final Partition partition) {
		this.strongLts = strongLts;
		this.weakLts = weakLts;
		this.partition = partition;
	}

	public Lts calculate() {
		final State originalInitialState = weakLts.getInitialState();
		final Collection<State> initialBlock =
				partition.getContainingBlock(originalInitialState);
		explore(initialBlock);
		final State initialState = blockToStateMap.get(initialBlock);
		for (final State s : strongLts.post(originalInitialState, Action.INTERNAL)) {
			if (partition.getContainingBlock(s) == initialBlock) {
				transitions.add(new Transition(initialState, Action.INTERNAL,
						initialState));
				break;
			}
		}
		return new Lts(states, weakLts.getActions(), transitions,
				initialState);
	}

	private void explore(final Collection<State> block) {
		if (blockToStateMap.containsKey(block)) {
			return;
		}
		final State newState = new State(new Integer(stateCounter++).toString());
		states.add(newState);
		blockToStateMap.put(block, newState);
		final State representative = block.iterator().next();
		for (final Transition t : weakLts.outTransitions(representative)) {
			final Collection<State> targetBlock =
					partition.getContainingBlock(t.getTarget());
			explore(targetBlock);
			transitions.add(new Transition(newState, t.getLabel(),
					blockToStateMap.get(targetBlock)));
		}
	}

}
