package org.andy.code.misc;

public interface Identified {
	default String id() { return getClass().getSimpleName(); }
}
