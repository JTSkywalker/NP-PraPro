package com.pseuco.project;

public class LtsMinimizer {

	/**
	 * @return Minimum LTS that is strongly bisimilar to the given one
	 * @throws InterruptedException
	 */
	public Lts minimize(final Lts lts) throws InterruptedException {
		final Lts weakLts = WeakLtsCalculator.call(lts);
		final Partition p = BisimulationCalculator.call(weakLts);
		final Lts weakBisimilarLts = BisimilarCondensation.call(weakLts, p);
		final Lts minWeakBisimilarLts = RedundantTransitionRemover
				.call(weakBisimilarLts);
		return restoreObservationalCongruence(lts, p, minWeakBisimilarLts);
	}

	private Lts restoreObservationalCongruence(Lts originalLts, Partition p,
			Lts weakBisimilarLts) {
		final State oldInitialState = originalLts.getInitialState();
		Block initialBlock = p.getContainingBlock(oldInitialState);
		for (State s : originalLts.post(oldInitialState, Action.INTERNAL)) {
			if (p.getContainingBlock(s).equals(initialBlock)) {
				State newInitialState = weakBisimilarLts.getInitialState();
				weakBisimilarLts.addTransition(new Transition(newInitialState,
						Action.INTERNAL, newInitialState));
				break;
			}
		}
		return weakBisimilarLts;
	}
}