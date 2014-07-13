package com.pseuco.project;

import java.util.ArrayList;
import java.util.List;

public class LtsMinimizer {
	public Lts minimize(Lts lts) {
		Lts weakLts = calculateWeakLts(lts);
		Partition partition = calculatePartition(weakLts);
		return calculateMinimal(lts, weakLts, partition);
	}
	
	private Lts calculateWeakLts(Lts lts) {
		//Step 1
		Lts taults = lts.transTauClosure();
		//Step 2
		List<Transition> trans = new ArrayList<>();
		for(State s : taults.getStates()) {//TODO concurrency
			List<State> postT1 = taults.postTau(s);
			for(State t : postT1) {
				List<Transition> postA = taults.post(t);
				for(Transition a : postA) {
					List<State> postT2 = taults.postTau(a.getTarget());
					for(State u : postT2) {
						trans.add(new Transition(s,a.getLabel(),u));
					}
				}
			}
		}
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