/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jee.padlock.resources;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import net.rwx.jee.padlock.TestSessionBean;
import net.rwx.jee.padlock.annotations.Authorization;
import net.rwx.jee.padlock.annotations.AuthorizationParameter;
import net.rwx.jee.padlock.annotations.Identification;
import net.rwx.jee.padlock.annotations.WithoutAuthentication;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
public class TestResource {
    public void methodWithAuthentication() {
    }

    @WithoutAuthentication
    public void methodWithoutAuthentication() {
    }

    @Identification
    public TestSessionBean methodForIdentification() {
        return TestSessionBean.builder().login("test@test.net").fullName("Test Name").build();
    }

    @Authorization(TestUnauthorization.class)
    public void methodWithWrongAuthorization() {
    }

    @Authorization(TestAuthorized.class)
    public void methodWithRightAuthorization() {
    }
    
    @Authorization(TestAuthorizedWithParameter.class)
    @AuthorizationParameter("firstParameter")
    public void methodWithOneParameter(@PathParam("firstParameter") Integer myParam) {
    }
    
    @Authorization(TestAuthorizedWithParameter.class)
    @AuthorizationParameter("firstParameter")
    @AuthorizationParameter("secondParameter")
    public void methodWithTwoParameters(@PathParam("firstParameter") Integer myParam,
            @PathParam("secondParameter") Integer myOtherParam) {
    }
    
    @Authorization(TestAuthorizedWithParameter.class)
    @AuthorizationParameter("myParameter")
    public void methodWithPathParameter(@PathParam("myParameter") Integer myParam) {
    }
    
    @Authorization(TestAuthorizedWithParameter.class)
    @AuthorizationParameter("myParameter")
    public void methodWithQueryParameter(@QueryParam("myParameter") Integer myParam) {
    }
}
