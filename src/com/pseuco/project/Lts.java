package com.pseuco.project;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Lts {

	private final Set<State> states = new HashSet<State>();
	private final Set<Action> actions = new HashSet<Action>();
	private final Set<Transition> transitions = new HashSet<Transition>();
	private final State initialState;
	private Map<State, Map<Action, Set<State>>> postMap =
			new HashMap<State, Map<Action, Set<State>>>();
	private Map<State, Map<Action, Set<State>>> preMap =
			new HashMap<State, Map<Action, Set<State>>>();
	private Map<State, Collection<Transition>> outTransitionsMap =
			new HashMap<State, Collection<Transition>>();

	public Lts(final Iterable<State> states, final Iterable<Action> actions,
			final Iterable<Transition> transitions, final State initialState) {
		this.initialState = initialState;

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
		states.add(s);
		outTransitionsMap.put(s, new LinkedList<Transition>());
	}

	public void addAction(Action a) {
		actions.add(a);
	}

	public void addTransition(Transition t) {
		transitions.add(t);
		addToPostMap(t);
		addToPreMap(t);
		outTransitionsMap.get(t.getSource()).add(t);
	}

	private void addToPostMap(Transition t) {
		Map<Action, Set<State>> outMap = postMap.get(t.getSource());
		if (outMap == null) {
			outMap = new HashMap<Action, Set<State>>();
			postMap.put(t.getSource(), outMap);
		}
		Set<State> targetSet = outMap.get(t.getLabel());
		if (targetSet == null) {
			targetSet = new HashSet<State>();
			outMap.put(t.getLabel(), targetSet);
		}
		targetSet.add(t.getTarget());
	}

	private void addToPreMap(Transition t) {
		Map<Action, Set<State>> inMap = preMap.get(t.getTarget());
		if (inMap == null) {
			inMap = new HashMap<Action, Set<State>>();
			preMap.put(t.getTarget(), inMap);
		}
		Set<State> sourceSet = inMap.get(t.getLabel());
		if (sourceSet == null) {
			sourceSet = new HashSet<State>();
			inMap.put(t.getLabel(), sourceSet);
		}
		sourceSet.add(t.getSource());
	}

    /**
     * returns only tau-posts, and itself
     * @param source
     * @return
     */
    public Collection<State> postTau(State source) {
    	throw new UnsupportedOperationException();
    }

    public Collection<State> preTau(State source) {
    	throw new UnsupportedOperationException();
    }

    public Lts transTauClosure() {
    	throw new UnsupportedOperationException();
    }

	public Collection<State> getStates() {
		return states;
	}

	public Collection<Action> getActions() {
		return actions;
	}

	public Collection<Transition> getTransitions() {
		return transitions;
	}

	public State getInitialState() {
		return initialState;
	}

	public Set<State> post(final State source, final Action a) {
		Map<Action, Set<State>> outMap = postMap.get(source);
		if (outMap == null) {
			return Collections.emptySet();
		}
		Set<State> targetSet = outMap.get(a);
		if (targetSet == null) {
			return Collections.emptySet();
		}
		return targetSet;
	}

	public Set<State> post(final Collection<State> sources, final Action a) {
		Set<State> result = new HashSet<State>();
		for (State s : sources) {
			result.addAll(post(s, a));
		}
		return result;
	}

	public Set<State> pre(final State target, final Action a) {
		Map<Action, Set<State>> inMap = preMap.get(target);
		if (inMap == null) {
			return Collections.emptySet();
		}
		Set<State> sourceSet = inMap.get(a);
		if (sourceSet == null) {
			return Collections.emptySet();
		}
		return sourceSet;
	}

	public Set<State> pre(final Collection<State> targets, final Action a) {
		Set<State> result = new HashSet<State>();
		for (State s : targets) {
			result.addAll(pre(s, a));
		}
		return result;
	}

	public Collection<Transition> outTransitions(State s) {
		return outTransitionsMap.get(s);
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