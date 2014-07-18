package com.pseuco.project;

import java.util.Collections;

public class RedundantTransitionRemover {

	public static Lts call(Lts lts) {
		return new RedundantTransitionRemover().calculateMinimum(lts);
	}
	
	private Lts oldLts;

	/**
	 * @param lts
	 * @return
	 * 		lts ohne redundante Transitionen
	 */
	private Lts calculateMinimum(Lts lts) {
		this.oldLts = lts;
		Lts newLts = new Lts(oldLts.getStates(), oldLts.getActions(),
				Collections.<Transition> emptySet(), oldLts.getInitialState());

		for (Transition t : oldLts.getTransitions()) {
			if (!isRedundant(t)) {
				newLts.addTransition(t);
			}
		}
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
			//Transition(s,τ,t) redundant .⇔ ∃ r ∈ Post(s,τ), t ≠ r ≠ s Λ t ∈ Post(r,τ)
			for (State step1 : oldLts.post(source, Action.INTERNAL)) {
				if (!step1.equals(source) && !step1.equals(target)) {
					if (oldLts.post(step1, Action.INTERNAL).contains(target)) {
						return true;
					}
				}
			}
		} else {
			//Transition(s,a,t) redundant .⇔ ((∃ r ∈ Post(s,a), r ≠ t .∧ t ∈ Post(r,τ))
			//								∨ (∃ r ∈ Post(s,τ), r ≠ s .∧ t ∈ Post(r,a)))
			for (State strongTarget : oldLts.post(source, label)) {
				if (!strongTarget.equals(target)) {
					if (oldLts.post(strongTarget, Action.INTERNAL)
							.contains(target)) {
						return true;
					}
				}
			}
			for (State strongSource : oldLts.post(source, Action.INTERNAL)) {
				if (!strongSource.equals(source)) {
					if (oldLts.post(strongSource, label).contains(target)) {
						return true;
					}
				}
			}
		}

		return false;
	}
}
