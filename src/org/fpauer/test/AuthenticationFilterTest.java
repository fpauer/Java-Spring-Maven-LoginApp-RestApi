package org.fpauer.test;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fpauer.filters.AuthenticationFilter;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author fpauer
 *
 */
public class AuthenticationFilterTest extends Mockito {

	@Test
	public void test() throws IOException, ServletException {
		  // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    
	    // mock the getRequestURI() response
	    when(httpServletRequest.getRequestURI()).thenReturn("/login-app/");

	    AuthenticationFilter authenticationFilter = new AuthenticationFilter();
	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);

	    // verify if a sendRedirect() was performed with the expected value
	    verify(httpServletResponse).sendRedirect("login.html");
	}

}
