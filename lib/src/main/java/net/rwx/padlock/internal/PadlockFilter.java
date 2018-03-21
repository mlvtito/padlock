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
package net.rwx.padlock.internal;

import net.rwx.padlock.annotations.WithoutAuthentication;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import net.rwx.padlock.PadlockSession;

/**
 *
 * @author <a href="mailto:arnaud.fonce@r-w-x.net">Arnaud Fonce</a>
 */
@Provider
class PadlockFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String JWT_COOKIE_NAME = "JTOKEN";

    @Inject
    private PadlockSession session;

    @Context
    private ResourceInfo resourceInfo;
    
    @Inject
    private BeanManager beanManager;

    @Inject
    private TokenHelper tokenHelper;
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        try {
            readTokenCookie(requestContext);
            if (needAuthentication()) {
                if( session.isAuthenticated() ) {
                    checkAuthorization(requestContext);
                }else {
                    throw new UnauthorizedException();
                }
            }
        } catch (UnauthorizedException ue) {
            unauthorized(requestContext);
        }
    }

    private boolean needAuthentication() {
        return !resourceInfo.getResourceMethod().isAnnotationPresent(WithoutAuthentication.class);
    }

    private void readTokenCookie(ContainerRequestContext requestContext) throws UnauthorizedException {
        Cookie tokenCookie = requestContext.getCookies().get(JWT_COOKIE_NAME);
        if (tokenCookie != null) {
            tokenHelper.parseTokenAndExtractBean(session, tokenCookie.getValue());
        }
    }

    private void checkAuthorization(ContainerRequestContext requestContext) throws UnauthorizedException {
        AuthorizationChecker authChecker = AuthorizationChecker.builder()
                .fromAuthorizedMethod(resourceInfo.getResourceMethod())
                .valueFrom(requestContext)
                .withBeanManager(beanManager)
                .build();

        authChecker.check();
    }

    private void unauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED).build()
        );
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (!session.isEmpty()) {
            String token = tokenHelper.serializeBeanAndCreateToken(session);
            NewCookie cookie = NewCookie.valueOf(JWT_COOKIE_NAME + "=" + token + ";Secure;HttpOnly;Path=/");
            responseContext.getHeaders().add(HttpHeaders.SET_COOKIE, cookie);
        }
    }
}
