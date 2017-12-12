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
package net.rwx.padlock.resources;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import net.rwx.padlock.TestSessionBean;
import net.rwx.padlock.annotations.Authorization;
import net.rwx.padlock.annotations.AuthorizationParameter;
import net.rwx.padlock.annotations.Identification;
import net.rwx.padlock.annotations.WithoutAuthentication;

/**
 *
 * @author <a href="mailto:arnaud.fonce@r-w-x.net">Arnaud Fonce</a>
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
