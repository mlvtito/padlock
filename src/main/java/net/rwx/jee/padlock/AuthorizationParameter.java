/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jee.padlock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.ContainerRequestContext;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class AuthorizationParameter {

    private final String name;
    private final Class<?> type;
    private final String value;

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static class Builder {

        private String name;
        private Method resourceMethod;
        private String value;
        private Class<?> valueType;
        private ContainerRequestContext requestContext;

        private Builder() {
        }

        public Builder name(final String value) {
            this.name = value;
            return this;
        }

        public Builder describeByMethod(final Method value) {
            this.resourceMethod = value;
            return this;
        }

        public Builder valueFrom(final ContainerRequestContext value) {
            this.requestContext = value;
            return this;
        }

        public AuthorizationParameter build() {
            extractValueAndTypeFromMethodAndRequest();
            return new AuthorizationParameter(name, valueType, value);
        }

        private void extractValueAndTypeFromMethodAndRequest() {
            Annotation[][] annotations = resourceMethod.getParameterAnnotations();
            for (int iMethodParameter = 0; iMethodParameter < annotations.length; iMethodParameter++) {
                for (Annotation annotation : annotations[iMethodParameter]) {
                    extractPathParam(annotation, iMethodParameter);
                }
            }
            checkIfValueHasBeenFound();
        }

        private void checkIfValueHasBeenFound() {
            if (value == null || valueType == null) {
                throw new RuntimeException("No parameter " + name + " on resource " + resourceMethod.getName());
            }
        }

        private void extractPathParam(Annotation annotation, int iMethodParameter) {
            if (annotation instanceof PathParam && ((PathParam) annotation).value().equals(name)) {
                value = requestContext.getUriInfo().getPathParameters().getFirst(name);
                valueType = resourceMethod.getParameterTypes()[iMethodParameter];
            }
        }
    }

    public static AuthorizationParameter.Builder builder() {
        return new AuthorizationParameter.Builder();
    }

    private AuthorizationParameter(final String name, final Class<?> type, final String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }
}
