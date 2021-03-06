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
    
    @Test
    public void should_NotBeAuthorized_while_ReadingFromAlwaysUnauthorized() {
        Response.StatusType status = client.path("api/auth/unauthorized")
                .request(MediaType.APPLICATION_JSON).cookie(COOKIE_AUTHENTICATED).get().getStatusInfo();
     
        assertThat(status.getStatusCode()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
    }
    
    @Test
    public void should_BeAuthorized_while_ReadingFromAlwaysAuthorized() {
        String value = client.path("api/auth/authorized")
                .request(MediaType.APPLICATION_JSON).cookie(COOKIE_AUTHENTICATED).get(String.class);
     
        assertThat(value).isEqualTo("my value");
    }
    
    @Test
    public void should_BeAuthorized_while_ReadingFromAuthorizedWithQueryParam_having_GoodValue() {
        String value = client.path("api/auth/authorizationWithQueryParam")
                .queryParam("param", "good value")
                .request(MediaType.APPLICATION_JSON).cookie(COOKIE_AUTHENTICATED).get(String.class);
     
        assertThat(value).isEqualTo("my value");
    }
    
    @Test
    public void should_NotBeAuthorized_while_ReadingFromAuthorizedWithQueryParam_having_WrongValue() {
        Response.StatusType status = client.path("api/auth/authorizationWithQueryParam")
                .queryParam("param", "wrong value")
                .request(MediaType.APPLICATION_JSON).cookie(COOKIE_AUTHENTICATED).get().getStatusInfo();
     
        assertThat(status.getStatusCode()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
    }
    
    @Test
    public void should_NotBeAuthorized_while_ReadingFromAuthorizedWithQueryParam_having_NoValue() {
        Response.StatusType status = client.path("api/auth/authorizationWithQueryParam")
                .request(MediaType.APPLICATION_JSON).cookie(COOKIE_AUTHENTICATED).get().getStatusInfo();
     
        assertThat(status.getStatusCode()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }
    
    @Test
    public void should_BeAuthorized_while_ReadingFromAuthorizedWithPathParam_having_GoodValue() {
        String value = client.path("api/auth/authorizationWithPathParam")
                .path("good value")
                .request(MediaType.APPLICATION_JSON).cookie(COOKIE_AUTHENTICATED).get(String.class);
     
        assertThat(value).isEqualTo("my value");
    }
    
    @Test
    public void should_NotBeAuthorized_while_ReadingFromAuthorizedWithPathParam_having_WrongValue() {
        Response.StatusType status = client.path("api/auth/authorizationWithPathParam")
                .path("wrong value")
                .request(MediaType.APPLICATION_JSON).cookie(COOKIE_AUTHENTICATED).get().getStatusInfo();
     
        assertThat(status.getStatusCode()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
    }
    
    @Test
    public void should_NotBeAuthorized_while_ReadingFromAuthorizedWithPathParam_having_NoValue() {
        Response.StatusType status = client.path("api/auth/authorizationWithPathParam")
                .request(MediaType.APPLICATION_JSON).cookie(COOKIE_AUTHENTICATED).get().getStatusInfo();
     
        assertThat(status.getStatusCode()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    }
}
