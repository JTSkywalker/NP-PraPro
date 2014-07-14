package com.pseuco.project;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bisimulation {

	private class SplitTask implements Runnable {

		final Collection<State> targetBlock;
		final Action action;

		SplitTask(final Collection<State> targetBlock, final Action action) {
			this.targetBlock = targetBlock;
			this.action = action;
		}

		@Override
		public void run() {
			if (!splitBlocks.containsKey(targetBlock)) {
				final BlockingQueue<Collection<State>> myIn;
				final BlockingQueue<Collection<State>> myOut =
						new LinkedBlockingQueue<Collection<State>>();
				outLock.lock();
				try {
					myIn = out;
					out = myOut;
				} finally {
					outLock.unlock();
				}

				final Set<State> splitter = lts.pre(targetBlock, action);
				Collection<State> block;
				try {
					while ((block = myIn.take()) != MARKER) {
						split(block, splitter, myOut);
					}
					myOut.put(MARKER);
				} catch (final InterruptedException e) {
					return;
				}
			}

			try {
				eventQueue.put(Event.TERMINATION);
			} catch (InterruptedException e) {
				return;
			}
		}

		private void split(final Collection<State> block,
				final Set<State> splitter,
				final BlockingQueue<Collection<State>> out) throws InterruptedException {
			final Collection<State> subBlock1 = new LinkedList<State>();
			final Collection<State> subBlock2 = new LinkedList<State>();
			for (final State s : block) {
				if (splitter.contains(s)) {
					subBlock1.add(s);
				} else {
					subBlock2.add(s);
				}
			}
			if (subBlock1.isEmpty() || subBlock2.isEmpty()) {
				out.put(block);
			} else {
				splitBlocks.put(block, true);
				out.put(subBlock1);
				out.put(subBlock2);
				launchSplitJobsOnBlock(subBlock1);
				launchSplitJobsOnBlock(subBlock2);
			}
		}
	}

	private enum Event {
		START, TERMINATION
	}

	private final Collection<State> MARKER = Collections.<State> emptyList();
	private final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
	private final ExecutorService executor;
	private BlockingQueue<Collection<State>> out;
	private final Lock outLock = new ReentrantLock();
	private final BlockingQueue<Event> eventQueue =
			new LinkedBlockingQueue<Event>();
	private final ConcurrentHashMap<Collection<State>, Boolean> splitBlocks =
			new ConcurrentHashMap<Collection<State>, Boolean>();
	private final Partition partition;

	private final Lts lts;

	public Bisimulation(final Lts lts) throws InterruptedException {
		this.lts = lts;

		out = new LinkedBlockingQueue<Collection<State>>();
		out.add(lts.getStates());
		out.add(MARKER);

		executor = Executors.newFixedThreadPool(NUM_THREADS);

		try {
			launchSplitJobsOnBlock(lts.getStates());

			int activeTaskCount = 0;
			while (activeTaskCount > 0 || !eventQueue.isEmpty()) {
				switch (eventQueue.take()) {
				case START:
					activeTaskCount++;
					break;
				case TERMINATION:
					activeTaskCount--;
					break;
				}
			}
		} finally {
			executor.shutdownNow();
		}

		this.partition = outputAsPartition();
	}

	public Partition getCoarsestPartition() {
		return this.partition;
	}

	private void launchSplitJobsOnBlock(final Collection<State> targetBlock) throws InterruptedException {
		for (final Action a : lts.getActions()) {
			launchSplitJobWithAction(targetBlock, a);
		}
	}

	private void launchSplitJobWithAction(final Collection<State> targetBlock,
			final Action action) throws InterruptedException {
		eventQueue.put(Event.START);
		final Runnable task = new SplitTask(targetBlock, action);
		executor.execute(task);
	}

	private Partition outputAsPartition() {
		final Collection<Collection<State>> blocks =
				new LinkedList<Collection<State>>();
		Collection<State> block;
		try {
			while ((block = out.take()) != MARKER) {
				blocks.add(block);
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		return new Partition(blocks);
	}
}
