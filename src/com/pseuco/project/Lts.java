package com.pseuco.project;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Lts {

	private final List<State> states;
	private final List<Action> actions;
	private final List<Transition> transitions;
	private Map<State, Map<Action, Set<State>>> postMap
			= new HashMap<State, Map<Action, Set<State>>>();
	private Map<State, Map<Action, Set<State>>> preMap
			= new HashMap<State, Map<Action, Set<State>>>();

	public Lts(final List<State> states, final List<Action> actions,
			final List<Transition> transitions) {
		this.states = states;
		this.actions = actions;
		this.transitions = transitions;
		
		for (State s : states) {
			Map<Action, Set<State>> outMap = new HashMap<Action, Set<State>>();
			Map<Action, Set<State>> inMap = new HashMap<Action, Set<State>>();
			postMap.put(s, outMap);
			preMap.put(s, inMap);
			for (Action a : actions) {
				outMap.put(a, new HashSet<State>());
				inMap.put(a, new HashSet<State>());
			}
		}
		for (Transition t : transitions) {
			postMap.get(t.getSource()).get(t.getLabel()).add(t.getTarget());
			preMap.get(t.getTarget()).get(t.getLabel()).add(t.getSource());
		}
	}

	public Set<State> post(final State source, final Action a) {
		return postMap.get(source).get(a);
	}
	
	public Set<State> post(final Collection<State> sources, final Action a) {
		Set<State> result = new HashSet<State>();
		for (State s : sources) {
			result.addAll(post(s, a));
		}
		return result;
	}

	public Set<State> pre(final State target, final Action a) {
		return preMap.get(target).get(a);
	}
	
	public Set<State> pre(final Collection<State> targets, final Action a) {
		Set<State> result = new HashSet<State>();
		for (State s : targets) {
			result.addAll(pre(s, a));
		}
		return result;
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
		return String.format("(%s, %s, %s)", states.toString(),
				actions.toString(), transitions.toString());
	}
}