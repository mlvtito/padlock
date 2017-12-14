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
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to give access the JAX-RS resource parameters to the authorization class define with 
 * {@link Authorization}.
 * 
 * <p>For instance, you can define a JAX-RS resource as below :</p>
 * <pre>
 * &#64;GET
 * &#64;Path("authorizationWithPathParam/{param}")
 * &#64;Authorization(AuthorizationWithParam.class)
 * &#64;AuthorizationParameter("param")
 * public String authorizationWithPathParam(@PathParam("param") String param) {
 *   return "my value";
 * }
 * </pre>
 * 
 * <p>Then the authorization class should have a setter for <i>param</i> parameter.</p>
 * <pre>
 * public class AuthorizationWithParam {
 *   private String param;
 * 
 *   public boolean authorized() {
 *     return param != null &and;&and; param.equals("good value");
 *   }
 * 
 *   public void setParam(String param) {
 *     this.param = param;
 *   }
 * }
 * </pre>
 * @see Authorization
 * @author <a href="mailto:arnaud.fonce@r-w-x.net">Arnaud Fonce</a>
 */
@Repeatable(AuthorizationParameters.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthorizationParameter {
    public String value();
}
