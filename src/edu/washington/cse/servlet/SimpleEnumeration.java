package edu.washington.cse.servlet;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

public class SimpleEnumeration<T> implements Enumeration<T> {
	private final Iterator<T> it;

	public SimpleEnumeration(final Collection<T> elem) {
		this.it = elem.iterator();
	}

	@Override
	public boolean hasMoreElements() {
		return it.hasNext();
	}

	@Override
	public T nextElement() {
		return it.next();
	}
}
