package com.davinryan.common.restservice.reflection;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * Useful library for doing reflection stuff.
 */
public class ReflectionUtils {

    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    private ReflectionUtils() {
    }

    /**
     * Invoke the getter method with the given {@code name} on the supplied
     * target object with the supplied {@code value}.
     * <p>
     * <p>This method traverses the class hierarchy in search of the desired
     * method. In addition, an attempt will be made to make non-{@code public}
     * methods <em>accessible</em>, thus allowing one to invoke {@code protected},
     * {@code private}, and <em>package-private</em> getter methods.
     * <p>
     * <p>In addition, this method supports JavaBean-style <em>property</em>
     * names. For example, if you wish to get the {@code name} property on the
     * target object, you may pass either &quot;name&quot; or
     * &quot;getName&quot; as the method name.
     *
     * @param target the target object on which to invoke the specified getter
     *               method
     * @param name   the name of the getter method to invoke or the corresponding
     *               property name
     * @return the value returned from the invocation
     * @see org.springframework.util.ReflectionUtils#findMethod(Class, String, Class[])
     * @see org.springframework.util.ReflectionUtils#makeAccessible(Method)
     * @see org.springframework.util.ReflectionUtils#invokeMethod(Method, Object, Object[])
     */
    public static Object invokeGetterMethod(Object target, String name) {
        Assert.notNull(target, "Target object must not be null");
        Assert.hasText(name, "Method name must not be empty");

        Method method = findGetterMethod(target, name);
        org.springframework.util.ReflectionUtils.makeAccessible(method);
        return org.springframework.util.ReflectionUtils.invokeMethod(method, target);
    }

    /**
     * Find a getter method for a given field.
     *
     * @param target the target object on which to invoke the specified setter
     *               method
     * @param name   the name of the setter method to invoke or the corresponding
     *               property name
     * @return getter method corresponding to name.
     */
    public static Method findGetterMethod(Object target, String name) {
        String getterMethodName = name;
        if (!name.startsWith(GETTER_PREFIX)) {
            getterMethodName = GETTER_PREFIX + StringUtils.capitalize(name);
        }
        return findMethod(target, name, getterMethodName);
    }

    /**
     * Find a setter method for a given field.
     *
     * @param target the target object on which to invoke the specified setter
     *               method
     * @param name   the name of the setter method to invoke or the corresponding
     *               property name
     * @param type   the formal parameter type declared by the setter method
     * @return setter method corresponding to name.
     */
    public static Method findSetterMethod(Object target, String name, Class<?> type) {
        String setterMethodName = name;
        if (!name.startsWith(SETTER_PREFIX)) {
            setterMethodName = SETTER_PREFIX + StringUtils.capitalize(name);
        }
        Class<?>[] paramTypes = type != null ? new Class<?>[]{type} : null;

        return findMethod(target, name, setterMethodName, paramTypes);
    }

    /**
     * Invoke the setter method with the given {@code name} on the supplied
     * target object with the supplied {@code value}.
     * <p>
     * <p>This method traverses the class hierarchy in search of the desired
     * method. In addition, an attempt will be made to make non-{@code public}
     * methods <em>accessible</em>, thus allowing one to invoke {@code protected},
     * {@code private}, and <em>package-private</em> setter methods.
     * <p>
     * <p>In addition, this method supports JavaBean-style <em>property</em>
     * names. For example, if you wish to set the {@code name} property on the
     * target object, you may pass either &quot;name&quot; or
     * &quot;setName&quot; as the method name.
     *
     * @param target the target object on which to invoke the specified setter
     *               method
     * @param name   the name of the setter method to invoke or the corresponding
     *               property name
     * @param value  the value to provide to the setter method
     * @see org.springframework.util.ReflectionUtils#findMethod(Class, String, Class[])
     * @see org.springframework.util.ReflectionUtils#makeAccessible(Method)
     * @see org.springframework.util.ReflectionUtils#invokeMethod(Method, Object, Object[])
     */
    public static void invokeSetterMethod(Object target, String name, Object value) {
        invokeSetterMethod(target, name, value, null);
    }

    /**
     * Invoke the setter method with the given {@code name} on the supplied
     * target object with the supplied {@code value}.
     * <p>
     * <p>This method traverses the class hierarchy in search of the desired
     * method. In addition, an attempt will be made to make non-{@code public}
     * methods <em>accessible</em>, thus allowing one to invoke {@code protected},
     * {@code private}, and <em>package-private</em> setter methods.
     * <p>
     * <p>In addition, this method supports JavaBean-style <em>property</em>
     * names. For example, if you wish to set the {@code name} property on the
     * target object, you may pass either &quot;name&quot; or
     * &quot;setName&quot; as the method name.
     *
     * @param target the target object on which to invoke the specified setter
     *               method
     * @param name   the name of the setter method to invoke or the corresponding
     *               property name
     * @param value  the value to provide to the setter method
     * @param type   the formal parameter type declared by the setter method
     * @see org.springframework.util.ReflectionUtils#findMethod(Class, String, Class[])
     * @see org.springframework.util.ReflectionUtils#makeAccessible(Method)
     * @see org.springframework.util.ReflectionUtils#invokeMethod(Method, Object, Object[])
     */
    public static void invokeSetterMethod(Object target, String name, Object value, Class<?> type) {
        Assert.notNull(target, "Target object must not be null");
        Assert.hasText(name, "Method name must not be empty");

        Method method = findSetterMethod(target, name, type);

        org.springframework.util.ReflectionUtils.makeAccessible(method);
        org.springframework.util.ReflectionUtils.invokeMethod(method, target, value);
    }

    /**
     * Find a method for a given field.
     *
     * @param target     the target object on which to invoke the specified setter
     *                   method
     * @param name       the corresponding property name
     * @param methodName the name of the method to invoke
     * @param paramTypes the formal parameter types declared by the method
     * @return setter method corresponding to name.
     */
    private static Method findMethod(Object target, String name, String methodName, Class<?>... paramTypes) {
        Method method = org.springframework.util.ReflectionUtils.findMethod(target.getClass(), methodName, paramTypes);
        if (method == null && !methodName.equals(name)) {
            return org.springframework.util.ReflectionUtils.findMethod(target.getClass(), name, paramTypes);
        }
        return method;
    }

    /**
     * Find a method for a given field.
     *
     * @param target     the target object on which to invoke the specified setter
     *                   method
     * @param name       the corresponding property name
     * @param methodName the name of the method to invoke
     * @return setter method corresponding to name.
     */
    private static Method findMethod(Object target, String name, String methodName) {
        Method method = org.springframework.util.ReflectionUtils.findMethod(target.getClass(), methodName);
        if (method == null && !methodName.equals(name)) {
            return org.springframework.util.ReflectionUtils.findMethod(target.getClass(), name);
        }
        return method;
    }
}
