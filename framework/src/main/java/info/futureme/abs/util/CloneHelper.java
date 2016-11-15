package info.futureme.abs.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CloneHelper {
    /**
     * Create a deep clone for any Object.
     */
    public static Object clone(Object o) {
        if (o != null) {
            /**
             * use a map to store obj-to-obj relations to prevent repeated clone
             * of the same obj reference.
             */
            Map<Object, Object> map = new HashMap<Object, Object>();
            try {
                return clone(o, map);
            } catch (Throwable e) {
            }
        }
        return null;
    }

    /**
     * Check if an Object has no need to be clone.
     */
    public static boolean isSimple(Object o) {
        final Class<?> type = o.getClass();
        return type.isPrimitive()//
                || type.equals(String.class)//
                || type.equals(Integer.class)//
                || type.equals(Boolean.class)//
                || type.equals(Float.class)//
                || type.equals(Byte.class)//
                || type.equals(Long.class)//
                || type.equals(Short.class)//
                || type.equals(Double.class)//
                || type.equals(Character.class);
    }

    private static List<Field> getAllFieads(Object o) {
        ArrayList<Field> fields = new ArrayList<Field>();
        if (o != null) {
            Class<?> type = o.getClass();
            do {
                for (Field f : type.getDeclaredFields()) {
                    fields.add(f);
                }
                type = type.getSuperclass();
            }
            while (type != null);
        }
        return fields;
    }

    private static Object clone(Object o, Map<Object, Object> map) {
        if (o == null) {
            return null;
        }

        // 1) check simple
        if (isSimple(o)) {
            return o;
        }

        // 2) check clone already
        Object newInstance = map.get(o);
        if (newInstance != null) {
            return newInstance;
        }

        // 3) check array
        final Class<?> type = o.getClass();
        if (type.isArray()) {
            return cloneArray(o, map);
        }

        // 4) create instance
        try {
            newInstance = type.newInstance();
        } catch (Exception e) {
        }
        if (newInstance != null) {
            map.put(o, newInstance);
            cloneFields(o, newInstance, map);
        }

        return newInstance;
    }

    private static Object cloneArray(Object o, Map<Object, Object> map) {
        if (o == null) {
            return null;
        }

        final int len = Array.getLength(o);
        Object array = Array.newInstance(o.getClass().getComponentType(), len);
        map.put(o, array);

        for (int i = 0; i < len; i++) {
            Array.set(array, i, clone(Array.get(o, i), map));
        }

        return array;
    }

    private static void cloneFields(Object object, Object newObject, Map<Object, Object> map) {
        for (Field f : getAllFieads(object)) {
            if (Modifier.isStatic(f.getModifiers())) {
                // skip static
                continue;
            }

            try {
                if (Modifier.isFinal(f.getModifiers())) {
                    Object finalObj = f.get(object);
                    Object newFinalObj = f.get(newObject);
                    map.put(finalObj, newFinalObj);
                    cloneFields(finalObj, newFinalObj, map);
                } else {
                    f.setAccessible(true);
                    f.set(newObject, clone(f.get(object), map));
                }
            } catch (Exception ex) {
            }
        }
    }
}
