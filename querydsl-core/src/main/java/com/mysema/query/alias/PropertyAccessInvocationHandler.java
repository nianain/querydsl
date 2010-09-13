/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query.alias;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.lang.StringUtils;

import com.mysema.query.types.Expression;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.expr.CollectionExpression;
import com.mysema.query.types.expr.MapExpression;
import com.mysema.query.types.path.*;
import com.mysema.util.ReflectionUtils;

/**
 * PropertyAccessInvocationHandler is the main InvocationHandler class for the
 * CGLIB alias proxies
 *
 * @author tiwe
 * @version $Id$
 */
class PropertyAccessInvocationHandler implements MethodInterceptor {

    private static final int RETURN_VALUE = 42;

    private final Expression<?> hostExpression;

    private final AliasFactory aliasFactory;

    private final Map<Object, Expression<?>> propToExpr = new HashMap<Object, Expression<?>>();

    private final Map<Object, Object> propToObj = new HashMap<Object, Object>();

    public PropertyAccessInvocationHandler(Expression<?> host, AliasFactory aliasFactory) {
        this.hostExpression = host;
        this.aliasFactory = aliasFactory;
    }

    //CHECKSTYLE:OFF
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
    //CHECKSTYLE:ON
        Object rv = null;

        MethodType methodType = MethodType.get(method);

