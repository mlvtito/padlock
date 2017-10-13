/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.padlock.testapp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
@Path("simple")
public class SimpleResource {
    
    @GET
    public String get() {
        return "OK";
    }
}
