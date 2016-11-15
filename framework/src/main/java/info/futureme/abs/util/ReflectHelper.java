package info.futureme.abs.util;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectHelper {
    // Create
    public static Object genericInvokMethod(Object obj, String methodName,
                                            int paramCount, Object... params) {
        Method method;
        Object requiredObj = null;
        Object[] parameters = new Object[paramCount];
        Class<?>[] classArray = new Class<?>[paramCount];
        for (int i = 0; i < paramCount; i++) {
            parameters[i] = params[i];
            classArray[i] = params[i].getClass();
        }
        try {
            method = obj.getClass().getDeclaredMethod(methodName, classArray);
            method.setAccessible(true);
            requiredObj = method.invoke(obj, params);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return requiredObj;
    }

    public static <T> T create(Class<T> cls, Object... args) {
        return create(cls, resolveArgsTypes(args), args);
    }

    public static void getFieldNamesAndRecursiveValues(final Object valueObj) throws IllegalArgumentException,
            IllegalAccessException{
        Map<String, Object> map = getFieldNamesAndValues(valueObj);
        for(Object obj : map.values()){
            Map<String, Object> map1 = getFieldNamesAndValues(obj);
            for(Object obj1 : map1.values()){
                Map<String, Object> map2 = getFieldNamesAndValues(obj1);
            }
        }
    }


    /**
     *
     * @param valueObj
     * @return
     * @since Aug 27, 2011 5:27:19 AM
     * @author Narendra
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static Map<String, Object> getFieldNamesAndValues(final Object valueObj) throws IllegalArgumentException,
            IllegalAccessException
    {
        DLog.i("Begin", "getFieldNamesAndValues");
        if(valueObj == null) {
            DLog.i("value", "null");
            return null;
        }
        Class c1 = valueObj.getClass();
        DLog.i("Class name got is", c1.getName());

        Map<String, Object> fieldMap = new HashMap<String ,Object>();
        Field[] valueObjFields = c1.getDeclaredFields();

        // compare values now
        for (int i = 0; i < valueObjFields.length; i++)
        {
            String fieldName = valueObjFields[i].getName();

            DLog.i("Getting Field Values for Field:: ", valueObjFields[i].getName());
            valueObjFields[i].setAccessible(true);

            Object newObj = valueObjFields[i].get(valueObj);

            DLog.i("Value of field" , fieldName + "newObj:" + newObj == null ? "null":newObj.getClass().getName() + ": " + newObj);
            fieldMap.put(fieldName, newObj);
        }
        DLog.i("End"," getFieldNamesAndValues");
        return fieldMap;
    }

    public static Field[] getAllFields(Class klass) {
        List<Field> fields = new ArrayList<Field>();
        fields.addAll(Arrays.asList(klass.getDeclaredFields()));
        if (klass.getSuperclass() != null) {
            fields.addAll(Arrays.asList(getAllFields(klass.getSuperclass())));
        }
        return fields.toArray(new Field[]{});
    }


    public static <T> T create(Class<T> cls, Class<?>[] types, Object... args) {
        try {
            Constructor<T> ctr = cls.getDeclaredConstructor(types);
            ctr.setAccessible(true);
            return ctr.newInstance(args);
        } catch (Throwable e) {
            Assert.r(e);
            return null;
        }
    }

    // Getter

    public static Method getMethod(Class<?> targetClass, String methodName, Class<?>... types) {
        try {
            Method method = targetClass.getDeclaredMethod(methodName, types);
            method.setAccessible(true);
            return method;
        } catch (Throwable e) {
            Assert.r(e);
            return null;
        }
    }

    public static Field getField(Class<?> targetClass, String fieldName) {
        try {
            Field field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (Throwable e) {
            return null;
        }
    }

    public static boolean setField(Object target, String fieldName, Object value){
        return setField(target, target.getClass(), fieldName, value);
    }

    public static boolean setField(Object target, Class fieldClass, String fieldName, Object value){
        try {
            Field f = fieldClass.getDeclaredField(fieldName);
            f.setAccessible(true); // solution
            f.set(target, value);
            return true;
            // production code should handle these exceptions more gracefully
        } catch (NoSuchFieldException x) {
            DLog.p(x);
        } catch (IllegalArgumentException x) {
            DLog.p(x);
        } catch (IllegalAccessException x) {
            DLog.p(x);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, String fieldName) {
        return getFieldValue(target, target.getClass(), fieldName);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, Class fieldClass, String fieldName) {
        Assert.r(target != null);
        try {
            Field field;
            field = getField(fieldClass, fieldName);
            DLog.i("field", field + "");
            if (field != null) {
                return (T) field.get(target);
            }
        } catch (Throwable ex) {
            DLog.p(ex);
        }
        return null;
    }

    // Invoke

    public static Object invokeStatic(String className, String methodName, Object... args) {
        return invokeStatic(className, methodName, resolveArgsTypes(args), args);
    }

    public static Object invokeStatic(String className, String methodName, Class<?>[] argTypes, Object... args) {
        try {
            return invokeStatic(Class.forName(className), methodName, argTypes, args);
        } catch (Throwable e) {
            Assert.r(e);
            return null;
        }
    }

    public static Object invokeStatic(Class<?> classType, String methodName, Object... args) {
        return invokeStatic(classType, methodName, resolveArgsTypes(args), args);
    }

    public static Object invokeStatic(Class<?> classType, String methodName, Class<?>[] argTypes, Object... args) {
        try {
            return invoke(null, classType, methodName, argTypes, args);
        } catch (Throwable e) {
            Assert.r(e);
            return null;
        }
    }

    public static Object invokeStatic(Method method, Object... args) {
        return invoke(null, method, args);
    }

    /**
     * @param args (Note:there must be a clear distinction between int type and
     *             float, double, etc.)
     */
    public static Object invoke(Object obj, String methodName, Object... args) {
        return invoke(obj, obj.getClass(), methodName, resolveArgsTypes(args), args);
    }

    public static Object invoke(Object obj, Method method, Object... args) {
        try {
            return method.invoke(obj, args);
        } catch (Throwable e) {
            Assert.r(e);
            return null;
        }
    }

    public static Object invoke(Object obj, Class<?> targetClass, String methodName, Class<?>[] argTypes, Object... args) {
        try {
            Method method = targetClass.getDeclaredMethod(methodName, argTypes);
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (Throwable e) {
            Assert.r(e);
            return null;
        }
    }

    /**
     * Construct a object by class name
     */
    public static Object construct(String className, Object... args) {
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (Exception e) {
            Assert.d(e);
        }
        return null;
    }

    /**
     * @return resolve primitive type for all primitive wrapper types.
     */
    public static Class<?> rawType(Class<?> type) {
        if (type.equals(Boolean.class)) {
            return boolean.class;
        } else if (type.equals(Integer.class)) {
            return int.class;
        } else if (type.equals(Float.class)) {
            return float.class;
        } else if (type.equals(Double.class)) {
            return double.class;
        } else if (type.equals(Short.class)) {
            return short.class;
        } else if (type.equals(Long.class)) {
            return long.class;
        } else if (type.equals(Byte.class)) {
            return byte.class;
        } else if (type.equals(Character.class)) {
            return char.class;
        }

        return type;
    }

    private static Class<?>[] resolveArgsTypes(Object... args) {
        Class<?>[] types = null;
        if (args != null && args.length > 0) {
            types = new Class<?>[args.length];
            for (int i = 0; i < args.length; ++i) {
                types[i] = rawType(args[i].getClass());
            }
        }
        return types;
    }
}
