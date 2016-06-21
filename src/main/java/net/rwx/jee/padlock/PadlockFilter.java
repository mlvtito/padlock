/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jee.padlock;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class PadlockFilter implements ContainerRequestFilter {

    private static final String JWT_COOKIE_NAME = "JTOKEN";
    private static final String JWT_HS256_KEY = "mdLhrTztDGE8DepxnTqoedwPgiGm64oQwm4j92Ad"
            + "VNWeiMRiq6PZWvZ8SAGrUuG5xogKkUyH6hcSkrvS4EdwzHnTj2sdshLXwBzyR2qdqCe5b6hTJt"
            + "qyQZALTix7qu3avP98eB946FnNqUWqsGyxmmpqSfxWTkdEPmBnKzaFPaYuQPsyAkFyA5RdAvMc"
            + "wqj8tXHGt7CQ6v83tqk6dNAuKstpGiaYYB65BaPaV8EGJXWTp9ZQrRfn42xS9vGjRU6J";

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
            if (needAuthentication()) {
                readJWTCookie(requestContext);
            }
        } catch (UnauthorizedException ue) {
            unauthorized(requestContext);
        }
    }

    private void readJWTCookie(ContainerRequestContext requestContext) throws UnauthorizedException {
        Cookie jwtCookie = requestContext.getCookies().get(JWT_COOKIE_NAME);
        if (jwtCookie == null) {
            throw new UnauthorizedException();
        }

        parseJWTCookie(jwtCookie);
    }

    private boolean needAuthentication() {
        return !resourceInfo.getResourceMethod().isAnnotationPresent(WithoutAuthentication.class)
                && !resourceInfo.getResourceMethod().isAnnotationPresent(Identification.class);
    }

    private void unauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED).build()
        );
    }

    private void parseJWTCookie(Cookie jwtCookie) throws UnauthorizedException {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setVerificationKey(new HmacKey(JWT_HS256_KEY.getBytes()))
                .build();

        JwtClaims claims = tryToProcessToClaims(jwtCookie.getValue(), jwtConsumer);
    }

    private JwtClaims tryToProcessToClaims(String jwtToken, JwtConsumer jwtConsumer) throws UnauthorizedException {
        try {
            return jwtConsumer.processToClaims(jwtToken);
        } catch (InvalidJwtException ex) {
            throw new UnauthorizedException();
        }
    }

    private class UnauthorizedException extends Exception {

    }
}
