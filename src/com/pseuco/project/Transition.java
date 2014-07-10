package com.pseuco.project;

public class Transition {

	private State source;
	private Action label;
	private State target;

	public Transition(final State source, final Action label, final State target) {
		this.source = source;
		this.label = label;
		this.target = target;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() == getClass()) {
			Transition t = (Transition) obj;
			if (source.equals(t.source) && label.equals(t.label)
					&& target.equals(t.target)) {
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		int hash = 17;
		int multi = 31;
		hash += source.hashCode();
		hash = hash * multi + label.hashCode();
		hash = hash * multi + target.hashCode();
		return hash;
	}

	public String toString() {
		return String.format("(%s, %s, %s)", source.toString(),
				label.toString(), target.toString());
	}

}
