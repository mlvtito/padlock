/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rwx.jee.padlock;

import net.rwx.jee.padlock.resources.TestUnauthorization;
import net.rwx.jee.padlock.resources.TestAuthorized;
import net.rwx.jee.padlock.resources.TestAuthorizedWithParameter;
import net.rwx.jee.padlock.resources.TestResource;
import net.rwx.jee.padlock.annotations.Identification;
import net.rwx.jee.padlock.annotations.WithoutAuthentication;
import java.io.IOException;
import java.util.Arrays;
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
import javax.ws.rs.core.UriInfo;
import net.rwx.jee.padlock.annotations.Authorization;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Ignore;
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
    private static final MultivaluedMap<String, String> PATH_PARAMETERS = new MultivaluedHashMap<>();
    private static final MultivaluedMap<String, String> QUERY_PARAMETERS = new MultivaluedHashMap<>();
    
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
    
    @Mock
    private UriInfo uriInfo;
    
    @Before
    public void initInvalidCookie() {
        INVALID_COOKIES_MAP.put("JTOKEN", new Cookie("JTOKEN", "azertyui.qsdfghj.wxcvb"));
    }

    @Before
    public void initAndMockResourceParameters() {
        PATH_PARAMETERS.put("firstParameter", Arrays.asList("65"));
        PATH_PARAMETERS.put("secondParameter", Arrays.asList("433"));
        PATH_PARAMETERS.put("myParameter", Arrays.asList("9876"));
        
        QUERY_PARAMETERS.put("myParameter", Arrays.asList("4321"));
        
        when(requestContext.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPathParameters()).thenReturn(PATH_PARAMETERS);
        when(uriInfo.getQueryParameters()).thenReturn(QUERY_PARAMETERS);
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
        mockBeanReference(new TestAuthorizedWithParameter());
        mockBeanReference(new ParamConverters());
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
    @Ignore
    public void should_SetTokenCookie_when_FilteringResponse_having_IdentificationMethod() throws NoSuchMethodException, IOException {
        mockResourceMethod("methodForIdentification");
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseContext.getHeaders()).thenReturn(headers);
        when(tokenHelper.serializeBeanAndCreateToken(anyObject())).thenReturn("FAKETOKEN");
        when(responseContext.getEntity()).thenReturn(new TestResource().methodForIdentification());

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

    @Test
    public void should_SetParamIntoChecker_when_FilteringRequest_having_OneParameter() throws NoSuchMethodException {
        mockResourceMethod("methodWithOneParameter", Integer.class);
        padlockFilter.filter(requestContext);
        assertThat(getTestAuthorizedWithParams().getFirstParameter()).isEqualTo(65);
    }

    
    @Test
    public void should_SetParamsIntoChecker_when_FilteringRequest_having_TwoParameters() throws NoSuchMethodException {
        mockResourceMethod("methodWithTwoParameters", Integer.class, Integer.class);
        padlockFilter.filter(requestContext);
        TestAuthorizedWithParameter checker = getTestAuthorizedWithParams();
        assertThat(checker.getFirstParameter()).isEqualTo(65);
        assertThat(checker.getSecondParameter()).isEqualTo(433);
    }
    
    @Test
    public void should_SetParamIntoChecker_when_FilteringRequest_having_PathParam() throws NoSuchMethodException {
        mockResourceMethod("methodWithPathParameter", Integer.class);
        padlockFilter.filter(requestContext);
        assertThat(getTestAuthorizedWithParams().getMyParameter()).isEqualTo(9876);
    }
    
    @Test
    public void should_SetParamIntoChecker_when_FilteringRequest_having_QueryParam() throws NoSuchMethodException {
        mockResourceMethod("methodWithQueryParameter", Integer.class);
        padlockFilter.filter(requestContext);
        assertThat(getTestAuthorizedWithParams().getMyParameter()).isEqualTo(4321);
    }
    
    private void mockResourceMethod(String methodName, Class<?>... parameters) throws NoSuchMethodException {
        when(resourceInfo.getResourceMethod()).thenReturn(TestResource.class.getMethod(methodName, parameters));
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
    
    private TestAuthorizedWithParameter getTestAuthorizedWithParams() {
        return (TestAuthorizedWithParameter)beanManager
                .getReference(null, TestAuthorizedWithParameter.class, null);
    }
}
