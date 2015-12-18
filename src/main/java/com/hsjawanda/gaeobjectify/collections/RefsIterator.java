/**
 *
 */
package com.hsjawanda.gaeobjectify.collections;

import java.util.Iterator;

import com.googlecode.objectify.Ref;


/**
 * @author harsh.deep
 * @param <E>
 *
 */
public class RefsIterator<E> implements Iterator<E> {

	private Iterator<Ref<E>> wrapped;

	public RefsIterator(Iterator<Ref<E>> toWrap) {
		this.wrapped = toWrap;
	}

	@Override
	public boolean hasNext() {
		return this.wrapped.hasNext();
	}

	@Override
	public E next() {
		return this.wrapped.next().get();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("This Iterator doesn't support removal.");
	}

}
