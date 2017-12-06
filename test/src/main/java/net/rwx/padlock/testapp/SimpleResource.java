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
        TestUserBean user = session.getAttribute("user", TestUserBean.class);
        return user.getName();
    }
    
    @GET
    @Path("getMailFromSession")
    @WithoutAuthentication
    public String getMailFromSession() {
        TestUserBean user = session.getAttribute("user", TestUserBean.class);
        return user.getMail();
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
