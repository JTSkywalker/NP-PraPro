package com.pseuco.project;

import java.util.Collections;

public class RedundantTransitionRemover {

	private final Lts oldLts, newLts;

	private RedundantTransitionRemover(Lts lts) {
		this.oldLts = lts;
		newLts = new Lts(oldLts.getStates(), oldLts.getActions(),
				Collections.<Transition> emptySet(), oldLts.getInitialState());

		for (Transition t : oldLts.getTransitions()) {
			if (!isRedundant(t)) {
				newLts.addTransition(t);
			}
		}
	}

	private Lts getMinimum() {
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
			for (State strongSource : oldLts.post(source, Action.INTERNAL)) {
				for (State strongTarget : oldLts.post(strongSource, label)) {
					if (!strongSource.equals(source)
							|| !strongTarget.equals(target)) {
						if (oldLts.post(strongTarget, Action.INTERNAL)
								.contains(target)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public static Lts call(Lts lts) {
		return new RedundantTransitionRemover(lts).getMinimum();
	}
}
