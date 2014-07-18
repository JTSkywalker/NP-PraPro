package com.pseuco.project;
/**
 * Ist Thread-Safe.
 */
public class Transition {

	private final State source;
	private final Action label;
	private final State target;

	public Transition(final State source, final Action label, final State target) {
		this.source = source;
		this.label = label;
		this.target = target;
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
			final Transition t = (Transition) obj;
			if (source.equals(t.source) && label.equals(t.label)
					&& target.equals(t.target)) {
				return true;
			}
		}
		return false;
	}

	public Action getLabel() {
		return label;
	}

	public State getSource() {
		return source;
	}

	public State getTarget() {
		return target;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		final int multi = 31;
		hash += source.hashCode();
		hash = hash * multi + label.hashCode();
		hash = hash * multi + target.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		return String.format("(%s, %s, %s)", source.toString(),
				label.toString(), target.toString());
	}

}
