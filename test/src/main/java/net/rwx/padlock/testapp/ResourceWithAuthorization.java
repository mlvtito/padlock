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
package net.rwx.padlock.testapp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import net.rwx.jee.padlock.annotations.Authorization;
import net.rwx.jee.padlock.annotations.AuthorizationParameter;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
@Path("auth")
public class ResourceWithAuthorization {

    @GET
    @Path("unauthorized")
    @Authorization(AuthorizationAlwaysFalse.class)
    public String unauthorized() {
        return "my value";
    }
    
    @GET
    @Path("authorized")
    @Authorization(AuthorizationAlwaysTrue.class)
    public String authorized() {
        return "my value";
    }
    
    @GET
    @Path("authorizationWithQueryParam")
    @Authorization(AuthorizationWithParam.class)
    @AuthorizationParameter("param")
    public String authorizationWithQueryParam(@QueryParam("param") String param) {
        return "my value";
    }
    
    @GET
    @Path("authorizationWithPathParam/{param}")
    @Authorization(AuthorizationWithParam.class)
    @AuthorizationParameter("param")
    public String authorizationWithPathParam(@PathParam("param") String param) {
        return "my value";
    }
}
