/*
 * Copyright 2017 Arnaud Fonce <arnaud.fonce@r-w-x.net>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.rwx.jee.padlock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;

/**
 *
 * @author <a href="mailto:arnaud.fonce@r-w-x.net">Arnaud Fonce</a>
 */
class AuthorizationParameter {

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
                    extractQueryParam(annotation, iMethodParameter);
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
        
        private void extractQueryParam(Annotation annotation, int iMethodParameter) {
            if (annotation instanceof QueryParam && ((QueryParam) annotation).value().equals(name)) {
                value = requestContext.getUriInfo().getQueryParameters().getFirst(name);
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
