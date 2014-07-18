package com.pseuco.project;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stellt ein veränderliches Labelled Transitions System dar ermöglicht die Operationen
 * adddState, addAction und addTransition sowie post(State source, Action a) und
 * pre(State source, Action a) in konstanter Zeit.
 * Ist Thread-Safe.
 */
public class Lts {
	
	/*
	 * Der Datentyp ConcurrentHashMap<T, Boolean> ist ein Workaround für ein in Java nicht
	 * vorimplementiertes "ConcurrentHashSet<T>": Der Boolean-Wert ist in jedem Fall irrelevant. 
	 */
	private final ConcurrentHashMap<State, Boolean> states =
			new ConcurrentHashMap<State, Boolean>();
	private final ConcurrentHashMap<Action, Boolean> actions =
			new ConcurrentHashMap<Action, Boolean>();
	private final ConcurrentHashMap<Transition, Boolean> transitions =
			new ConcurrentHashMap<Transition, Boolean>();
	private final State initialState;
	private ConcurrentHashMap<Tupel<State, Action>,
		ConcurrentHashMap<State, Boolean>> postMap = new ConcurrentHashMap<
		Tupel<State, Action>, ConcurrentHashMap<State, Boolean>>();
	private ConcurrentHashMap<Tupel<State, Action>,
	ConcurrentHashMap<State, Boolean>>
		preMap = new ConcurrentHashMap<Tupel<State, Action>,
		ConcurrentHashMap<State, Boolean>>();
	private ConcurrentHashMap<State, ConcurrentHashMap<Transition, Boolean>>
		outTransitionsMap = new ConcurrentHashMap<State,
		ConcurrentHashMap<Transition, Boolean>>();

	public Lts(State initialState) {
		this.initialState = initialState;
		addState(initialState);
	}

	public Lts(final Iterable<State> states, final Iterable<Action> actions,
			final Iterable<Transition> transitions, final State initialState) {
		this.initialState = initialState;
		addState(initialState);

		for (State s : states) {
			addState(s);
		}
		for (Action a : actions) {
			addAction(a);
		}
		for (Transition t : transitions) {
			addTransition(t);
		}
	}

	public void addState(State s) {
		states.put(s, true);
	}

	public void addAction(Action a) {
		actions.put(a, true);
	}

	public void addTransition(Transition t) {
		transitions.put(t, true);
		addToPostMap(t);
		addToPreMap(t);
		addToOutTransitionsMap(t);
	}

	private void addToPostMap(Transition t) {
		Tupel<State, Action> tupel = new Tupel<State, Action>(t.getSource(),
				t.getLabel());
		postMap.putIfAbsent(tupel, new ConcurrentHashMap<State, Boolean>());
		ConcurrentHashMap<State, Boolean> targetSet = postMap.get(tupel);
		targetSet.put(t.getTarget(), true);
	}

	private void addToPreMap(Transition t) {
		Tupel<State, Action> tupel = new Tupel<State, Action>(t.getTarget(),
				t.getLabel());
		preMap.putIfAbsent(tupel, new ConcurrentHashMap<State, Boolean>());
		ConcurrentHashMap<State, Boolean> sourceSet = preMap.get(tupel);
		sourceSet.put(t.getSource(), true);
	}

	private void addToOutTransitionsMap(Transition t) {
		outTransitionsMap.putIfAbsent(t.getSource(),
						new ConcurrentHashMap<Transition, Boolean>());
		ConcurrentHashMap<Transition, Boolean> transitions =
				outTransitionsMap.get(t.getSource());
		transitions.put(t, true);
	}

	public Collection<State> getStates() {
		return states.keySet();
	}

	public Collection<Action> getActions() {
		return actions.keySet();
	}

	public Collection<Transition> getTransitions() {
		return transitions.keySet();
	}

	public State getInitialState() {
		return initialState;
	}

	public Set<State> post(final State source, final Action a) {
		ConcurrentHashMap<State, Boolean> targetSet =
				postMap.get(new Tupel<State, Action>(source, a));
		if (targetSet == null) {
			return Collections.emptySet();
		}
		return targetSet.keySet();
	}

	public Set<State> post(final Collection<State> sources, final Action a) {
		Set<State> result = new HashSet<State>();
		for (State s : sources) {
			result.addAll(post(s, a));
		}
		return result;
	}

	public Set<State> pre(final State target, final Action a) {
		ConcurrentHashMap<State, Boolean> sourceSet =
				preMap.get(new Tupel<State, Action>(target, a));
		if (sourceSet == null) {
			return Collections.emptySet();
		}
		return sourceSet.keySet();
	}

	public Set<State> pre(final Collection<State> targets, final Action a) {
		Set<State> result = new HashSet<State>();
		for (State s : targets) {
			result.addAll(pre(s, a));
		}
		return result;
	}

	public Collection<Transition> outTransitions(State s) {
		ConcurrentHashMap<Transition, Boolean> outTransitions =
				outTransitionsMap.get(s);
		if (outTransitions == null) {
			return Collections.emptySet();
		}
		return outTransitions.keySet();
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() == getClass()) {
			Lts lts = (Lts) obj;
			if (states.equals(lts.states) && actions.equals(lts.actions)
					&& transitions.equals(lts.transitions)) {
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		int hash = 17;
		int multi = 31;
		hash += states.hashCode();
		hash = hash * multi + actions.hashCode();
		hash = hash * multi + transitions.hashCode();
		return hash;
	}

	public String toString() {
		return String.format("(%s, %s, %s)", states.keySet().toString(),
				actions.keySet().toString(), transitions.keySet().toString());
	}
}

class Tupel<A, B> {

	private final A a;
	private final B b;

	public Tupel(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() == getClass()) {
			@SuppressWarnings("unchecked")
			Tupel<A, B> other = (Tupel<A, B>) obj;
			if (a.equals(other.a) && b.equals(other.b)) {
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		int hash = 17;
		int multi = 31;
		hash += a.hashCode();
		hash = hash * multi + b.hashCode();
		return hash;
	}

	public String toString() {
		return "(" + a.toString() + ", " + b.toString() + ")";
	}

}