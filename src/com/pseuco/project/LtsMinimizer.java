package com.pseuco.project;


public class LtsMinimizer {
	public Lts minimize(final Lts lts) throws InterruptedException {
		final Lts weakLts = WeakLtsCalculator.call(lts);
		final Bisimulation b = new Bisimulation(weakLts);
		final Partition p = b.getCoarsestPartition();
		final Lts c = BisimilarCondensation.call(weakLts, p);
		final Lts d = RedundantTransitionRemover.call(c);
		final State initialState = lts.getInitialState();
		Block initialBlock = p.getContainingBlock(initialState);
		for (State s : lts.post(initialState, Action.INTERNAL)) {
			if (p.getContainingBlock(s).equals(initialBlock)) {
				d.addTransition(new Transition(d.getInitialState(),
						Action.INTERNAL, d.getInitialState()));
				break;
			}
		}
		return d;
	}
}