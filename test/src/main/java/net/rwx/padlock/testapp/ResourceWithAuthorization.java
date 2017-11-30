/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
