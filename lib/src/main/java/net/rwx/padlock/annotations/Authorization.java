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

/**
 * Use this annotation on a JAX-RS resource to define authorization rules.
 * 
 * <p>The annotation value contains the class wich implement these rules.</p>
 * 
 * <p>For instance :</p>
 * <pre>
 * &#64;GET
 * &#64;Authorization(AuthorizationAlwaysTrue.class)
 * public String shouldHaveUsedWithoutAuthentication() {
 *   return "My Public Information";
 * }
 * </pre>
 * As you can see in the example, <i>AuthorizationAlwaysTrue</i> will be the authorization class which is in charge of 
 * validating authorization.
 * 
 * <p>An authorization class must only define a <i>authorized</i> method which return a boolean value.</p>
 * For instance : 
 * <pre>
 * public class AuthorizationAlwaysTrue {
 *   public boolean authorized() {
 *     return true;
 *   }
 * }
 * </pre>
 * 
 * @see AuthorizationParameter
 * @author <a href="mailto:arnaud.fonce@r-w-x.net">Arnaud Fonce</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Authorization {
    public Class value();
}
