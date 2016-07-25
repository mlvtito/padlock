/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jee.padlock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
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
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
@RequestScoped
public class PadlockFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    private PadlockBeanWrapper padlockBeanWrapper;

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void readJWTCookie(ContainerRequestContext requestContext) throws UnauthorizedException, MalformedClaimException, IOException, ClassNotFoundException {
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

    private boolean isIdentification() {
        return resourceInfo.getResourceMethod().isAnnotationPresent(Identification.class);
    }

    private void unauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED).build()
        );
    }

    private void parseJWTCookie(Cookie jwtCookie) throws UnauthorizedException, MalformedClaimException, IOException, ClassNotFoundException {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setVerificationKey(new HmacKey(JWT_HS256_KEY.getBytes()))
                .build();

        JwtClaims claims = tryToProcessToClaims(jwtCookie.getValue(), jwtConsumer);
        String padlockBeanBase64 = claims.getClaimValue("padlockBean", String.class);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(padlockBeanBase64)))) {
            Object padlockBean = ois.readObject();
            padlockBeanWrapper.setBean(padlockBean);
        }
    }

    private JwtClaims tryToProcessToClaims(String jwtToken, JwtConsumer jwtConsumer) throws UnauthorizedException {
        try {
            return jwtConsumer.processToClaims(jwtToken);
        } catch (InvalidJwtException ex) {
            throw new UnauthorizedException();
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        try {
            if (isIdentification()) {
                Object entity = responseContext.getEntity();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                    oos.writeObject(entity);
                }
                JwtClaims claims = new JwtClaims();
                claims.setClaim("padlockBean", Base64.getEncoder().encodeToString(baos.toByteArray()));
                JsonWebSignature jws = new JsonWebSignature();
                jws.setPayload(claims.toJson());
                jws.setKey(new HmacKey(JWT_HS256_KEY.getBytes()));
                jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
                String jwt = jws.getCompactSerialization();
                NewCookie cookie = NewCookie.valueOf(JWT_COOKIE_NAME + "=" + jwt + ";Secure;HttpOnly");
                responseContext.getHeaders().add(HttpHeaders.SET_COOKIE, cookie);
            }
        } catch (JoseException ex) {
            // TODO : generate error during 
            Logger.getLogger(PadlockFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class UnauthorizedException extends Exception {

    }
}
