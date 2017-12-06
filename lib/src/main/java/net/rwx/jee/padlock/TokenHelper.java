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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
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

    private static final String CLAIM_AUTHENTICATED = "net.rwx.padlock.authenticated";
    private static final String CLAIM_ATTRIBUTE_PREFIX = "net.rwx.padlock.attribute.";
    
    private static final Logger logger = Logger.getLogger(TokenHelper.class.getName());

    @Context
    private Application application;
    
    @Inject
    private BeanManager beanManager;
    
    void parseTokenAndExtractBean(PadlockSession session, String token) throws UnauthorizedException {
        try {
            
            JwtConsumer jwtConsumer = buildTokenConsumer();
            JwtClaims claims = jwtConsumer.processToClaims(token);
            for(String name: claims.getClaimNames()) {
                if( name.startsWith(CLAIM_ATTRIBUTE_PREFIX) ) {
                    String attributeName = name.substring(CLAIM_ATTRIBUTE_PREFIX.length());
                    Object bean = deserializeBean(claims.getClaimValue(name, String.class));
                    session.setAttribute(attributeName, bean);
                }else if(name.equals(CLAIM_AUTHENTICATED)) {
                    session.setAuthenticated(claims.getClaimValue(name, Boolean.class));
                }
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
                claims.setClaim(CLAIM_ATTRIBUTE_PREFIX + name, claim);
            }
            claims.setClaim(CLAIM_AUTHENTICATED, bean.isAuthenticated());
            JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(claims.toJson());
            jws.setKey(new HmacKey(retreiveProvidedKey()));
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
            return jws.getCompactSerialization();
        } catch (IOException | JoseException e) {
            logger.log(Level.WARNING, "Error while creating JWT token", e);
            throw new RuntimeException(e);
        }
    }

    private JwtConsumer buildTokenConsumer() {
        return new JwtConsumerBuilder().setVerificationKey(new HmacKey(retreiveProvidedKey())).build();
    }

    private byte[] retreiveProvidedKey() {
        Optional<Class<?>> keyProviderType = application.getClasses().stream().filter(cls -> KeyProvider.class.isAssignableFrom(cls)).findFirst();
        if (keyProviderType.isPresent()) {
            Bean<KeyProvider> bean = (Bean<KeyProvider>) beanManager.resolve(beanManager.getBeans(keyProviderType.get()));
            KeyProvider keyProvider = (KeyProvider) beanManager.getReference(bean, keyProviderType.get(), beanManager.createCreationalContext(bean));
            return keyProvider.getKey();
        } else {
            throw new RuntimeException("No key provided");
        }
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
