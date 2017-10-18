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
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
@ApplicationScoped
class TokenHelper {

    private static final Logger logger = Logger.getLogger(TokenHelper.class.getName());

    private static final String BEAN_CLAIM_KEY = "padlockBean";

    private static final String JWT_HS256_KEY = "mdLhrTztDGE8DepxnTqoedwPgiGm64oQwm4j92Ad"
            + "VNWeiMRiq6PZWvZ8SAGrUuG5xogKkUyH6hcSkrvS4EdwzHnTj2sdshLXwBzyR2qdqCe5b6hTJt"
            + "qyQZALTix7qu3avP98eB946FnNqUWqsGyxmmpqSfxWTkdEPmBnKzaFPaYuQPsyAkFyA5RdAvMc"
            + "wqj8tXHGt7CQ6v83tqk6dNAuKstpGiaYYB65BaPaV8EGJXWTp9ZQrRfn42xS9vGjRU6J";

    void parseTokenAndExtractBean(PadlockSession session, String token) throws UnauthorizedException {
        try {
            JwtConsumer jwtConsumer = buildTokenConsumer();
            JwtClaims claims = jwtConsumer.processToClaims(token);
            for(String name: claims.getClaimNames()) {
                session.setAttribute(name, deserializeBean(claims.getClaimValue(name, String.class)));
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while parsing JWT token", e);
            throw new UnauthorizedException();
        }
    }

    String serializeBeanAndCreateToken(PadlockSession bean) {
        try {
            JwtClaims claims = new JwtClaims();
            Enumeration<String> names = bean.getAttributeNames();
            while( names.hasMoreElements() ) {
                String name = names.nextElement();
                String claim = serializeBean(bean.getAttribute(name));
                claims.setClaim(name, claim);
            }
            JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(claims.toJson());
            jws.setKey(new HmacKey(JWT_HS256_KEY.getBytes()));
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
            return jws.getCompactSerialization();
        } catch (IOException | JoseException e) {
            logger.log(Level.WARNING, "Error while creating JWT token", e);
            throw new RuntimeException(e);
        }
    }

    private JwtConsumer buildTokenConsumer() {
        return new JwtConsumerBuilder().setVerificationKey(new HmacKey(JWT_HS256_KEY.getBytes())).build();
    }

    private Object deserializeBean(String serializedBean) throws Exception {
        try (ObjectInputStream ois = buildObjectInputStream(serializedBean)) {
            return ois.readObject();
        }
    }

    private String serializeBean(Object bean) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(bean);
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    private static ObjectInputStream buildObjectInputStream(String serializedBean) throws IOException {
        return new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(serializedBean)));
    }

}
