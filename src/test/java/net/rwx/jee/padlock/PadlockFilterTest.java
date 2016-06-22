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
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
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
    private static final String TOKEN_VALUE = "eyJhbGciOiJIUzI1NiJ9"
            + ".eyJwYWRsb2NrQmVhbiI6Im5ldC5yd3guamVlLnBhZGxvY2suVGVzdFNlc3Npb25CZWFuQDIyMjExNGJhIn0"
            + ".SIhcwsV2cBzMQQezBFk3gY467PZObX9V5NaDKTZ6t0g";

    @InjectMocks
    private PadlockFilter padlockFilter;

    @Mock
    private ContainerRequestContext requestContext;

    @Mock
    private ContainerResponseContext responseContext;

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
        VALID_COOKIES_MAP.put("JTOKEN", new Cookie("JTOKEN", TOKEN_VALUE));
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
    public void should_Authorized_when_Filtering_given_NOJWTTokenAndIdentificationMethod() throws NoSuchMethodException, IOException {
        when(requestContext.getCookies()).thenReturn(NO_COOKIES_MAP);
        when(resourceInfo.getResourceMethod()).thenReturn(
                this.getClass().getMethod("methodForIdentification")
        );

        padlockFilter.filter(requestContext);

        verify(requestContext, never()).abortWith(any(Response.class));
    }

    @Test
    public void should_SetNewJWTCookie_when_FilteringResponse_given_IdentificationMethod() throws NoSuchMethodException, IOException {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseContext.getHeaders()).thenReturn(headers);
        when(responseContext.getEntity()).thenReturn(methodForIdentification());

        padlockFilter.filter(requestContext, responseContext);

        assertThat(headers.get(HttpHeaders.SET_COOKIE).get(0)).extracting("name").containsExactly("JTOKEN");
        assertThat(headers.get(HttpHeaders.SET_COOKIE).get(0)).extracting("value").containsExactly(TOKEN_VALUE);
    }

    public void methodWithAuthentication() {
    }

    @WithoutAuthentication
    public void methodWithoutAuthentication() {
    }

    @Identification
    public TestSessionBean methodForIdentification() {
        return TestSessionBean.builder().login("test@test.net").fullName("Test Name").build();
    }
}
