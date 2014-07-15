package com.pseuco.project;

import java.util.Collections;

public class RedundantTransitionRemover {

	private final Lts oldLts, newLts;

	public RedundantTransitionRemover(Lts lts) {
		this.oldLts = lts;
		newLts = new Lts(oldLts.getStates(), oldLts.getActions(),
				Collections.<Transition> emptySet(), oldLts.getInitialState());

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
		State source = t.getSource();
		Action label = t.getLabel();
		State target = t.getTarget();

		if (label.equals(Action.INTERNAL)) {
			if (source.equals(target)) {
				return true;
			}
			for (State step1 : oldLts.post(source, Action.INTERNAL)) {
				if (!step1.equals(source) && !step1.equals(target)) {
					if (oldLts.post(step1, Action.INTERNAL).contains(target)) {
						return true;
					}
				}
			}
		} else {
			for (State step1 : oldLts.post(source, Action.INTERNAL)) {
				for (State step2 : oldLts.post(step1, label)) {
					if(!step1.equals(source) || !step2.equals(target)) {
						if (oldLts.post(step2, Action.INTERNAL)
								.contains(target)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}
}
