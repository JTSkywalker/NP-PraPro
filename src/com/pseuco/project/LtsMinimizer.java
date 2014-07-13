package com.pseuco.project;

import java.util.Collection;

public class LtsMinimizer {
	public Lts minimize(Lts lts) {
		Lts weakLts = calculateWeakLts(lts);
		Partition partition = calculatePartition(weakLts);
		return calculateMinimal(lts, weakLts, partition);
	}

	private Lts calculateWeakLts(Lts lts) {
		//Step 1
		Lts tauLts;
		//Step 2

    	throw new UnsupportedOperationException();
	}

	private Partition calculatePartition(final Lts lts) {
    	final Bisimulation b = new Bisimulation(lts);
    	return b.getCoarsestPartition();
	}

	private Lts calculateMinimal(final Lts strongLts, final Lts weakLts,
			final Partition partition) {
		Lts minLts = (new RedundantTransitionRemoval(new BisimilarCondensation(
				weakLts, partition).calculate())).getMinimum();
		State initialState = strongLts.getInitialState();
		Collection<State> initialBlock = partition
				.getContainingBlock(initialState);
		for (State s : strongLts.post(initialState, Action.INTERNAL)) {
			if (partition.getContainingBlock(s).equals(initialBlock)) {
				minLts.addTransition(new Transition(minLts.getInitialState(),
						Action.INTERNAL, minLts.getInitialState()));
				break;
			}
		}
		return minLts;
	}

}