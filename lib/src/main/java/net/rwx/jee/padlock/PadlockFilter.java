/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jee.padlock;

import net.rwx.jee.padlock.annotations.WithoutAuthentication;
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

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
@Provider
public class PadlockFilter implements ContainerRequestFilter, ContainerResponseFilter {

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
            NewCookie cookie = NewCookie.valueOf(JWT_COOKIE_NAME + "=" + token + ";Secure;HttpOnly");
            responseContext.getHeaders().add(HttpHeaders.SET_COOKIE, cookie);
        }
    }
}
