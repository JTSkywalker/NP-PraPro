package com.pseuco.project;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WeakLtsCalculator {
	/**
	 * Berechnet das schwache LTS zu strongLts, also ein LTS mit den
	 * gleichen Zuständen und Aktionen und allen schwachen Transitionen
	 * aus strongLts.
	 * @param strongLts
	 * @return
	 * @throws InterruptedException
	 */
	public static Lts call(final Lts strongLts)
			throws InterruptedException {
		return new WeakLtsCalculator(strongLts).calculate();
	}

	private class VisibleTransitionCalculator implements Runnable {

		private final State strongSource;

		public VisibleTransitionCalculator(final State strongSource) {
			this.strongSource = strongSource;
		}

		@Override
		public void run() {
			for (Action a : strongLts.getActions()) {
				if (a.equals(Action.INTERNAL)) {
					continue;
				}
				Set<State> targetSet = new HashSet<State>();
				for (State strongTarget : strongLts.post(strongSource, a)) {
					for (State weakTarget : weakLts.post(strongTarget,
							Action.INTERNAL)) {
						targetSet.add(weakTarget);
					}
				}
				for (State weakSource : weakLts.pre(strongSource,
						Action.INTERNAL)) {
					for (State target : targetSet) {
						weakLts.addTransition(new Transition(weakSource, a,
								target));
					}
				}
			}
		}
	}

	private class InvisibleTransitionCalculator implements Runnable {

		private final State source;

		public InvisibleTransitionCalculator(final State source) {
			this.source = source;
		}

		@Override
		public void run() {
			for (State target : reachabilityChecker.getReachable(source)) {
				weakLts.addTransition(new Transition(source, Action.INTERNAL,
						target));
			}
		}
	}

	private final int NUM_THREADS =
			Runtime.getRuntime().availableProcessors() + 1;
	final Lts strongLts, weakLts;
	InternalReachabilityChecker reachabilityChecker;

	private WeakLtsCalculator(final Lts strongLts) {
		this.strongLts = strongLts;
		weakLts = new Lts(strongLts.getStates(), strongLts.getActions(),
				Collections.<Transition> emptyList(),
				strongLts.getInitialState());
	}

	private Lts calculate() throws InterruptedException {
		calculateInvisible();
		calculateVisible();
		return weakLts;
	}

	private void calculateInvisible() throws InterruptedException {
		reachabilityChecker = new InternalReachabilityChecker(strongLts);
		reachabilityChecker.check();
		final ExecutorService executor = Executors
				.newFixedThreadPool(NUM_THREADS);
		for (State s : strongLts.getStates()) {
			executor.execute(new InvisibleTransitionCalculator(s));
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
	}

	private void calculateVisible() throws InterruptedException {
		final ExecutorService executor = Executors
				.newFixedThreadPool(NUM_THREADS);
		for (State s : strongLts.getStates()) {
			executor.execute(new VisibleTransitionCalculator(s));
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
	}
}
