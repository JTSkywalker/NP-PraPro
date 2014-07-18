package com.pseuco.project;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BisimulationCalculator {

	public static Partition call(final Lts lts)
			throws InterruptedException {
		return new BisimulationCalculator().calculateCoarsestPartition(lts);
	}
	
	private class SplitTask implements Runnable {

		final Block targetBlock;
		final Action action;

		SplitTask(final Block targetBlock, final Action action) {
			this.targetBlock = targetBlock;
			this.action = action;
		}

		@Override
		public void run() {
			if (!splitBlocks.containsKey(targetBlock)) {
				final BlockingQueue<Block> myIn;
				final BlockingQueue<Block> myOut =
						new LinkedBlockingQueue<Block>();
				outLock.lock();
				try {
					myIn = out;
					out = myOut;
				} finally {
					outLock.unlock();
				}

				final Set<State> splitter = lts.pre(targetBlock.getStates(),
						action);
				Block block;
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

		private void split(final Block block, final Set<State> splitter,
				final BlockingQueue<Block> out) throws InterruptedException {
			final Block subBlock1 = new Block();
			final Block subBlock2 = new Block();
			for (final State s : block.getStates()) {
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

	private final Block MARKER = Block.EMPTY;
	private final int NUM_THREADS =
			Runtime.getRuntime().availableProcessors() + 1;
	private ExecutorService executor;
	private BlockingQueue<Block> out;
	private final Lock outLock = new ReentrantLock();
	private final BlockingQueue<Event> eventQueue =
			new LinkedBlockingQueue<Event>();
	private final ConcurrentHashMap<Block, Boolean> splitBlocks =
			new ConcurrentHashMap<Block, Boolean>();

	private Lts lts;


	public Partition calculateCoarsestPartition(final Lts lts) throws InterruptedException {
		this.lts = lts;
		executor = Executors.newFixedThreadPool(NUM_THREADS);
		out = new LinkedBlockingQueue<Block>();
		out.add(new Block(lts.getStates()));
		out.add(MARKER);

		try {
			launchSplitJobsOnBlock(new Block(lts.getStates()));
			waitForTermination();
		} finally {
			executor.shutdownNow();
		}

		return outputAsPartition();
	}

	private void waitForTermination() throws InterruptedException {
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
	}

	private void launchSplitJobsOnBlock(final Block targetBlock)
			throws InterruptedException {
		for (final Action a : lts.getActions()) {
			launchSplitJobWithAction(targetBlock, a);
		}
	}

	private void launchSplitJobWithAction(final Block targetBlock,
			final Action action) throws InterruptedException {
		eventQueue.put(Event.START);
		final Runnable task = new SplitTask(targetBlock, action);
		executor.execute(task);
	}

	private Partition outputAsPartition() throws InterruptedException {
		final Collection<Block> blocks = new LinkedList<Block>();
		Block block;
		while ((block = out.take()) != MARKER) {
			blocks.add(block);
		}
		return new Partition(blocks);
	}
}
