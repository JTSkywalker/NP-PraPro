package com.pseuco.project;

import java.util.Collection;
import java.util.LinkedList;

public class Block {

	static Block EMPTY = new Block();

	final Collection<State> states;

	public Block() {
		states  = new LinkedList<State>();
	}

	public Block(final Collection<State> states) {
		this.states = states;
	}

	public void add(State s) {
		states.add(s);
	}

	public Collection<State> getStates() {
		return states;
	}

	public boolean isEmpty() {
		return states.isEmpty();
	}

	public String toString() {
		return states.toString();
	}

}
