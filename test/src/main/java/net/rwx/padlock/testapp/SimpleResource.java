/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.padlock.testapp;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import net.rwx.jee.padlock.annotations.Identification;
import net.rwx.jee.padlock.annotations.WithoutAuthentication;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
@Path("simple")
public class SimpleResource {
    
    @GET
    @Path("noAnnotation")
    public String get() {
        return "without annotation";
    }
    
    @GET
    @Path("withoutAuthentication")
    @WithoutAuthentication
    public String withoutAuthentication() {
        return "without authentication";
    }
    
    @POST
    @Path("login")
    @Identification
    public TestUserBean login(@FormParam("login") String login, @FormParam("password") String password) {
        if( login.equals(password)) {
            return TestUserBean.builder().name("John Doe").mail("john.doe@test.net").build();
        }else {
            throw new RuntimeException("Unauthified");
        }
    }
}
