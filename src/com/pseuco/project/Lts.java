package com.pseuco.project;

import java.util.List;


public class Lts {

    private final List<State> states;
    private final List<Action> actions;
    private final List<Transition> transitions;

    public Lts(final List<State> states, final List<Action> actions,
               final List<Transition> transitions) {
        this.states = states;
        this.actions = actions;
        this.transitions = transitions;
    }

    public List<State> post(State source) {
    	throw new UnsupportedOperationException();
    }
    
    public List<State> pre(State source) {
    	throw new UnsupportedOperationException();
    }
    /**
     * returns only tau-posts, and itself
     * @param source
     * @return
     */
    public List<State> postTau(State source) {
    	throw new UnsupportedOperationException();
    }
        
    public List<State> preTau(State source) {
    	throw new UnsupportedOperationException();
    }
    
    public Lts transTauClosure() {
    	throw new UnsupportedOperationException();
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
            if (states.equals(lts.states) && actions.equals(lts.actions) && transitions.equals(lts.transitions)) {
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