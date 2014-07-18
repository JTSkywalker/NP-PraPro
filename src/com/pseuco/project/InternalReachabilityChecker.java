package com.pseuco.project;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Stellt Abfrage auf τ-Erreichbarkeit in konstanter Zeit zur Verfügung.
 * Vor der ersten Abfrage muss check() aufgerufen werden.
 */
public class InternalReachabilityChecker {

	private class FixedSourceReachabilityCheck implements Runnable {

		final State source;
		final Set<State> marked = new HashSet<State>();

		public FixedSourceReachabilityCheck(final State source) {
			this.source = source;
		}

		@Override
		public void run() {
			explore(source);
			reachabilityMap.put(source, marked);
		}

		private void explore(State s) {
			if (marked.contains(s)) {
				return;
			}

			marked.add(s);
			for (State s2 : lts.post(s, Action.INTERNAL)) {
				explore(s2);
			}
		}
	}

	private final int NUM_THREADS =
			Runtime.getRuntime().availableProcessors() + 1;
	private final Lts lts;
	private final Map<State, Set<State>> reachabilityMap =
			new ConcurrentHashMap<State, Set<State>>();

	public InternalReachabilityChecker(final Lts lts) {
		this.lts = lts;
	}

	/**
	 * Berechnet die Erreichbarkeit, damit später in konstanter Zeit darauf
	 * zugegriffen werden kann.
	 *
	 * @throws InterruptedException
	 */
	public void check() throws InterruptedException {
		final ExecutorService executor = Executors
				.newFixedThreadPool(NUM_THREADS);
		for (State s : lts.getStates()) {
			executor.execute(new FixedSourceReachabilityCheck(s));
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

	public boolean isReachable(final State source, final State target) {
		return reachabilityMap.get(source).contains(target);
	}

	public Collection<State> getReachable(final State source) {
		return reachabilityMap.get(source);
	}
}
