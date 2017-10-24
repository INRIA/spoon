/**
 * Copyright (C) 2006-2017 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.reflect.meta.impl;

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.SpoonException;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.path.CtRole;

/**
 * common implementation of {@link RoleHandler}
 * @param <T> the type of node whose attribute has to be manipulated
 * @param <U> the type of container value of the attribute
 * @param <V> the type of item value of the attribute
 */
abstract class AbstractRoleHandler<T, U, V> implements RoleHandler {

	private final CtRole role;
	private final Class<T> targetClass;
	private final Class<V> valueClass;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected AbstractRoleHandler(CtRole role, Class<T> targetType, Class<?> valueType) {
		this.role = role;
		this.targetClass = targetType;
		this.valueClass = (Class) valueType;
	}

	@Override
	public CtRole getRole() {
		return role;
	}

	@Override
	public Class<?> getTargetType() {
		return targetClass;
	}


	@SuppressWarnings("unchecked")
	protected T castTarget(Object element) {
		return (T) element;
	}
	@SuppressWarnings("unchecked")
	protected U castValue(Object value) {
		return (U) value;
	}

	protected void checkItemsClass(Iterable<?> iterable) {
		//check that each item has expected class
		for (Object value : iterable) {
			castItemValue(value);
		}
	}
	@SuppressWarnings("unchecked")
	protected V castItemValue(Object value) {
		//check that item has expected class
		if (value != null && valueClass.isInstance(value) == false) {
			throw new ClassCastException(value.getClass().getName() + " cannot be cast to " + valueClass.getName());
		}
		return (V) value;
	}

	public <W, X> void setValue(W element, X value) {
		throw new SpoonException("Setting of CtRole." + role.name() + " is not supported for " + element.getClass().getSimpleName());
	};

	@Override
	public Class<?> getValueClass() {
		return valueClass;
	}

	public <W, X> List<X> asList(W element) {
		throw new SpoonException("The value of CtRole." + getRole().name() + " cannot be adapted to List for " + element.getClass().getSimpleName());
	};

	public <W, X> Set<X> asSet(W element) {
		throw new SpoonException("The value of CtRole." + getRole().name() + " cannot be adapted to Set for " + element.getClass().getSimpleName());
	};

	public <W, X> Map<String, X> asMap(W element) {
		throw new SpoonException("The value of CtRole." + getRole().name() + " cannot be adapted to Map for " + element.getClass().getSimpleName());
	};

//	protected abstract <V> Iterator<V> iterator(T element);
//	protected abstract int size(T element);
//	protected abstract <V> V get(T element, int index);
//	protected abstract boolean contains(T element, Object o);
//	protected abstract <V> boolean add(T element, V o);
//	protected abstract boolean remove(T element, Object o);


	abstract static class SingleHandler<T, U> extends AbstractRoleHandler<T, U, U> {

		protected SingleHandler(CtRole role, Class<T> targetType, Class<?> valueClass) {
			super(role, targetType, valueClass);
		}

		@Override
		public ContainerKind getContainerKind() {
			return ContainerKind.SINGLE;
		}

		public <W, X> java.util.Collection<X> asCollection(W element) {
			return asList(element);
		};

		public <W, X> java.util.List<X> asList(W element) {
			return Collections.<X>singletonList(getValue(element));
		};

		public <W, X> java.util.Set<X> asSet(W element) {
			return Collections.<X>singleton(getValue(element));
		};
	}

	abstract static class ListHandler<T, V> extends AbstractRoleHandler<T, List<V>, V> {

		protected ListHandler(CtRole role, Class<T> targetType, Class<?> valueClass) {
			super(role, targetType, valueClass);
		}

		@Override
		public ContainerKind getContainerKind() {
			return ContainerKind.LIST;
		}

		@Override
		protected List<V> castValue(Object value) {
			List<V> list = super.castValue(value);
			//check that each item has expected class
			checkItemsClass(list);
			return list;
		}

		public <W, X> java.util.Collection<X> asCollection(W element) {
			return asList(element);
		};

		public <W, X> java.util.List<X> asList(W e) {
			return new AbstractList<X>() {
				T element = castTarget(e);

				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public Iterator<X> iterator() {
					return (Iterator) ListHandler.this.iterator(element);
				}

				@Override
				public int size() {
					return ListHandler.this.size(element);
				}

				@SuppressWarnings("unchecked")
				@Override
				public X get(int index) {
					return (X) ListHandler.this.get(element, index);
				}
				@Override
				public boolean add(X value) {
					return ListHandler.this.add(element, castItemValue(value));
				}

				@Override
				public boolean remove(Object value) {
					return ListHandler.this.remove(element, value);
				}
			};
		}

