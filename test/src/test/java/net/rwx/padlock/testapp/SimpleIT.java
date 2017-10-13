package net.rwx.padlock.testapp;


import javax.ws.rs.client.ClientBuilder;
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
    
    @Test
    public void should_BeProtectedByDefault_when_DeclaringRestResource() {
        Response.StatusType status = ClientBuilder.newClient()
                .target("http://localhost:8080/test").path("api/simple/noAnnotation")
                .request(MediaType.APPLICATION_JSON).get().getStatusInfo();
        
        assertThat(status.getStatusCode()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
    }
    
    @Test
    public void should_BeFreelyAccessible_when_DeclaringRestResource_having_NoAuthAnnotation() {
        String response = ClientBuilder.newClient()
                .target("http://localhost:8080/test").path("api/simple/withoutAuthentication")
                .request(MediaType.APPLICATION_JSON).get(String.class);
        
        assertThat(response).isEqualTo("without authentication");
    }
}
