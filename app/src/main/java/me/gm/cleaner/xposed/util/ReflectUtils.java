package me.gm.cleaner.xposed.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.gm.cleaner.xposed.XposedContext;

public class ReflectUtils extends XposedContext {
    private static final Map<String, Field> sFieldCache = new HashMap<>();

    public static Field findField(Object instance, Class<?> cls) {
        String fullFieldName = instance.getClass().getName() + '#' + cls.getName();
        if (sFieldCache.containsKey(fullFieldName)) {
            return sFieldCache.get(fullFieldName);
        }
        try {
            Field[] declaredFields = instance.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                Object objField = field.get(instance);
                if (objField != null && cls.equals(objField.getClass())) {
                    sFieldCache.put(field.getName(), field);
                    return field;
                }
            }
            for (Field field : declaredFields) {
                Object objField = field.get(instance);
                if (objField != null && cls.isAssignableFrom(objField.getClass())) {
                    sFieldCache.put(field.getName(), field);
                    return field;
                }
            }
            throw new NoSuchFieldException(fullFieldName + " field not found");
        } catch (NoSuchFieldException e) {
            throw new NoSuchFieldError(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static Object getObjectField(Object instance, Class<?> cls) {
        try {
            return findField(instance, cls).get(instance);
        } catch (IllegalAccessException e) {
            XposedBridge.log(e);
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static Object getObjectField(Object instance, String className) {
        try {
            return findField(instance, XposedHelpers.findClass(className, sClassLoader)).get(instance);
        } catch (IllegalAccessException e) {
            XposedBridge.log(e);
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static void setObjectField(Object instance, Class<?> cls, Object value) {
        try {
            findField(instance, cls).set(instance, value);
        } catch (IllegalAccessException e) {
            XposedBridge.log(e);
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static void setObjectField(Object instance, String className, Object value) {
        try {
            findField(instance, XposedHelpers.findClass(className, sClassLoader)).set(instance, value);
        } catch (IllegalAccessException e) {
            XposedBridge.log(e);
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public interface Callback {
        /**
         * @param objField the value of the represented field in object
         * @return True if no further handling is desired
         */
        boolean onFieldFound(Object objField);
    }

    public static void findField(Object instance, Class<?> cls, Callback handle) {
        try {
            Field[] declaredFields = instance.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                Object objField = field.get(instance);
                if (objField != null && cls.equals(objField.getClass())
                        && handle.onFieldFound(objField)) {
                    return;
                }
            }
            for (Field field : declaredFields) {
                Object objField = field.get(instance);
                if (objField != null && cls.isAssignableFrom(objField.getClass())
                        && handle.onFieldFound(objField)) {
                    return;
                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public static void handleObjectFields(Object instance, Class<?> cls, Callback handle) {
        findField(instance, cls, handle);
    }

    public static void handleObjectFields(Object instance, String className, Callback handle) {
        findField(instance, XposedHelpers.findClass(className, sClassLoader), handle);
    }

    public static Object callMethod(Method method, Object instance, Object... args) {
        try {
            method.setAccessible(true);
            return method.invoke(instance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            XposedBridge.log(e);
            throw new IllegalArgumentException(e);
        }
    }

    public static Object callStaticMethod(Method method, Object... args) {
        return callMethod(method, null, args);
    }
}
