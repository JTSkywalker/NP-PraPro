package com.pseuco.project;

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
    	return b.calculateCoarsestPartition();
	}

	private Lts calculateMinimal(final Lts strongLts, final Lts weakLts,
			final Partition partition) {
		BisimilarCondensation c = new BisimilarCondensation(weakLts, partition);
    	return c.calculate();
	}

}