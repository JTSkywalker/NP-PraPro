package com.pseuco.project;

public class State {

    private final String name;

    public State(String name) {
        this.name = name;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() == getClass()) {
            State s = (State) obj;
            if (name.equals(s.name)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return name;
    }

}
