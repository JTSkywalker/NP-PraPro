package com.pseuco.project.test;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.pseuco.project.Action;
import com.pseuco.project.InternalReachabilityChecker;
import com.pseuco.project.Lts;
import com.pseuco.project.State;
import com.pseuco.project.Transition;

public class InternalReachabilityCheckerTest {

	private static Lts ltsA, ltsB;
	private static State s1, s2, s3, s4;
	private static Action i;
	private static InternalReachabilityChecker c1, c2;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		s1 = new State("A");
		s2 = new State("B");
		s3 = new State("C");
		s4 = new State("D");
		i = Action.INTERNAL;

		ltsA = new Lts(s1);
		ltsA.addState(s1);
		ltsA.addState(s2);
		ltsA.addState(s3);
		ltsA.addState(s4);
		ltsA.addAction(i);

		ltsA.addTransition(new Transition(s1, i, s2));
		ltsA.addTransition(new Transition(s2, i, s3));

		c1 = new InternalReachabilityChecker(ltsA);

		ltsB = new Lts(s1);
		ltsB.addState(s1);
		ltsB.addState(s2);
		ltsB.addState(s3);
		ltsB.addAction(i);

		ltsB.addTransition(new Transition(s1, i, s2));
		ltsB.addTransition(new Transition(s2, i, s3));
		ltsB.addTransition(new Transition(s3, i, s1));

		c2 = new InternalReachabilityChecker(ltsB);
	}

	@Test
	public void testTwoStep() throws InterruptedException {
		assertTrue(c1.isReachable(s1, s3));
	}

	@Test
	public void testReflexive() throws InterruptedException {
		assertTrue(c1.isReachable(s1, s1));
	}

	@Test
	public void testIsolated() throws InterruptedException {
		assertTrue(!c1.isReachable(s1, s4));
		assertTrue(!c1.isReachable(s4, s1));
	}

	@Test
	public void testBackwards() throws InterruptedException {
		assertTrue(!c1.isReachable(s2, s1));
		assertTrue(!c1.isReachable(s3, s2));
		assertTrue(!c1.isReachable(s3, s1));
	}

	@Test
	public void testCycle() throws InterruptedException {
		assertTrue(c2.isReachable(s1, s3));
		assertTrue(c2.isReachable(s2, s1));
		assertTrue(c2.isReachable(s3, s2));
	}

}
