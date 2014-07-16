package com.pseuco.project;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WeakLtsCalculator {

	public static Lts call(final Lts strongLts)
			throws InterruptedException {
		return new WeakLtsCalculator(strongLts).getWeakLts();
	}

	private class WeakTransitionCalculator implements Runnable {

		private final State strongSource;

		public WeakTransitionCalculator(final State strongSource) {
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

	private final int NUM_THREADS =
			Runtime.getRuntime().availableProcessors() + 1;
	final Lts strongLts, weakLts;
	final InternalReachabilityChecker reachabilityChecker;

	private WeakLtsCalculator(final Lts strongLts) throws InterruptedException {
		this.strongLts = strongLts;
		weakLts = new Lts(strongLts.getStates(), strongLts.getActions(),
				Collections.<Transition> emptyList(),
				strongLts.getInitialState());
		reachabilityChecker = new InternalReachabilityChecker(strongLts);
		for (State source : strongLts.getStates()) {
			for (State target : reachabilityChecker.getReachable(source)) {
				weakLts.addTransition(new Transition(source, Action.INTERNAL,
						target));
			}
		}
		final ExecutorService executor = Executors
				.newFixedThreadPool(NUM_THREADS);
		for (State s : strongLts.getStates()) {
			executor.execute(new WeakTransitionCalculator(s));
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

	private Lts getWeakLts() {
		return weakLts;
	}

}
