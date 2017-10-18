package net.rwx.padlock.testapp;

import java.util.Map;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class SimpleIT {

    private WebTarget client = ClientBuilder.newClient().target("http://localhost:8080/test");

    @Test
    public void should_BeProtectedByDefault_when_DeclaringRestResource() {
        Response.StatusType status = client.path("api/simple/noAnnotation")
                .request(MediaType.APPLICATION_JSON).get().getStatusInfo();

        assertThat(status.getStatusCode()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    public void should_BeFreelyAccessible_when_DeclaringRestResource_having_NoAuthAnnotation() {
        String response = client.path("api/simple/withoutAuthentication")
                .request(MediaType.APPLICATION_JSON).get(String.class);

        assertThat(response).isEqualTo("without authentication");
    }

    @Test
    public void should_HaveGetBean_when_Login() {
        Form form = new Form()
                .param("login", "my-wisely-choose-login")
                .param("password", "my-wisely-choose-login");

        Map<String, NewCookie> cookies = client.path("api/simple/login")
                .request(MediaType.APPLICATION_JSON).post(Entity.form(form)).getCookies();

        assertThat(cookies).containsKeys("JTOKEN");
    }
    
    @Test
    public void should_GetUserName_while_ReadingFromSessionToken() {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoick8wQUJYTnlBQ1J1WlhRdWNuZDRMbkJoWkd4dlkyc3VkR1Z6ZEdGd2NDNVVaWE4w"
                + "VlhObGNrSmxZVzRuNXdCcWVHYjlSd0lBQWt3QUJHMWhhV3gwQUJKTWFtRjJZUzlzWVc1bkwxTjBjbWx1Wnp0TUFBUnVZVzFsY1FC"
                + "K0FBRjRjSFFBRVdwdmFHNHVaRzlsUUhSbGMzUXVibVYwZEFBSVNtOW9iaUJFYjJVPSJ9.-KFt72DG5NFuxTCErKfk_WT88xNS_25"
                + "LE2SLCXoLbQk";
        
        String name = client.path("api/simple/getNameFromSession")
                .request(MediaType.APPLICATION_JSON).cookie(NewCookie.valueOf("JTOKEN="+jwt)).get(String.class);
        
        assertThat(name).isEqualTo("John Doe");
    }
}
