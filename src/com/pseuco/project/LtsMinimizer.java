package com.pseuco.project;


public class LtsMinimizer {
	
	/**
	 * Berechnet die Minimierung.
	 * @param lts
	 * @return
	 * 		minimales beobachtungskongruentes LTS zu lts
	 * @throws InterruptedException
	 */
	public Lts minimize(final Lts lts) throws InterruptedException {
		final Lts weakLts 			  = WeakLtsCalculator.call(lts);
		final Partition p             = BisimulationCalculator.call(weakLts);
		final Lts weakBisimilarLts    = BisimilarCondensation.call(weakLts, p);
		final Lts minWeakBisimilarLts = RedundantTransitionRemover.call(weakBisimilarLts);
		return restoreObservationalCongruence(lts, p, minWeakBisimilarLts);
	}
	/**
	 * Stellt die Beobachtungkongruenz wieder her, indem bei Bedarf eine τ-Schleife um den
	 * Startknoten gesetzt wird.
	 * @param originalLts
	 * @param p
	 * @param weakBisimilarLts
	 * @return
	 */
	private Lts restoreObservationalCongruence(Lts originalLts, Partition p, Lts weakBisimilarLts) {
		final State initialState = originalLts.getInitialState();
		Block initialBlock = p.getContainingBlock(initialState);
		for (State s : originalLts.post(initialState, Action.INTERNAL)) {
			if (p.getContainingBlock(s).equals(initialBlock)) {
				weakBisimilarLts.addTransition(new Transition(weakBisimilarLts.getInitialState(),
						Action.INTERNAL, weakBisimilarLts.getInitialState()));
				break;
			}
		}
		return weakBisimilarLts;
	}
}