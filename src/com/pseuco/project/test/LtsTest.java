package com.pseuco.project.test;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.pseuco.project.Action;
import com.pseuco.project.Lts;
import com.pseuco.project.State;
import com.pseuco.project.Transition;

public class LtsTest {
	
	State s1, s2, s3;
	Action a1;
	private Lts ltsA;

	@Before
	public void setUp() {
        List<State> states = new LinkedList<State>();
        s1 = new State("A");
        states.add(s1);
        s2 = new State("B");
        states.add(s2);
        s3 = new State("C");
        states.add(s3);

        List<Action> actions = new LinkedList<Action>();
        a1 = new Action("a");
        actions.add(a1);

        List<Transition> transitions = new LinkedList<Transition>();
        Transition t1 = new Transition(s1, a1, s2);
        transitions.add(t1);
        Transition t2 = new Transition(s2, a1, s3);
        transitions.add(t2);

        ltsA = new Lts(states, actions, transitions, s1);
	}

	@Test
	public void testSimplePost() {
		Set<State> e = new HashSet<State>();
		e.add(s2);
		assertEquals(ltsA.post(s1, a1), e);
	}

	@Test
	public void testSetPost() {
		Set<State> e = new HashSet<State>();
		e.add(s2);
		e.add(s3);
		Collection<State> sources = new LinkedList<State>();
		sources.add(s1);
		sources.add(s2);
		assertEquals(ltsA.post(sources, a1), e);
	}
	
	@Test
	public void testSimplePre() {
		Set<State> e = new HashSet<State>();
		e.add(s1);
		assertEquals(ltsA.pre(s2, a1), e);
	}

	@Test
	public void testSetPre() {
		Set<State> e = new HashSet<State>();
		e.add(s1);
		e.add(s2);
		Collection<State> targets = new LinkedList<State>();
		targets.add(s2);
		targets.add(s3);
		assertEquals(ltsA.pre(targets, a1), e);
	}

}
