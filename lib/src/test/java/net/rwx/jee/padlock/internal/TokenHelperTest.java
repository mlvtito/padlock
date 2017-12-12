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
package net.rwx.jee.padlock.internal;

import net.rwx.jee.padlock.PadlockSession;
import net.rwx.jee.padlock.TestSessionBean;
import net.rwx.jee.padlock.internal.TokenHelper;
import net.rwx.jee.padlock.resources.TestUnserializableSessionBean;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:arnaud.fonce@r-w-x.net">Arnaud Fonce</a>
 */
public class TokenHelperTest {
    
    private static final String TOKEN_VALUE = "eyJhbGciOiJIUzI1NiJ9"
            + ".eyJwYWRsb2NrQmVhbiI6InJPMEFCWE55QUNOdVpYUXVjbmQ0TG1wbFpTNXdZV1JzYjJOckxsUmxjM1JUWlhOemFXOXVRbVZoYmxQYU"
            + "lvOGhzWTZWQWdBQ1RBQUlablZzYkU1aGJXVjBBQkpNYW1GMllTOXNZVzVuTDFOMGNtbHVaenRNQUFWc2IyZHBibkVBZmdBQmVIQjBBQ"
            + "WxVWlhOMElFNWhiV1YwQUExMFpYTjBRSFJsYzNRdWJtVjAifQ"
            + ".RWxOVgfavQhRXZck_VDPviT6TLyRpQYx4U3kKI3ZVO8";
    
    private TokenHelper tokenHelper = new TokenHelper();
    
    @Test
    @Ignore
    public void should_GetValidToken_when_SerializingBeanAndCreatingToken() {
        PadlockSession bean = new PadlockSession();
        bean.setAttribute("bean", TestSessionBean.builder().login("test@test.net").fullName("Test Name").build());
        String token = tokenHelper.serializeBeanAndCreateToken(bean);
        assertThat(token).isEqualTo(TOKEN_VALUE);
    }
    
//    @Test
//    public void should_GetValidBean_when_ParsingTokenAndExtractingBean() throws UnauthorizedException {
//        TestSessionBean bean = (TestSessionBean)tokenHelper.parseTokenAndExtractBean(TOKEN_VALUE);
//        assertThat(bean.getFullName()).isEqualTo("Test Name");
//        assertThat(bean.getLogin()).isEqualTo("test@test.net");
//    }
 
//    @Test(expected = UnauthorizedException.class)
//    public void should_ThrowUnauthorizedException_when_ParsingToken_having_InvalidToken() throws UnauthorizedException {
//        tokenHelper.parseTokenAndExtractBean("azertyuiopfjhdg");
//    }
    
    @Test(expected = RuntimeException.class)
    public void should_ThrowException_when_CreatingToken_having_NonSerializableBean() {
        PadlockSession bean = new PadlockSession();
        bean.setAttribute("bean", TestUnserializableSessionBean.builder()
                .fullName("Non Serilizable").login("not@serializable.net").build());
        tokenHelper.serializeBeanAndCreateToken(bean);
    }
}
