package com.pseuco.project;

import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class WeakLtsCalculator {

	public static Lts call(final Lts strongLts)
			throws InterruptedException {
		return new WeakLtsCalculator(strongLts).getWeakLts();
	}

	private class WeakTransitionCalculator implements Runnable {

		private final State source;

		public WeakTransitionCalculator(final State source) {
			this.source = source;
		}

		@Override
		public void run() {
			for (State target : reachabilityChecker.getReachable(source)) {
				transitions
						.add(new Transition(source, Action.INTERNAL, target));
			}
			for (State step1 : reachabilityChecker.getReachable(source)) {
				for (Transition t : strongLts.outTransitions(step1)) {
					if (!t.getLabel().equals(Action.INTERNAL)) {
						for (State step3 : reachabilityChecker.getReachable(t
								.getTarget())) {
							transitions.add(new Transition(source,
									t.getLabel(), step3));
						}
					}
				}
			}
		}
	}

	private static final int NUM_THREADS =
			Runtime.getRuntime().availableProcessors();
	final Lts strongLts, weakLts;
	final BlockingQueue<Transition> transitions =
			new LinkedBlockingQueue<Transition>();
	final InternalReachabilityChecker reachabilityChecker;

	private WeakLtsCalculator(final Lts strongLts) throws InterruptedException {
		this.strongLts = strongLts;
		weakLts = new Lts(strongLts.getStates(), strongLts.getActions(),
				Collections.<Transition> emptyList(),
				strongLts.getInitialState());
		reachabilityChecker = new InternalReachabilityChecker(strongLts);
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
		while (!transitions.isEmpty()) {
			weakLts.addTransition(transitions.take());
		}
	}

	private Lts getWeakLts() {
		return weakLts;
	}

}
