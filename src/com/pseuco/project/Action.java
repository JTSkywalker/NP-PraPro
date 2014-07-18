package com.pseuco.project;
/**
 * 
 * Ist Thread-Safe. 
 *
 */
public class Action {
	
	public static final Action INTERNAL = new Action("τ");

    private final String name;

    public Action(final String name) {
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
            final Action a = (Action) obj;
            if (name.equals(a.name)) {
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
