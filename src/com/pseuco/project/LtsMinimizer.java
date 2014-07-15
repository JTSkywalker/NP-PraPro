package com.pseuco.project;

import java.util.Collection;
import java.util.LinkedList;

public class LtsMinimizer {
	public Lts minimize(Lts lts) throws InterruptedException {
		Lts weakLts = calculateWeakLts(lts);
		Partition partition = calculatePartition(weakLts);
		return calculateMinimal(lts, weakLts, partition);
	}

	private Lts calculateWeakLts(Lts lts) {
		//Step 1
		Lts taults = lts.transTauClosure();
		//Step 2
		Collection<Transition> trans = new LinkedList<>();
		for(State s : taults.getStates()) {//TODO concurrency
			Collection<State> postT1 = taults.postTau(s);
			for(State t : postT1) {
				Collection<Transition> postA = taults.outTransitions(t);
				for(Transition a : postA) {
					Collection<State> postT2 = taults.postTau(a.getTarget());
					for(State u : postT2) {
						trans.add(new Transition(s,a.getLabel(),u));
					}
				}
			}
		}
    	throw new UnsupportedOperationException();
	}

	private Partition calculatePartition(final Lts lts)
			throws InterruptedException {
		final Bisimulation b = new Bisimulation(lts);
    	return b.getCoarsestPartition();
	}

	private Lts calculateMinimal(final Lts strongLts, final Lts weakLts,
			final Partition partition) {
		Lts minLts = (new RedundantTransitionRemover(new BisimilarCondensation(
				weakLts, partition).calculate())).getMinimum();
		State initialState = strongLts.getInitialState();
		Block initialBlock = partition
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