/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jee.padlock;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import net.rwx.jee.padlock.annotations.Authorization;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.ws.rs.container.ContainerRequestContext;
import net.rwx.jee.padlock.annotations.AuthorizationParameters;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
class AuthorizationChecker {

    private final Object checker;
    private final Collection<AuthorizationParameter> parameters;
    private final ParamConverters paramConverters;

    public void check() {
        try {
            tryToCheck();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void tryToCheck() throws Exception {
        for (AuthorizationParameter parameter : parameters) {
            setValueToChecker(parameter);
        }
        invokeChecker();
    }

    private void setValueToChecker(AuthorizationParameter parameter) throws Exception {
        for (PropertyDescriptor descriptor : Introspector.getBeanInfo(checker.getClass()).getPropertyDescriptors()) {
            if (descriptor.getName().equals(parameter.getName())) {
                Method writeMethod = descriptor.getWriteMethod();
                Object paramValue = convertValue(parameter);
                writeMethod.invoke(checker, paramValue);
            }
        }
    }

    private Object convertValue(AuthorizationParameter parameter) {
        return paramConverters.convertValueToType(parameter.getValue(), parameter.getType(), new ArrayList<>());
    }

    private void invokeChecker() throws Exception {
        Method checkerMethod = checker.getClass().getMethod("authorized");
        if (checkerMethod != null) {
            if (!(boolean) checkerMethod.invoke(checker)) {
                throw new RuntimeException("NOT AUTHORIZED FROM CHECKER");
            }
        }else {
            throw new RuntimeException("No authorized method in authorization checker " + checker.getClass());
        }
    }

    public static class Builder {

        private Method authorizedMethod;
        private BeanManager beanManager;
        private ContainerRequestContext requestContext;

        private Builder() {
        }

        public Builder fromAuthorizedMethod(final Method value) {
            this.authorizedMethod = value;
            return this;
        }

        public Builder withBeanManager(final BeanManager value) {
            this.beanManager = value;
            return this;
        }

        public Builder valueFrom(final ContainerRequestContext value) {
            this.requestContext = value;
            return this;
        }

        public AuthorizationChecker build() {
            Authorization authorization = authorizedMethod.getAnnotation(Authorization.class);
            Object authorizationChecker = getReferenceToChecker(authorization);
            Collection<AuthorizationParameter> authParameters = buildAuthorizationParameters();
            ParamConverters converters = getReferenceToBean(ParamConverters.class);
            return new AuthorizationChecker(authorizationChecker, authParameters, converters);
        }

        private Object getReferenceToChecker(Authorization authorization) {
            if (authorization == null) {
                return null;
            } else {
                return getReferenceToBean(authorization.value());
            }
        }

        private <T> T getReferenceToBean(Class<T> type) {
            Bean<T> bean = (Bean<T>) beanManager.resolve(beanManager.getBeans(type));
            return (T) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));
        }

        private Collection<AuthorizationParameter> buildAuthorizationParameters() {
            Collection<AuthorizationParameter> authParameters = new ArrayList<>();
            for (net.rwx.jee.padlock.annotations.AuthorizationParameter paramAnnotation : getParameterAnnotations()) {
                authParameters.add(
                        AuthorizationParameter.builder()
                        .name(paramAnnotation.value())
                        .describeByMethod(authorizedMethod)
                        .valueFrom(requestContext)
                        .build()
                );
            }
            return authParameters;
        }

        private net.rwx.jee.padlock.annotations.AuthorizationParameter[] getParameterAnnotations() {
            AuthorizationParameters params = authorizedMethod.getAnnotation(AuthorizationParameters.class);
            if (params == null) {
                return new net.rwx.jee.padlock.annotations.AuthorizationParameter[0];
            } else {
                return params.value();
            }
        }
    }

    public static AuthorizationChecker.Builder builder() {
        return new AuthorizationChecker.Builder();
    }

    private AuthorizationChecker(final Object authorizationChecker,
            final Collection<AuthorizationParameter> parameters, final ParamConverters paramConverters) {
        this.parameters = parameters;
        this.paramConverters = paramConverters;
        if (authorizationChecker == null) {
            this.checker = this;
        } else {
            this.checker = authorizationChecker;
        }
    }

    public boolean authorized() {
        return true;
    }
}
