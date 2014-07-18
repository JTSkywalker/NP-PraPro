package com.pseuco.project;
/**
 * 
 * Ist Thread-Safe.
 *
 */
public class State {

    private final String name;

    public State(final String name) {
        this.name = name;
    }

    @Override
	public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() == getClass()) {
            final State s = (State) obj;
            if (name.equals(s.name)) {
                return true;
            }
        }
        return false;
    }

    @Override
	public int hashCode() {
        return name.hashCode();
    }

    @Override
	public String toString() {
        return name;
    }

}
