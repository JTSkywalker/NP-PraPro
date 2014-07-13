package com.pseuco.project;

public class LtsMinimizer {
	public Lts minimize(Lts lts) {
		Lts weakLts = calculateWeakLts(lts);
		Partition partition = calculatePartition(weakLts);
		return calculateMinimal(lts, weakLts, partition);
	}
	
	private Lts calculateWeakLts(Lts lts) {
    	throw new UnsupportedOperationException();
	}

	private Partition calculatePartition(final Lts lts) {
    	final Bisimulation b = new Bisimulation(lts);
    	return b.calculateCoarsestPartition();
	}
	
	private Lts calculateMinimal(final Lts strongLts, final Lts weakLts,
			final Partition partition) {
		ObservationalCongruentCondensation c = new ObservationalCongruentCondensation(
				strongLts, weakLts, partition);
    	return c.calculate();
	}

}