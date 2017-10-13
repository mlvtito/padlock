package net.rwx.padlock.testapp;


import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
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
    public void should_BlaBlaBlaBlaBlaBlaBlaBlaBlaBla() {
        String response = ClientBuilder.newClient()
                .target("http://localhost:8080/test").path("api/simple")
                .request(MediaType.APPLICATION_JSON).get(String.class);
        assertThat(response).isEqualTo("OK");
    }
}
