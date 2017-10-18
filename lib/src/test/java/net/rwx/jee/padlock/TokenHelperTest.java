/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jee.padlock;

import net.rwx.jee.padlock.resources.TestUnserializableSessionBean;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
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
    
    @Test
    public void should_GetValidBean_when_ParsingTokenAndExtractingBean() throws UnauthorizedException {
        TestSessionBean bean = (TestSessionBean)tokenHelper.parseTokenAndExtractBean(TOKEN_VALUE);
        assertThat(bean.getFullName()).isEqualTo("Test Name");
        assertThat(bean.getLogin()).isEqualTo("test@test.net");
    }
 
    @Test(expected = UnauthorizedException.class)
    public void should_ThrowUnauthorizedException_when_ParsingToken_having_InvalidToken() throws UnauthorizedException {
        tokenHelper.parseTokenAndExtractBean("azertyuiopfjhdg");
    }
    
    @Test(expected = RuntimeException.class)
    public void should_ThrowException_when_CreatingToken_having_NonSerializableBean() {
        PadlockSession bean = new PadlockSession();
        bean.setAttribute("bean", TestUnserializableSessionBean.builder()
                .fullName("Non Serilizable").login("not@serializable.net").build());
        tokenHelper.serializeBeanAndCreateToken(bean);
    }
}
