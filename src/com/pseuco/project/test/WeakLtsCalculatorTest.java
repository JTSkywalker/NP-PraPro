package com.pseuco.project.test;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.pseuco.project.Action;
import com.pseuco.project.Lts;
import com.pseuco.project.State;
import com.pseuco.project.Transition;
import com.pseuco.project.WeakLtsCalculator;

public class WeakLtsCalculatorTest {

	private static Lts ltsA, ltsB;
	private static State s1, s2, s3;
	private static Action i, a;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		s1 = new State("A");
		s2 = new State("B");
		s3 = new State("C");
		i = Action.INTERNAL;
		a = new Action("a");

		ltsA = new Lts(s1);
		ltsA.addState(s1);
		ltsA.addState(s2);
		ltsA.addState(s3);
		ltsA.addAction(i);
		ltsA.addAction(a);

		ltsA.addTransition(new Transition(s1, i, s1));
		ltsA.addTransition(new Transition(s2, i, s2));
		ltsA.addTransition(new Transition(s3, i, s3));

		ltsA.addTransition(new Transition(s1, i, s2));
		ltsA.addTransition(new Transition(s1, i, s3));
		ltsA.addTransition(new Transition(s2, i, s3));

		ltsA.addTransition(new Transition(s1, a, s1));
		ltsA.addTransition(new Transition(s1, a, s2));
		ltsA.addTransition(new Transition(s1, a, s3));
		ltsA.addTransition(new Transition(s2, a, s1));
		ltsA.addTransition(new Transition(s2, a, s2));
		ltsA.addTransition(new Transition(s2, a, s3));
		ltsA.addTransition(new Transition(s3, a, s1));
		ltsA.addTransition(new Transition(s3, a, s2));
		ltsA.addTransition(new Transition(s3, a, s3));

		ltsB = new Lts(s1);
		ltsB.addState(s1);
		ltsB.addState(s2);
		ltsB.addState(s3);
		ltsB.addAction(i);
		ltsB.addAction(a);

		ltsB.addTransition(new Transition(s1, i, s2));
		ltsB.addTransition(new Transition(s2, i, s3));

		ltsB.addTransition(new Transition(s3, a, s1));
	}

	@Test
	public void test() throws InterruptedException {
		assertEquals(ltsA, new WeakLtsCalculator(ltsB).get());
	}
}
