/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.padlock.testapp;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import net.rwx.jee.padlock.PadlockSession;
import net.rwx.jee.padlock.annotations.WithoutAuthentication;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
@Path("simple")
public class SimpleResource {

    @Inject
    private PadlockSession session;

    @GET
    @Path("noAnnotation")
    public String noAnnotation() {
        return "not authorized";
    }
    
    @GET
    @Path("getNameFromSession")
    public String getNameFromSession() {
        return session.getAttribute("user", TestUserBean.class).getName();
    }
    
    @GET
    @Path("getMailFromSession")
    @WithoutAuthentication
    public String getMailFromSession() {
        return session.getAttribute("user", TestUserBean.class).getMail();
    }

    @GET
    @Path("withoutAuthentication")
    @WithoutAuthentication
    public String withoutAuthentication() {
        return "without authentication";
    }

    @POST
    @Path("login")
    @WithoutAuthentication
    public String login(@FormParam("login") String login, @FormParam("password") String password) {
        if (login.equals(password)) {
            session.setAttribute("user", TestUserBean.builder().name("John Doe").mail("john.doe@test.net").build());
            session.setAuthenticated(true);
            return "connected";
        } else {
            return "not connected";
        }
    }
}
