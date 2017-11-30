/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.padlock.testapp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import net.rwx.jee.padlock.annotations.Authorization;

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
}
