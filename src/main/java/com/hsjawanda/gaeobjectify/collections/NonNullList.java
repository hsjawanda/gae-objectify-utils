/**
 *
 */
package com.hsjawanda.gaeobjectify.collections;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;


/**
 * @author harsh.deep
 *
 */
public class NonNullList<E> implements List<E> {

	private static final Logger log = Logger.getLogger(NonNullList.class.getName());

	private List<E> wrapped;

	private static final int SPARE_CAPACITY = 5;

	private NonNullList(List<E> toWrap) {
		checkNotNull(toWrap);
		this.wrapped = toWrap;
	}

	public static <T> NonNullList<T> copyFrom(List<T> input) {
		if (null == input)
			return empty();
		int nulls = 0;
		for (T elem : input) {
			if (null == elem) {
				nulls++;
			}
		}
		int size = (SPARE_CAPACITY < nulls) ? input.size() - nulls + SPARE_CAPACITY : input.size();
		StringBuilder logMsg = new StringBuilder(30).append("Allocating new with: size = ")
				.append(size).append(";").append(" nulls = ").append(nulls);
		log.info(logMsg.toString());
		NonNullList<T> retList = new NonNullList<>(new ArrayList<T>(size));
		retList.addAll(input);
		return retList;
	}

	public static <T> NonNullList<T> wrap(List<T> toWrap) {
		int size = checkNotNull(toWrap).size();
		for (int i = 0; i < size; i++) {
			if (null == toWrap.get(i))
				throw new IllegalArgumentException("The list to wrap cannot contain nulls.");
		}
		return new NonNullList<>(toWrap);
	}

	public static <T> NonNullList<T> empty() {
		return new NonNullList<>(new ArrayList<T>());
	}

	public static <T> NonNullList<T> empty(int initialCapacity) {
		return new NonNullList<>(new ArrayList<T>(initialCapacity));
	}

	/**
	 * @param e
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	@Override
	public boolean add(E e) {
		if (null == e)
			return false;
		return this.wrapped.add(e);
	}

	/**
	 * @param index
	 * @param element
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	@Override
	public void add(int index, E element) {
		if (null == element)
			return;
		this.wrapped.add(index, element);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean listChanged = false;
		if (null != c) {
			for (E elem : c) {
				if (null != elem) {
					this.wrapped.add(elem);
					listChanged = true;
				}
			}
		}
		return listChanged;
	}

	/**
	 * @param index
	 * @param c
	 * @return
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		boolean listChanged = false;
		int idx = index;
		if (null != c) {
			for (E elem : c) {
				if (null != elem) {
					this.wrapped.add(idx++, elem);
					listChanged = true;
				}
			}
		}
		return listChanged;
	}

	/**
	 *
	 * @see java.util.List#clear()
	 */
	@Override
	public void clear() {
		this.wrapped.clear();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return this.wrapped.contains(o);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return this.wrapped.containsAll(c);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return this.wrapped.equals(o);
	}

	/**
	 * @param index
	 * @return
	 * @see java.util.List#get(int)
	 */
	@Override
	public E get(int index) {
		return this.wrapped.get(index);
	}

	/**
	 * @return
	 * @see java.util.List#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.wrapped.hashCode();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	@Override
	public int indexOf(Object o) {
		return this.wrapped.indexOf(o);
	}

	/**
	 * @return
	 * @see java.util.List#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.wrapped.isEmpty();
	}

	/**
	 * @return
	 * @see java.util.List#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		return this.wrapped.iterator();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	@Override
	public int lastIndexOf(Object o) {
		return this.wrapped.lastIndexOf(o);
	}

	/**
	 * @return
	 * @see java.util.List#listIterator()
	 */
	@Override
	public ListIterator<E> listIterator() {
		return this.wrapped.listIterator();
	}

	/**
	 * @param index
	 * @return
	 * @see java.util.List#listIterator(int)
	 */
	@Override
	public ListIterator<E> listIterator(int index) {
		return this.wrapped.listIterator(index);
	}

	/**
	 * @param index
	 * @return
	 * @see java.util.List#remove(int)
	 */
	@Override
	public E remove(int index) {
		return this.wrapped.remove(index);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		return this.wrapped.remove(o);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		return this.wrapped.removeAll(c);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return this.wrapped.retainAll(c);
	}

	/**
	 * @param index
	 * @param element
	 * @return
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	@Override
	public E set(int index, E element) {
		return this.wrapped.set(index, element);
	}

	/**
	 * @return
	 * @see java.util.List#size()
	 */
	@Override
	public int size() {
		return this.wrapped.size();
	}

	/**
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 * @see java.util.List#subList(int, int)
	 */
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return this.wrapped.subList(fromIndex, toIndex);
	}

	/**
	 * @return
	 * @see java.util.List#toArray()
	 */
	@Override
	public Object[] toArray() {
		return this.wrapped.toArray();
	}

	/**
	 * @param a
	 * @return
	 * @see java.util.List#toArray(java.lang.Object[])
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		return this.wrapped.toArray(a);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 30;
		StringBuilder builder = new StringBuilder();
		builder.append("NonNullList [");
		if (this.wrapped != null) {
			builder.append("wrapped=")
					.append(this.wrapped.subList(0, Math.min(this.wrapped.size(), maxLen)));
		}
		builder.append("]");
		return builder.toString();
	}

}
