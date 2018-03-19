/**
 *
 */
package com.hsjawanda.gaeobjectify.collections;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.annotation.Nullable;

import com.googlecode.objectify.Ref;
import com.hsjawanda.gaeobjectify.data.GaeDataUtil;


/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public class RefsList<E> implements List<E> {

	private List<Ref<E>> wrapped;

	protected RefsList(List<Ref<E>> toWrap) {
		this.wrapped = new ArrayList<>(toWrap);

	}

	public RefsList() {
		this.wrapped = new ArrayList<>();
	}

	public static <T> RefsList<T> wrap(List<Ref<T>> toWrap) {
		if (null == toWrap)
			return new RefsList<>();
		RefsList<T> refsList = new RefsList<>();
		refsList.wrapped = toWrap;
		return refsList;
	}

	public static <T> RefsList<T> wrap(Set<Ref<T>> setToWrap) {
		List<Ref<T>> toWrap = null;
		if (null != setToWrap) {
			toWrap = new ArrayList<Ref<T>>(setToWrap.size());
			for (Ref<T> ref : setToWrap) {
				if (null != ref) {
					toWrap.add(ref);
				}
			}
		}
		return wrap(toWrap);
	}

	public static <T> RefsList<T> newList(List<Ref<T>> toCopy) {
		if (null == toCopy)
			return new RefsList<>();
		return new RefsList<>(toCopy);
	}

	/**
	 * @param index
	 * @param element
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	@Override
	public void add(int index, E element) {
		this.add(index, GaeDataUtil.getNullableRefFromPojo(element));
	}

	protected boolean add(int index, Ref<E> ref) {
		if (null != ref) {
			this.wrapped.add(index, ref);
			return true;
		}
		return false;
	}

	/**
	 * @param e
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	@Override
	public boolean add(E e) {
		return this.add(GaeDataUtil.getNullableRefFromPojo(e));
	}

	protected boolean add(Ref<E> ref) {
		if (null != ref)
			return this.wrapped.add(ref);
		return false;
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		if (null == c || c.size() < 1)
			return false;
		boolean listChanged = false;
		for (E element : c) {
			listChanged |= this.add(GaeDataUtil.getNullableRefFromPojo(element));
			// if (null != ref) {
			// this.wrapped.add(ref);
			// listChanged = true;
			// }
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
		if (null == c || c.size() < 1)
			return false;
		boolean listChanged = false;
		for (E element : c) {
			listChanged |= this.add(index, GaeDataUtil.getNullableRefFromPojo(element));
			// if (null != ref) {
			// this.wrapped.add(ref);
			// listChanged = true;
			// }
		}
		return listChanged;
		// if (null == c || c.size() < 1)
		// return false;
		// List<Ref<E>> refList = new ArrayList<>(c.size());
		// for (E element : c) {
		// Ref<E> ref = GaeDataUtil.getNullableRefFromPojo(element);
		// if (null != ref) {
		// refList.add(ref);
		// }
		// }
		// return this.wrapped.addAll(index, refList);
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
		return this.wrapped.contains(GaeDataUtil.getNullableRefFromPojo(o));
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		if (null == c || c.size() < 1)
			return false;
		for (Object elem : c) {
			if (!this.contains(elem))
				return false;
		}
		return true;
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (null == o)
			return false;
		if (o instanceof List<?>) {
			List<?> other = (List<?>) o;
			if (other.size() != this.size())
				return false;
			for (int i = 0; i < other.size(); i++) {
				Object o1 = other.get(i);
				if (o1 instanceof Ref) {
					@SuppressWarnings("unchecked")
					Ref<E> o1Ref = (Ref<E>) o1;
					if (!o1Ref.equals(o1))
						return false;
				} else
					return false;
			}
		}
		return true;
	}

	/**
	 * @param index
	 * @return
	 * @see java.util.List#get(int)
	 */
	@Override
	@Nullable
	public E get(int index) {
		Ref<E> element = this.wrapped.get(index);
		return element == null ? null : element.get();
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
		return this.wrapped.indexOf(GaeDataUtil.getNullableRefFromPojo(o));
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
		return listIterator();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	@Override
	public int lastIndexOf(Object o) {
		return this.wrapped.lastIndexOf(GaeDataUtil.getNullableRefFromPojo(o));
	}

	/**
	 * @return
	 * @see java.util.List#listIterator()
	 */
	@Override
	public ListIterator<E> listIterator() {
		return new RefsListIterator<>(this.wrapped.listIterator());
	}

	/**
	 * @param index
	 * @return
	 * @see java.util.List#listIterator(int)
	 */
	@Override
	public ListIterator<E> listIterator(int index) {
		return new RefsListIterator<>(this.wrapped.listIterator(index));
	}

	/**
	 * @param index
	 * @return
	 * @see java.util.List#remove(int)
	 */
	@Override
	public E remove(int index) {
		Ref<E> ref = this.wrapped.remove(index);
		return null != ref ? ref.get() : null;
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		return this.wrapped.remove(GaeDataUtil.getNullableRefFromPojo(o));
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		if (null == c)
			return false;
		boolean listChanged = false;
		for (Object elem : c) {
			listChanged |= this.remove(elem);
		}
		return listChanged;
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		if (null == c)
			return false;
		boolean listChanged = false;
		Iterator<Ref<E>> iter = this.wrapped.iterator();
		while (iter.hasNext()) {
			Ref<E> ref = iter.next();
			if (!c.contains(ref.get())) {
				iter.remove();
				listChanged = true;
			}
		}
		return listChanged;
	}

	/**
	 * @param index
	 * @param element
	 * @return <code>null</code> if a valid <code>Ref</code> couldn't be created for
	 *         <code>element</code>, the previous element otherwise.
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	@Override
	public E set(int index, E element) {
		return this.set(index, GaeDataUtil.getNullableRefFromPojo(checkNotNull(element)));
	}

	protected E set(int index, Ref<E> ref) {
		checkArgument(null != ref, "Couldn't create a valid ref for element.");
		return this.wrapped.set(index, ref).get();
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
		return new RefsList<E>(this.wrapped.subList(fromIndex, toIndex));
	}

	/**
	 * @return
	 * @see java.util.List#toArray()
	 */
	@Override
	public Object[] toArray() {
		Object[] arr = new Object[this.wrapped.size()];
		int index = 0;
		for (Ref<E> ref : this.wrapped) {
			arr[index++] = ref.get();
		}
		return arr;
	}

	/**
	 * @param a
	 * @return
	 * @see java.util.List#toArray(java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < this.wrapped.size()) {
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), this.wrapped.size());
		}
		Object[] arr = a;
		int index = 0;
		for (Ref<E> ref : this.wrapped) {
			arr[index++] = ref.get();
		}

		if (a.length > this.wrapped.size()) {
			a[this.wrapped.size()] = null;
		}

		return a;
	}
}
