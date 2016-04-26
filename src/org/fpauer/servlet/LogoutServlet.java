package org.fpauer.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fpauer.filters.Config;
 
/**
 * Servlet implementation class LogoutServlet
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
        
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException ,IOException {
    	response.setContentType("application/json");
        logout(request, response);
    	response.getOutputStream().write(Config.get(Config.Keys.JSON_LOGIN_ERROR).getBytes());
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        logout(request, response);
        response.sendRedirect("login.html");
    }
    
    private void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
    	Cookie[] cookies = request.getCookies();
        if(cookies != null){
	        for(Cookie cookie : cookies){
	            if(cookie.getName().equals( Config.get(Config.Keys.COOKIE_NAME) )){
	            	Cookie userCookie = new Cookie(Config.get(Config.Keys.COOKIE_NAME) , "");
	            	userCookie.setMaxAge(0);
	            	userCookie.setPath("/");
	            	response.addCookie(userCookie);
	            }
	        }
        }

        //invalidate the session if exists
        HttpSession session = request.getSession(false);
        if(session != null){
            session.invalidate();
        }
    }
 
}
