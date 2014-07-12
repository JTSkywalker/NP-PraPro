package com.pseuco.project;

public class Action {
	
	public static final Action INTERNAL = new Action("τ");

    private final String name;

    public Action(String name) {
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
            Action a = (Action) obj;
            if (name.equals(a.name)) {
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
