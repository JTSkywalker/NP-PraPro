package com.pseuco.project;

public class LtsMinimizer {
	public Lts minimize(Lts lts) {
		Lts weakLts = calculateWeakLts(lts);
		Partition partition = calculatePartition(weakLts);
		return calculateMinimal(weakLts, partition);
	}
	
	private Lts calculateWeakLts(Lts lts) {
    	throw new UnsupportedOperationException();
	}
	
	private Partition calculatePartition(Lts lts) {
    	throw new UnsupportedOperationException();
	}
	
	private Lts calculateMinimal(Lts lts, Partition partition) {
    	throw new UnsupportedOperationException();
	}

}