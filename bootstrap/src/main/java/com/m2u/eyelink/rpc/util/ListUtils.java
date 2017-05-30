package com.m2u.eyelink.rpc.util;

import java.util.Collection;
import java.util.List;

import com.m2u.eyelink.util.CollectionUtils;


public final class ListUtils {

    private ListUtils() {
    }

    /**
     * @deprecated Since 1.6.1. Use {@link CollectionUtils#isEmpty(Collection)}
     */
    @Deprecated
    public static boolean isEmpty(List list) {
        return CollectionUtils.isEmpty(list);
    }

    public static <V> boolean addIfValueNotNull(List<V> list, V value) {
        if (value == null) {
            return false;
        }

        return list.add(value);
    }
    
    public static <V> boolean addAllIfAllValuesNotNull(List<V> list, V[] values) {
        if (values == null) {
            return false;
        }
        
        for (V value : values) {
            if (value == null) {
                return false;
            }
        }
        
        for (V value : values) {
            list.add(value);
        }
        
        return true;
    }
    
    public static <V> void addAllExceptNullValue(List<V> list, V[] values) {
        if (values == null) {
            return;
        }
        
        for (V value : values) {
            addIfValueNotNull(list, value);
        }
    }
    
    public static <V> V getFirst(List<V> list) {
        return getFirst(list, null);
    }

    public static <V> V getFirst(List<V> list, V defaultValue) {
        int size = list.size();
        
        if (size > 0) {
            return list.get(0);
        } else {
            return defaultValue;
        }
    }

    public static <V> boolean isFirst(List<V> list, V object) {
        V first = getFirst(list);
        return first.equals(object);
    }
    
    public static <V> V get(List<V> list, int index, V defaultValue) {
        try {
            return list.get(index);
        } catch (Exception ignore) {
        }

        return defaultValue;
    }

    public static <V> V getLast(List<V> list) {
        return getLast(list, null);
    }
    
    public static <V> V getLast(List<V> list, V defaultValue) {
        int size = list.size();
        
        if (size > 0) {
            return list.get(size - 1);
        } else {
            return defaultValue;
        }
    }

    public static <V> boolean isLast(List<V> list, V object) {
        V last = getLast(list);
        return last.equals(object);
    }
    
}
