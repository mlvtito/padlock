/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jee.padlock;

import net.rwx.jee.padlock.annotations.Identification;
import net.rwx.jee.padlock.annotations.WithoutAuthentication;
import javax.enterprise.context.RequestScoped;
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
@RequestScoped
@Provider
public class PadlockFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    private PadlockBeanWrapper padlockBeanWrapper;

    private static final String JWT_COOKIE_NAME = "JTOKEN";

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    private BeanManager beanManager;

    @Inject
    private TokenHelper tokenHelper;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        try {
            if (needAuthentication()) {
                readTokenCookie(requestContext);
                checkAuthorization(requestContext);
            }
        } catch (UnauthorizedException ue) {
            unauthorized(requestContext);
        }
    }

    private boolean needAuthentication() {
        return !resourceInfo.getResourceMethod().isAnnotationPresent(WithoutAuthentication.class)
                && !resourceInfo.getResourceMethod().isAnnotationPresent(Identification.class);
    }

    private void readTokenCookie(ContainerRequestContext requestContext) throws UnauthorizedException {
        Cookie tokenCookie = requestContext.getCookies().get(JWT_COOKIE_NAME);
        if (tokenCookie == null) {
            throw new UnauthorizedException();
        }

        Object bean = tokenHelper.parseTokenAndExtractBean(tokenCookie.getValue());
        padlockBeanWrapper.setBean(bean);
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
        if (isIdentification()) {
            Object entity = responseContext.getEntity();
            String token = tokenHelper.serializeBeanAndCreateToken(entity);
            NewCookie cookie = NewCookie.valueOf(JWT_COOKIE_NAME + "=" + token + ";Secure;HttpOnly");
            responseContext.getHeaders().add(HttpHeaders.SET_COOKIE, cookie);
        }
    }

    private boolean isIdentification() {
        return resourceInfo.getResourceMethod().isAnnotationPresent(Identification.class);
    }
}
