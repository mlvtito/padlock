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

    private static final NewCookie COOKIE_AUTHENTICATED = NewCookie.valueOf("JTOKEN=eyJhbGciOiJIUzI1NiJ9.eyJuZXQucnd4Ln"
            + "BhZGxvY2suYXR0cmlidXRlLnVzZXIiOiJyTzBBQlhOeUFDUnVaWFF1Y25kNExuQmhaR3h2WTJzdWRHVnpkR0Z3Y0M1VVpYTjBWWE5sY2"
            + "tKbFlXNG41d0JxZUdiOVJ3SUFBa3dBQkcxaGFXeDBBQkpNYW1GMllTOXNZVzVuTDFOMGNtbHVaenRNQUFSdVlXMWxjUUIrQUFGNGNIUU"
            + "FFV3B2YUc0dVpHOWxRSFJsYzNRdWJtVjBkQUFJU205b2JpQkViMlU9IiwibmV0LnJ3eC5wYWRsb2NrLmF1dGhlbnRpY2F0ZWQiOnRydW"
            + "V9.dqmvCSx2GF6wGMffcobr-Wz4PNzvdQSG-dkC_I56-Fw");
    
    private static final NewCookie COOKIE_UNAUTHENTICATED = NewCookie.valueOf("JTOKEN=eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoic"
            + "k8wQUJYTnlBQ1J1WlhRdWNuZDRMbkJoWkd4dlkyc3VkR1Z6ZEdGd2NDNVVaWE4wVlhObGNrSmxZVzRuNXdCcWVHYjlSd0lBQWt3QUJHM"
            + "WhhV3gwQUJKTWFtRjJZUzlzWVc1bkwxTjBjbWx1Wnp0TUFBUnVZVzFsY1FCK0FBRjRjSFFBRVdwdmFHNHVaRzlsUUhSbGMzUXVibVYwZ"
            + "EFBSVNtOW9iaUJFYjJVPSJ9.-KFt72DG5NFuxTCErKfk_WT88xNS_25LE2SLCXoLbQk");
    
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

        System.out.println("####### " + cookies.get("JTOKEN"));
        assertThat(cookies).containsKeys("JTOKEN");
    }
    
    @Test
    public void should_GetUserName_while_ReadingFromSessionToken_having_SecuredEndpoint() {
        String name = client.path("api/simple/getNameFromSession")
                .request(MediaType.APPLICATION_JSON).cookie(COOKIE_AUTHENTICATED).get(String.class);
        
        assertThat(name).isEqualTo("John Doe");
    }
    
    @Test
    public void should_GetUserMail_while_ReadingFromSessionToken_having_UnsecuredEndpoint() {
        String mail = client.path("api/simple/getMailFromSession")
                .request(MediaType.APPLICATION_JSON).cookie(COOKIE_AUTHENTICATED).get(String.class);
        
        assertThat(mail).isEqualTo("john.doe@test.net");
    }
}
