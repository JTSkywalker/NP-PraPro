package com.pseuco.project;

import java.util.Collections;

public class RedundantTransitionRemoval {

	private final Lts oldLts, newLts;
	private final LongestInternalPaths paths;

	public RedundantTransitionRemoval(Lts lts) {
		this.oldLts = lts;
		newLts = new Lts(oldLts.getStates(), oldLts.getActions(),
				Collections.<Transition> emptySet(), oldLts.getInitialState());

		paths = new LongestInternalPaths(oldLts);
		for (Transition t : oldLts.getTransitions()) {
			if (!isRedundant(t)) {
				newLts.addTransition(t);
			}
		}
	}

	public Lts getMinimum() {
		return newLts;
	}

	private boolean isRedundant(Transition t) {
		if (t.getLabel().equals(Action.INTERNAL)) {
			return paths.getLength(t.getSource(), t.getTarget()) > 1;
		} else {
			State s1 = t.getSource();
			for (State s2 : newLts.post(s1, t.getLabel())) {
				if (s2.equals(t.getTarget())
						|| oldLts.post(s2, Action.INTERNAL).contains(
								t.getTarget())) {
					return true;
				}
			}
			for (State s2 : oldLts.post(s1, Action.INTERNAL)) {
				for (State s3 : newLts.post(s2, t.getLabel())) {
					if (s3.equals(t.getTarget())
							|| oldLts.post(s3, Action.INTERNAL).contains(
									t.getTarget())) {
						return true;
					}
				}
			}

			return false;
		}
	}
}
