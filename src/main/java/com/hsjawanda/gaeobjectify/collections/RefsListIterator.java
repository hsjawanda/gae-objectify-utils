/**
 *
 */
package com.hsjawanda.gaeobjectify.collections;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ListIterator;

import com.googlecode.objectify.Ref;
import com.hsjawanda.gaeobjectify.data.GaeDataUtil;


/**
 * @author harsh.deep
 *
 */
public class RefsListIterator<E> implements ListIterator<E> {

	private ListIterator<Ref<E>> wrapped;

	public RefsListIterator(ListIterator<Ref<E>> toWrap) {
		checkNotNull(toWrap);
		this.wrapped = toWrap;
	}

	@Override
	public void add(E arg0) {
		Ref<E> ref = GaeDataUtil.getNullableRefFromPojo(arg0);
		if (null != ref) {
			this.wrapped.add(ref);
		}
	}

	@Override
	public boolean hasNext() {
		return this.wrapped.hasNext();
	}

	@Override
	public boolean hasPrevious() {
		return this.wrapped.hasPrevious();
	}

	@Override
	public E next() {
		Ref<E> ref = this.wrapped.next();
		if (null != ref)
			return ref.get();
		return null;
	}

	@Override
	public int nextIndex() {
		return this.wrapped.nextIndex();
	}

	@Override
	public E previous() {
		Ref<E> ref = this.wrapped.previous();
		if (null != ref)
			return ref.get();
		return null;
	}

	@Override
	public int previousIndex() {
		return this.wrapped.previousIndex();
	}

	@Override
	public void remove() {
		this.wrapped.remove();
	}

	@Override
	public void set(E arg0) {
		Ref<E> ref = GaeDataUtil.getNullableRefFromPojo(arg0);
		if (null != ref) {
			this.wrapped.set(ref);
		}
	}

}
