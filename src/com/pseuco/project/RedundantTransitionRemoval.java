package com.pseuco.project;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
			Set<State> set = new HashSet<State>();
			set.addAll(newLts.post(s1, t.getLabel()));
			for (State s2 : oldLts.post(s1, Action.INTERNAL)) {
				set.addAll(newLts.post(s2, t.getLabel()));
			}

			for (State s3 : set) {
				if (s3.equals(t.getTarget())) {
					return true;
				}
				for (State s4 : oldLts.post(s3, Action.INTERNAL)) {
					if (s4.equals(t.getTarget())) {
						return true;
					}
				}
			}

			return false;
		}
	}
}
