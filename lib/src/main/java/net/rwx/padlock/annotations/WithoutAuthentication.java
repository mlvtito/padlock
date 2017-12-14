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
package net.rwx.padlock.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.rwx.padlock.PadlockSession;

/**
 * Use this annotation to define JAX-RS resource that does not need authentication.
 * 
 * <p>Authentication is not valid until a valid cookie token is present with an authenticated claim to "true"</p>
 * 
 * <p>To validate or invalidate an authentication, use {@link PadlockSession#setAuthenticated(boolean) }.</p>
 * 
 * <p>For instance : </p>
 * <pre>
 * &#64;POST
 * &#64;WithoutAuthentication
 * public void login(&#64;FormParam("email") String email, 
 *                 &#64;FormParam("password") String password) {
 *   String encrypted = encrypt(password);
 *   if( userService.check(email, encrypted) ) {
 *     session.setAuthenticated(true);
 *   }else {
 *     throw new NotAuthorizedException();
 *   }
 * }
 * </pre>
 * 
 * @see PadlockSession#setAuthenticated(boolean) 
 * @see PadlockSession#isAuthenticated() 
 * @author <a href="mailto:arnaud.fonce@r-w-x.net">Arnaud Fonce</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WithoutAuthentication {
    
}