        if (methodType == MethodType.GETTER) {
            String ptyName = propertyNameForGetter(method);
            Class<?> ptyClass = method.getReturnType();
            Type genericType = method.getGenericReturnType();

            if (propToObj.containsKey(ptyName)) {
                rv = propToObj.get(ptyName);
            } else {
                PathMetadata<String> pm = PathMetadataFactory.forProperty((Path<?>) hostExpression, ptyName);
                rv = newInstance(ptyClass, genericType, proxy, ptyName, pm);
            }
            aliasFactory.setCurrent(propToExpr.get(ptyName));
            
        } else if (methodType == MethodType.SCALA_GETTER) {
            String ptyName = method.getName();
            Class<?> ptyClass = method.getReturnType();
            Type genericType = method.getGenericReturnType();

            if (propToObj.containsKey(ptyName)) {
                rv = propToObj.get(ptyName);
            } else {
                PathMetadata<String> pm = PathMetadataFactory.forProperty((Path<?>) hostExpression, ptyName);
                rv = newInstance(ptyClass, genericType, proxy, ptyName, pm);
            }
            aliasFactory.setCurrent(propToExpr.get(ptyName));

        } else if (methodType == MethodType.LIST_ACCESS) {
            // TODO : manage cases where the argument is based on a property invocation
            Object propKey = Arrays.asList(MethodType.LIST_ACCESS, args[0]);
            if (propToObj.containsKey(propKey)) {
                rv = propToObj.get(propKey);
            } else {
                PathMetadata<Integer> pm = PathMetadataFactory.forListAccess((ListPath<?, ?>) hostExpression, (Integer) args[0]);
                Class<?> elementType = ((CollectionExpression<?,?>) hostExpression).getElementType();
                rv = newInstance(elementType, elementType, proxy, propKey, pm);
            }
            aliasFactory.setCurrent(propToExpr.get(propKey));

        } else if (methodType == MethodType.MAP_ACCESS) {
            Object propKey = Arrays.asList(MethodType.MAP_ACCESS, args[0]);
            if (propToObj.containsKey(propKey)) {
                rv = propToObj.get(propKey);
            } else {
                PathMetadata<?> pm = PathMetadataFactory.forMapAccess((MapPath<?, ?, ?>) hostExpression, args[0]);
                Class<?> valueType = ((MapExpression<?, ?>) hostExpression).getValueType();
                rv = newInstance(valueType, valueType, proxy, propKey, pm);
            }
            aliasFactory.setCurrent(propToExpr.get(propKey));

        } else if (methodType == MethodType.TO_STRING) {
            rv = hostExpression.toString();

        } else if (methodType == MethodType.HASH_CODE) {
            rv = hostExpression.hashCode();

        } else if (methodType == MethodType.GET_MAPPED_PATH) {
            rv = hostExpression;

        } else {
            throw new IllegalArgumentException("Invocation of " + method.getName() + " not supported");
        }
        return rv;
    }

    @SuppressWarnings({ "unchecked"})
    @Nullable
    private <T> T newInstance(Class<T> type, Type genericType, Object parent, Object propKey, PathMetadata<?> pm) {
        Expression<?> path;
        Object rv;

        if (String.class.equals(type)) {
            path = new StringPath(pm);
            // null is used as a return value to block method invocations on Strings
            rv = null;

        } else if (Integer.class.equals(type) || int.class.equals(type)) {
            path = new NumberPath<Integer>(Integer.class, pm);
            rv = Integer.valueOf(RETURN_VALUE);

        } else if (Byte.class.equals(type) || byte.class.equals(type)) {
            path = new NumberPath<Byte>(Byte.class, pm);
            rv = Byte.valueOf((byte)RETURN_VALUE);

        } else if (java.util.Date.class.equals(type)) {
            path = new DateTimePath<Date>(Date.class, pm);
            rv = new Date();

        } else if (java.sql.Timestamp.class.equals(type)) {
            path = new DateTimePath<Timestamp>(Timestamp.class, pm);
            rv = new Timestamp(System.currentTimeMillis());

        } else if (java.sql.Date.class.equals(type)) {
            path = new DatePath<java.sql.Date>(java.sql.Date.class, pm);
            rv = new java.sql.Date(System.currentTimeMillis());

        } else if (java.sql.Time.class.equals(type)) {
            path = new TimePath<java.sql.Time>(java.sql.Time.class, pm);
            rv = new java.sql.Time(System.currentTimeMillis());

        } else if (Long.class.equals(type) || long.class.equals(type)) {
            path = new NumberPath<Long>(Long.class, pm);
            rv = Long.valueOf(RETURN_VALUE);

        } else if (Short.class.equals(type) || short.class.equals(type)) {
            path = new NumberPath<Short>(Short.class, pm);
            rv = Short.valueOf((short) RETURN_VALUE);

        } else if (Double.class.equals(type) || double.class.equals(type)) {
            path = new NumberPath<Double>(Double.class, pm);
            rv = Double.valueOf(RETURN_VALUE);

        } else if (Float.class.equals(type) || float.class.equals(type)) {
            path = new NumberPath<Float>(Float.class, pm);
            rv = Float.valueOf(RETURN_VALUE);

        } else if (BigInteger.class.equals(type)) {
            path = new NumberPath<BigInteger>(BigInteger.class, pm);
            rv = BigInteger.valueOf(RETURN_VALUE);

        } else if (BigDecimal.class.equals(type)) {
            path = new NumberPath<BigDecimal>(BigDecimal.class, pm);
            rv = BigDecimal.valueOf(RETURN_VALUE);

        } else if (Boolean.class.equals(type) || boolean.class.equals(type)) {
            path = new BooleanPath(pm);
            rv = Boolean.TRUE;

        } else if (List.class.isAssignableFrom(type)) {
            Class<Object> elementType = (Class)ReflectionUtils.getTypeParameter(genericType, 0);
            path = new ListPath<Object,EntityPathBase<Object>>(elementType, (Class)EntityPathBase.class, pm);
            rv = aliasFactory.createAliasForProperty(type, parent, path);

        } else if (Set.class.isAssignableFrom(type)) {
            Class<?> elementType = ReflectionUtils.getTypeParameter(genericType, 0);
            path = new SetPath(elementType, elementType.getName(), pm);
            rv = aliasFactory.createAliasForProperty(type, parent, path);

        } else if (Collection.class.isAssignableFrom(type)) {
            Class<?> elementType = ReflectionUtils.getTypeParameter(genericType, 0);
            path = new CollectionPath(elementType, elementType.getSimpleName(), pm);
            rv = aliasFactory.createAliasForProperty(type, parent, path);

        } else if (Map.class.isAssignableFrom(type)) {
            Class<Object> keyType = (Class)ReflectionUtils.getTypeParameter(genericType, 0);
            Class<Object> valueType = (Class)ReflectionUtils.getTypeParameter(genericType, 1);
            path = new MapPath<Object,Object,EntityPathBase<Object>>(keyType, valueType, (Class)EntityPathBase.class, pm);
            rv = aliasFactory.createAliasForProperty(type, parent, path);

        } else if (Enum.class.isAssignableFrom(type)) {
            path = new EnumPath(type, pm);
            rv = type.getEnumConstants()[0];

        } else if (type.isArray()){
            path = new ArrayPath(type, pm);
            rv = Array.newInstance(type.getComponentType(), 5);

        } else {
            if (Comparable.class.isAssignableFrom(type)){
                path = new ComparablePath(type, pm);                
                
            }else{
                path = new EntityPathBase<T>((Class<T>) type, pm);
            }
            if (!Modifier.isFinal(type.getModifiers())){
                rv = aliasFactory.createAliasForProperty(type, parent, path);
            }else{
                rv = null;
            }
        }
        propToObj.put(propKey, rv);
        propToExpr.put(propKey, path);
        return (T) rv;
    }

    private String propertyNameForGetter(Method method) {
        String name = method.getName();
        name = name.startsWith("is") ? name.substring(2) : name.substring(3);
        return StringUtils.uncapitalize(name);
    }

}
