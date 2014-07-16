package com.pseuco.project;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Lts {

	private final ConcurrentHashMap<State, Boolean> states =
			new ConcurrentHashMap<State, Boolean>();
	private final ConcurrentHashMap<Action, Boolean> actions =
			new ConcurrentHashMap<Action, Boolean>();
	private final ConcurrentHashMap<Transition, Boolean> transitions =
			new ConcurrentHashMap<Transition, Boolean>();
	private final State initialState;
	private ConcurrentHashMap<State, ConcurrentHashMap<Action,
		ConcurrentHashMap<State, Boolean>>> postMap = new ConcurrentHashMap<
		State, ConcurrentHashMap<Action, ConcurrentHashMap<State, Boolean>>>();
	private ConcurrentHashMap<State, ConcurrentHashMap<Action,
	ConcurrentHashMap<State, Boolean>>>
		preMap = new ConcurrentHashMap<State, ConcurrentHashMap<Action,
		ConcurrentHashMap<State, Boolean>>>();
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
		postMap.putIfAbsent(t.getSource(), new ConcurrentHashMap<Action,
				ConcurrentHashMap<State, Boolean>>());
		ConcurrentHashMap<Action, ConcurrentHashMap<State, Boolean>> outMap =
				postMap.get(t.getSource());
		outMap.putIfAbsent(t.getLabel(),
				new ConcurrentHashMap<State, Boolean>());
		ConcurrentHashMap<State, Boolean> targetSet = outMap.get(t.getLabel());
		targetSet.put(t.getTarget(), true);
	}

	private void addToPreMap(Transition t) {
		preMap.putIfAbsent(t.getTarget(), new ConcurrentHashMap<Action,
				ConcurrentHashMap<State, Boolean>>());
		ConcurrentHashMap<Action, ConcurrentHashMap<State, Boolean>> inMap =
				preMap.get(t.getTarget());
		inMap.putIfAbsent(t.getLabel(),
				new ConcurrentHashMap<State, Boolean>());
		ConcurrentHashMap<State, Boolean> sourceSet = inMap.get(t.getLabel());
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
		ConcurrentHashMap<Action, ConcurrentHashMap<State, Boolean>> outMap =
				postMap.get(source);
		if (outMap == null) {
			return Collections.emptySet();
		}
		ConcurrentHashMap<State, Boolean> targetSet = outMap.get(a);
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
		ConcurrentHashMap<Action, ConcurrentHashMap<State, Boolean>> inMap =
				preMap.get(target);
		if (inMap == null) {
			return Collections.emptySet();
		}
		ConcurrentHashMap<State, Boolean> sourceSet = inMap.get(a);
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