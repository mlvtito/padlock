/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jee.padlock;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author Arnaud Fonce <arnaud.fonce@r-w-x.net>
 */
@RunWith(MockitoJUnitRunner.class)
public class PadlockFilterTest {

    private static final Map<String, Cookie> NO_COOKIES_MAP = new HashMap<>();
    private static final Map<String, Cookie> INVALID_COOKIES_MAP = new HashMap<>();
    private static final Map<String, Cookie> VALID_COOKIES_MAP = new HashMap<>();
    
    @InjectMocks
    private PadlockFilter padlockFilter;
    
    @Mock
    private ContainerRequestContext requestContext;
    
    @Mock
    private ResourceInfo resourceInfo;
    
    @Captor
    private ArgumentCaptor<Response> responseCaptor;
    
    @Before
    public void initInvalidCookie() {
        INVALID_COOKIES_MAP.put("JTOKEN", new Cookie("JTOKEN", "azertyui.qsdfghj.wxcvb"));
    }
    
    @Before
    public void initValidToken() {
        VALID_COOKIES_MAP.put("JTOKEN", new Cookie("JTOKEN", 
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZS"
                        + "I6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.JFsTLE3xCN84O1dqLwMjI1rjJc"
                        + "sfB2VSwdapienn1o4"));
    }
    
    @Before
    public void initMethodResource() throws NoSuchMethodException {
        when(resourceInfo.getResourceMethod()).thenReturn(
                this.getClass().getMethod("methodWithAuthentication")
        );
    }
    
    @Test
    public void should_Unauthorized_when_Filtering_given_NoJWTToken() throws IOException {
        when(requestContext.getCookies()).thenReturn(NO_COOKIES_MAP);
        
        padlockFilter.filter(requestContext);
        
        verify(requestContext).abortWith(responseCaptor.capture());
        assertThat(responseCaptor.getValue().getStatus())
                .isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
    }
    
    @Test
    public void should_Unauthorized_when_Filtering_given_InvalidJWTToken() throws IOException {
        when(requestContext.getCookies()).thenReturn(INVALID_COOKIES_MAP);
        
        padlockFilter.filter(requestContext);
        
        verify(requestContext).abortWith(responseCaptor.capture());
        assertThat(responseCaptor.getValue().getStatus())
                .isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
    }
    
    @Test
    public void should_Authorized_when_Filtering_given_ValidJWTToken() throws IOException {
        when(requestContext.getCookies()).thenReturn(VALID_COOKIES_MAP);
        
        padlockFilter.filter(requestContext);
        
        verify(requestContext, never()).abortWith(any(Response.class));
    }
    
    @Test
    public void should_Authorized_when_Filtering_given_NOJWTTokenAndWithoutAuth() throws IOException, NoSuchMethodException {
        when(requestContext.getCookies()).thenReturn(NO_COOKIES_MAP);
        when(resourceInfo.getResourceMethod()).thenReturn(
                this.getClass().getMethod("methodWithoutAuthentication")
        );
        
        padlockFilter.filter(requestContext);
        
        verify(requestContext, never()).abortWith(any(Response.class));
    }
    
    @Test
    public void should_Authorized_when_Filtering_givenNOJWTTokenAndIdentificationMethod() throws NoSuchMethodException, IOException {
        when(requestContext.getCookies()).thenReturn(NO_COOKIES_MAP);
        when(resourceInfo.getResourceMethod()).thenReturn(
                this.getClass().getMethod("methodForIdentification")
        );
        
        padlockFilter.filter(requestContext);
        
        verify(requestContext, never()).abortWith(any(Response.class));
    }
    
    public void methodWithAuthentication() {}
    
    @WithoutAuthentication
    public void methodWithoutAuthentication() {}
    
    @Identification
    public void methodForIdentification() {}
}
