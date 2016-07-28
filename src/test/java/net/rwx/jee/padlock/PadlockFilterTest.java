/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jee.padlock;

import net.rwx.jee.padlock.annotations.Identification;
import net.rwx.jee.padlock.annotations.WithoutAuthentication;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.inject.spi.BeanManager;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import net.rwx.jee.padlock.annotations.Authorization;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

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
            + ".eyJwYWRsb2NrQmVhbiI6InJPMEFCWE55QUNOdVpYUXVjbmQ0TG1wbFpTNXdZV1JzYjJOckxsUmxjM1JUWlhOemFXOXVRbVZoYmxQYU"
            + "lvOGhzWTZWQWdBQ1RBQUlablZzYkU1aGJXVjBBQkpNYW1GMllTOXNZVzVuTDFOMGNtbHVaenRNQUFWc2IyZHBibkVBZmdBQmVIQjBBQ"
            + "WxVWlhOMElFNWhiV1YwQUExMFpYTjBRSFJsYzNRdWJtVjAifQ"
            + ".RWxOVgfavQhRXZck_VDPviT6TLyRpQYx4U3kKI3ZVO8";

    @InjectMocks
    private PadlockFilter padlockFilter;

    @Mock
    private PadlockBeanWrapper padlockBeanWrapper;

    @Mock
    private TokenHelper tokenHelper;

    @Mock
    private ContainerRequestContext requestContext;

    @Mock
    private ContainerResponseContext responseContext;

    @Mock
    private BeanManager beanManager;

    @Mock
    private ResourceInfo resourceInfo;

    @Before
    public void initInvalidCookie() {
        INVALID_COOKIES_MAP.put("JTOKEN", new Cookie("JTOKEN", "azertyui.qsdfghj.wxcvb"));
    }

    @Before
    public void initValidTokenAndMockItByDefault() throws UnauthorizedException {
        VALID_COOKIES_MAP.put("JTOKEN", new Cookie("JTOKEN", TOKEN_VALUE));
        when(requestContext.getCookies()).thenReturn(VALID_COOKIES_MAP);
        when(tokenHelper.parseTokenAndExtractBean(anyString()))
                .thenReturn(TestSessionBean.builder().fullName("Name").login("test@test.net").build());
    }

    @Before
    public void mockBeanManager() {
        mockBeanReference(new TestAuthorized());
        mockBeanReference(new TestUnauthorization());
    }

    @Before
    public void mockMethodWithAuthenticationByDefault() throws NoSuchMethodException {
        mockResourceMethod("methodWithAuthentication");
    }

    @Test
    public void should_Unauthorized_when_FilteringRequest_having_NoTokenCookie() throws IOException {
        when(requestContext.getCookies()).thenReturn(NO_COOKIES_MAP);
        padlockFilter.filter(requestContext);
        assertUnauthorized();
    }

    @Test
    public void should_Unauthorized_when_FilteringRequest_having_InvalidToken() throws IOException, UnauthorizedException {
        when(requestContext.getCookies()).thenReturn(INVALID_COOKIES_MAP);
        when(tokenHelper.parseTokenAndExtractBean(anyString())).thenThrow(new UnauthorizedException());

        padlockFilter.filter(requestContext);

        assertUnauthorized();
    }

    @Test
    public void should_Authorized_when_FilteringRequest_having_ValidToken() throws IOException, UnauthorizedException {
        padlockFilter.filter(requestContext);
        assertAuthorized();
    }

    @Test
    public void should_SetPadlockBean_when_FilteringRequest_having_ValidToken() throws IOException, UnauthorizedException {
        padlockFilter.filter(requestContext);

        ArgumentCaptor<Object> padlockBeanCaptor = ArgumentCaptor.forClass(Object.class);
        verify(padlockBeanWrapper).setBean(padlockBeanCaptor.capture());
        TestSessionBean sessionBean = (TestSessionBean) padlockBeanCaptor.getValue();
        assertThat(sessionBean.getLogin()).isEqualTo("test@test.net");
    }

    @Test
    public void should_Authorized_when_FilteringRequest_having_NOTokenAndWithoutAuth() throws IOException, NoSuchMethodException {
        when(requestContext.getCookies()).thenReturn(NO_COOKIES_MAP);
        mockResourceMethod("methodWithoutAuthentication");
        padlockFilter.filter(requestContext);
        assertAuthorized();
    }

    @Test
    public void should_Authorized_when_FilteringRequest_having_NOTokenAndIdentificationMethod() throws NoSuchMethodException, IOException {
        when(requestContext.getCookies()).thenReturn(NO_COOKIES_MAP);
        mockResourceMethod("methodForIdentification");
        padlockFilter.filter(requestContext);
        assertAuthorized();
    }

    @Test
    public void should_SetTokenCookie_when_FilteringResponse_having_IdentificationMethod() throws NoSuchMethodException, IOException {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseContext.getHeaders()).thenReturn(headers);
        when(responseContext.getEntity()).thenReturn(methodForIdentification());
        when(tokenHelper.serializeBeanAndCreateToken(anyObject())).thenReturn("FAKETOKEN");
        when(resourceInfo.getResourceMethod()).thenReturn(this.getClass().getMethod("methodForIdentification"));

        padlockFilter.filter(requestContext, responseContext);

        assertThat(headers.get(HttpHeaders.SET_COOKIE).get(0)).extracting("name").containsExactly("JTOKEN");
        assertThat(headers.get(HttpHeaders.SET_COOKIE).get(0)).extracting("secure").containsExactly(true);
        assertThat(headers.get(HttpHeaders.SET_COOKIE).get(0)).extracting("httpOnly").containsExactly(true);
        assertThat(headers.get(HttpHeaders.SET_COOKIE).get(0)).extracting("value").containsExactly("FAKETOKEN");
    }

    @Test
    public void should_Unauthorized_when_FilteringRequest_having_WrongAuthorization() throws NoSuchMethodException, UnauthorizedException {
        mockResourceMethod("methodWithWrongAuthorization");
        padlockFilter.filter(requestContext);
        assertUnauthorized();
    }

    @Test
    public void should_Authorized_when_FilteringRequest_having_RightAuthorization() throws NoSuchMethodException, UnauthorizedException {
        mockResourceMethod("methodWithRightAuthorization");
        padlockFilter.filter(requestContext);
        assertAuthorized();
    }

    private void mockResourceMethod(String methodName) throws NoSuchMethodException {
        when(resourceInfo.getResourceMethod()).thenReturn(this.getClass().getMethod(methodName));
    }

    private void mockBeanReference(Object reference) {
        when(beanManager.getReference(any(), eq(reference.getClass()), any())).thenReturn(reference);
    }

    private void assertUnauthorized() {
        ArgumentCaptor<Response> responseCaptor = ArgumentCaptor.forClass(Response.class);
        verify(requestContext).abortWith(responseCaptor.capture());
        assertThat(responseCaptor.getValue().getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    private void assertAuthorized() {
        verify(requestContext, never()).abortWith(any(Response.class));
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

    @Authorization(TestUnauthorization.class)
    public void methodWithWrongAuthorization() {
    }

    @Authorization(TestAuthorized.class)
    public void methodWithRightAuthorization() {
    }
}
