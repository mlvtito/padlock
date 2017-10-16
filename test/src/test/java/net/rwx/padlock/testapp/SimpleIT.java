package net.rwx.padlock.testapp;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
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
        Form form = new Form();
        form.param("login", "my-wisely-choose-login")
                .param("password", "my-wisely-choose-login");

        TestUserBean bean = client.path("api/simple/login")
                .request(MediaType.APPLICATION_JSON).post(Entity.form(form), TestUserBean.class);
        
        assertThat(bean.getName()).isEqualTo("John Doe");
        assertThat(bean.getMail()).isEqualTo("john.doe@test.net");
    }
}
