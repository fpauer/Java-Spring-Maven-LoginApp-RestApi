package org.fpauer.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fpauer.servlet.LoginServlet;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author fpauer
 *
 */
public class LoginServletTest extends Mockito {

	@Test(expected=ServletException.class)
	public void testDoPostHttpServletRequestHttpServletResponse() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);    
        
        when(request.getParameter("user")).thenReturn("bob");
        when(request.getParameter("pwd")).thenReturn("wrongpassword");
        PrintWriter writer = new PrintWriter("somefile.txt");
        when(response.getWriter()).thenReturn(writer);

        new LoginServlet().doPost(request, response);
        
        verify(request, atLeast(1)).getParameter("username");
        writer.flush(); // it may not have been flushed yet...

        byte[] encoded = Files.readAllBytes(Paths.get("somefile.txt"));
        assertTrue(new String(encoded, "UTF-8").contains("<font color=red>Either user name or password is wrong.</font>"));
	}

	@Test(expected=ServletException.class)
	public void testDoPostHttpServletRequestHttpServletResponse2() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);    
        
        when(request.getParameter("user")).thenReturn("bob");
        when(request.getParameter("pwd")).thenReturn("bobspassword");
        PrintWriter writer = new PrintWriter("somefile.txt");
        when(response.getWriter()).thenReturn(writer);

        new LoginServlet().doPost(request, response);

        final ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(captor.capture());
        final Cookie cookies = captor.getValue();
        
        verify(request, atLeast(1)).getParameter("user");
        writer.flush(); // it may not have been flushed yet...
        
        assertTrue(cookies.getValue().contains("Bob Hamilton"));
 	}

}