		protected boolean remove(T element, Object value) {
			List<V> values = new ArrayList<>(this.<T, List<V>>getValue(element));
			boolean ret = values.remove(value);
			if (ret) {
				setValue(element, values);
			}
			return ret;
		}

		protected boolean add(T element, V value) {
			List<V> values = new ArrayList<>(this.<T, List<V>>getValue(element));
			boolean ret = values.add(value);
			setValue(element, values);
			return ret;
		}

		protected V get(T element, int index) {
			return this.<T, List<V>>getValue(element).get(index);
		}

		protected int size(T element) {
			return this.<T, List<V>>getValue(element).size();
		}

		protected Iterator<V> iterator(T element) {
			return this.<T, List<V>>getValue(element).iterator();
		};
	}

	abstract static class SetHandler<T, V> extends AbstractRoleHandler<T, Set<V>, V> {

		protected SetHandler(CtRole role, Class<T> targetType, Class<?> valueClass) {
			super(role, targetType, valueClass);
		}

		@Override
		public ContainerKind getContainerKind() {
			return ContainerKind.SET;
		}

		@Override
		protected Set<V> castValue(Object value) {
			Set<V> set = super.castValue(value);
			//check that each item has expected class
			checkItemsClass(set);
			return set;
		}

		public <W, X> Collection<X> asCollection(W element) {
			return asSet(element);
		};

		@Override
		public <W, X> Set<X> asSet(W e) {
			return new AbstractSet<X>() {
				T element = castTarget(e);

				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public Iterator<X> iterator() {
					return (Iterator) SetHandler.this.iterator(element);
				}

				@Override
				public int size() {
					return SetHandler.this.size(element);
				}

				@Override
				public boolean contains(Object o) {
					return SetHandler.this.contains(element, o);
				}

				@Override
				public boolean add(X value) {
					return SetHandler.this.add(element, castItemValue(value));
				}

				@Override
				public boolean remove(Object value) {
					return SetHandler.this.remove(element, value);
				}
			};
		}

		protected boolean remove(T element, Object value) {
			Set<V> values = new HashSet<>(this.<T, Set<V>>getValue(element));
			boolean ret = values.remove(value);
			if (ret) {
				setValue(element, values);
			}
			return false;
		}

		protected boolean add(T element, V value) {
			Set<V> values = new HashSet<>(this.<T, Set<V>>getValue(element));
			boolean ret = values.add(value);
			if (ret) {
				setValue(element, values);
			}
			return ret;
		}

		protected boolean contains(T element, Object o) {
			return this.<T, Set<V>>getValue(element).contains(o);
		}

		protected int size(T element) {
			return this.<T, Set<V>>getValue(element).size();
		}

		protected Iterator<V> iterator(T element) {
			return this.<T, Set<V>>getValue(element).iterator();
		}
	}

	abstract static class MapHandler<T, V> extends AbstractRoleHandler<T, Map<String, V>, V> {

		protected MapHandler(CtRole role, Class<T> targetType, Class<?> valueClass) {
			super(role, targetType, valueClass);
		}

		@Override
		public ContainerKind getContainerKind() {
			return ContainerKind.MAP;
		}

		@Override
		protected Map<String, V> castValue(Object value) {
			Map<String, V> map = super.castValue(value);
			//check that each item has expected class
			checkItemsClass(map.values());
			return map;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public <W, X> java.util.Collection<X> asCollection(W element) {
			return (Collection) asMap(element).values();
		};

		@Override
		public <W, X> Map<String, X> asMap(W e) {
			// TODO Auto-generated method stub
			return new AbstractMap<String, X>() {
				T element = castTarget(e);

				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public Set<Map.Entry<String, X>> entrySet() {
					return (Set) MapHandler.this.entrySet(element);
				}

				@SuppressWarnings("unchecked")
				@Override
				public X get(Object key) {
					return (X) MapHandler.this.get(element, key);
				}

				@SuppressWarnings("unchecked")
				@Override
				public X put(String key, X value) {
					return (X) MapHandler.this.put(element, key, castItemValue(value));
				}
			};
		}

		protected V get(T element, Object key) {
			return this.<T, Map<String, V>>getValue(element).get(key);
		}

		protected V put(T element, String key, V value) {
			return this.<T, Map<String, V>>getValue(element).put(key, value);
		}

		protected Set<Map.Entry<String, V>> entrySet(T element) {
			return this.<T, Map<String, V>>getValue(element).entrySet();
		}
	}
}
