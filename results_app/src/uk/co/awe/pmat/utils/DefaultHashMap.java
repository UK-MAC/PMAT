package uk.co.awe.pmat.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;

/**
 * A hash map that takes a {@code Callable} which is used to provide values when
 * no value is found for the given key.
 * 
 * @author AWE Plc copyright 2013
 * @param <T>
 *            the type of the key.
 * @param <U>
 *            the type of the values.
 */
public class DefaultHashMap<T, U> implements Map<T, U> {

	private final Map<T, U> baseMap;
	private final Creator<U> defaultCreator;

	/**
	 * A class which is used to create the map items when one does not already
	 * exist in the map.
	 * 
	 * @param <V>
	 *            the type of the items which will be created by this {@code
	 *            Creator}.
	 */
	public abstract static class Creator<V> {

		/**
		 * Create a new object of type {@code V}.
		 * 
		 * @return the new object.
		 */
		public abstract V create();

		/**
		 * Called when the map is cleared. This can be used to reset any
		 * internal state the creator may be holding.
		 */
		public void reset() {
		}
	}

	/**
	 * Create a new {@code Map} of {@code List}s which will return a new empty
	 * list whenever an key is used that is not contained in the map.
	 * 
	 * @param <T>
	 *            the key type.
	 * @param <U>
	 *            the list element type.
	 * @return a map of lists.
	 */
	public static <T, U> Map<T, List<U>> mapOfLists() {
        return new DefaultHashMap<>(new Creator<List<U>>() {
            @Override
            public List<U> create() {
                return new ArrayList<>();
            }
        });
    }

	/**
	 * Create a new {@code Map} of {@code Map}s, which will create and return a
	 * new {@code DefaultHashMap}, using the given {@code Creator}, whenever a
	 * key is used that is not contained in the map.
	 * 
	 * @param <T>
	 *            the key type.
	 * @param <U>
	 *            the contained maps key type.
	 * @param <V>
	 *            the contained maps value type.
	 * @param subCreator
	 *            the creator which will be used to create new {@code
	 *            DefaultHashMap} elements.
	 * @return the map of maps.
	 */
	public static <T, U, V> Map<T, Map<U, V>> mapOfDefaultMaps(final Creator<V> subCreator) {
        return new DefaultHashMap<>(new Creator<Map<U, V>>() {
            @Override
            public Map<U, V> create() {
                return new DefaultHashMap<>(subCreator);
            }
        });
    }

	/**
	 * Create a new {@code Map} of {@code Map}s, which will create and return a
	 * new {@code HashMap} whenever a key is used that is not contained in the
	 * map.
	 * 
	 * @param <T>
	 *            the key type.
	 * @param <U>
	 *            the contained maps key type.
	 * @param <V>
	 *            the contained maps value type.
	 * @return the map of maps.
	 */
	public static <T, U, V> Map<T, Map<U, V>> mapOfHashMaps() {
        return new DefaultHashMap<>(new Creator<Map<U, V>>() {
            @Override
            public Map<U, V> create() {
                return new HashMap<>();
            }
        });
    }

	/**
	 * Create a new {@code EnumMap} of {@code Map}s, which will create and
	 * return a new {@code HashMap} whenever a key is used that is not contained
	 * in the map.
	 * 
	 * @param <T>
	 *            the key type.
	 * @param <U>
	 *            the contained maps key type.
	 * @param <V>
	 *            the contained maps value type.
	 * @param enumClz
	 *            the class of the {@code Enum} keys.
	 * @return the map of maps.
	 */
	public static <T extends Enum<T>, U, V> Map<T, Map<U, V>> enumMapOfMaps(Class<T> enumClz) {
        return new DefaultHashMap<>(new EnumMap<T, Map<U, V>>(enumClz), new Creator<Map<U, V>>() {
            @Override
            public Map<U, V> create() {
                return new HashMap<>();
            }
        });
    }

	/**
	 * Create a {@code DefaultHashMap} by wrapping the given map. All method
	 * calls are passed onto this wrapped map, except that when no item is found
	 * on a call to {@link Map#get} a new item is created and added to the map.
	 * 
	 * @param baseMap
	 *            the map to wrap.s
	 * @param defaultCreator
	 *            the {@code Callable} to use to provide default values.
	 */
	public DefaultHashMap(Map<T, U> baseMap, Creator<U> defaultCreator) {
		this.baseMap = baseMap;
		this.defaultCreator = defaultCreator;
	}

	/**
	 * Create a new {@code DefaultHashMap}.
	 * 
	 * @param defaultCallable
	 *            the {@code Callable} to use to provide default values.
	 */
	public DefaultHashMap(Creator<U> defaultCallable) {
		this(new HashMap<T, U>(), defaultCallable);
	}

	@Override
	public int size() {
		return baseMap.size();
	}

	@Override
	public boolean isEmpty() {
		return baseMap.isEmpty();
	}

	@Override
	@SuppressWarnings("element-type-mismatch")
	public boolean containsKey(Object key) {
		return baseMap.containsKey(key);
	}

	@Override
	@SuppressWarnings("element-type-mismatch")
	public boolean containsValue(Object value) {
		return baseMap.containsValue(value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public U get(Object obj) {
		try {
			final T key = (T) obj;
			if (!baseMap.containsKey(key)) {
				baseMap.put(key, defaultCreator.create());
			}
			return baseMap.get(key);
		} catch (ClassCastException ex) {
			return null;
		}
	}

	@Override
	public U put(T key, U value) {
		return baseMap.put(key, value);
	}

	@Override
	@SuppressWarnings("element-type-mismatch")
	public U remove(Object key) {
		return baseMap.remove(key);
	}

	@Override
	public void putAll(Map<? extends T, ? extends U> map) {
		baseMap.putAll(map);
	}

	@Override
	public void clear() {
		baseMap.clear();
		defaultCreator.reset();
	}

	@Override
	public Set<T> keySet() {
		return baseMap.keySet();
	}

	@Override
	public Collection<U> values() {
		return baseMap.values();
	}

	@Override
	public Set<Entry<T, U>> entrySet() {
		return baseMap.entrySet();
	}

	@Override
	public String toString() {
		return baseMap.toString();
	}
}
