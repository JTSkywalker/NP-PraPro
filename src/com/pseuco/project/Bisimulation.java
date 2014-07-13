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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bisimulation {

	class SplitTask implements Runnable {

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
					e.printStackTrace();
				}
			}

			taskCountLock.lock();
			try {
				activeTaskCount--;
				taskCountChanged.signal();
			} finally {
				taskCountLock.unlock();
			}
		}

		private void split(final Collection<State> block,
				final Set<State> splitter,
				final BlockingQueue<Collection<State>> out) {
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
				try {
					out.put(block);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				splitBlocks.put(block, true);
				try {
					out.put(subBlock1);
					out.put(subBlock2);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
				launchSplitJobsOnBlock(subBlock1);
				launchSplitJobsOnBlock(subBlock2);
			}
		}
	}

	private final Collection<State> MARKER = Collections.<State> emptyList();
	private final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
	private final ExecutorService executor;
	private BlockingQueue<Collection<State>> out;
	private final Lock outLock = new ReentrantLock();
	private int activeTaskCount = 0;
	private final Lock taskCountLock = new ReentrantLock();
	private final Condition taskCountChanged = taskCountLock.newCondition();
	private final ConcurrentHashMap<Collection<State>, Boolean> splitBlocks =
			new ConcurrentHashMap<Collection<State>, Boolean>();

	private final Lts lts;

	public Bisimulation(final Lts lts) {
		this.lts = lts;
		executor = Executors.newFixedThreadPool(NUM_THREADS);
	}

	public Partition calculateCoarsestPartition() {
		splitBlocks.clear();

		out = new LinkedBlockingQueue<Collection<State>>();
		out.add(lts.getStates());
		out.add(MARKER);

		launchSplitJobsOnBlock(lts.getStates());

		taskCountLock.lock();
		try {
			while (activeTaskCount > 0) {
				try {
					taskCountChanged.await();
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		} finally {
			taskCountLock.unlock();
		}

		return outputAsPartition();
	}

	private void launchSplitJobsOnBlock(final Collection<State> targetBlock) {
		for (final Action a : lts.getActions()) {
			launchSplitJobWithAction(targetBlock, a);
		}
	}

	private void launchSplitJobWithAction(final Collection<State> targetBlock,
			final Action action) {
		taskCountLock.lock();
		try {
			activeTaskCount++;
		} finally {
			taskCountLock.unlock();
		}

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
