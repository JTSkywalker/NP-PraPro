package com.pseuco.project;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RedundantTransitionRemover {
	/**
	 * Entfernt redundante Knoten.
	 * @param lts
	 * @return
	 * @throws InterruptedException
	 */
	public static Lts call(Lts lts) throws InterruptedException {
		return new RedundantTransitionRemover().calculateMinimum(lts);
	}
	
	private class RedundancyChecker implements Runnable {
		
		public RedundancyChecker(Transition t) {
			super();
			this.t = t;
		}

		final Transition t;
		
		public boolean isRedundant() {
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

		@Override
		public void run() {
			if (!isRedundant()) {
				newLts.addTransition(t);
			}
		}
	}
	private Lts oldLts, newLts;
	private final int NUM_THREADS =
			Runtime.getRuntime().availableProcessors() + 1;
	/**
	 * @param lts
	 * @return
	 * 		lts ohne redundante Transitionen
	 * @throws InterruptedException 
	 */
	private Lts calculateMinimum(Lts lts) throws InterruptedException {
		this.oldLts = lts;
		newLts = new Lts(oldLts.getStates(), oldLts.getActions(),
				Collections.<Transition> emptySet(), oldLts.getInitialState());

		final ExecutorService executor = Executors
				.newFixedThreadPool(NUM_THREADS);
		for (Transition t : oldLts.getTransitions()) {
			executor.execute(new RedundancyChecker(t));
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			try {
				executor.awaitTermination(Long.MAX_VALUE,
						TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				executor.shutdownNow();
				throw e;
			}
		}
		return newLts;
	}
}
