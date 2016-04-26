package org.fpauer.filters;

import java.io.IOException;
 
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
 
@WebFilter("/AuthenticationFilter")
public class AuthenticationFilter implements Filter {
 
    private ServletContext context;
     
    public void init(FilterConfig fConfig) throws ServletException {
        this.context = fConfig.getServletContext();
        this.context.log("AuthenticationFilter initialized");
    }
     
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
 
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
         
        String uri = req.getRequestURI();
        //this.context.log("AuthenticationFilter Requested Resource::"+uri);
         
        Cookie[] cookies = req.getCookies();
        Cookie access_cookie = null;
        if(cookies != null){
            for(Cookie cookie : cookies){
            	if( Config.get(Config.Keys.COOKIE_NAME).equals(cookie.getName()) )
            	{
            		access_cookie = cookie;
            	}
            }
        }
        
        if(access_cookie == null && !(uri.contains("html") || uri.endsWith("login"))){
            //this.context.log("1 - Unauthorized access request");
        	if( request.getParameter("uri")!=null )
        	{
            	HttpSession session = req.getSession();
            	session.setAttribute("callback", request.getParameter("uri"));
        	}
        	res.sendRedirect("login.html");
        } else if(access_cookie == null || access_cookie.getValue().isEmpty()){
    		response.setContentType("application/json");
        	res.getOutputStream().write(Config.get(Config.Keys.JSON_LOGIN_ERROR).getBytes());
        } else {
            this.context.log("Has cookie");
        	if( uri.endsWith("/data") ) 
        	{
        	   // pass the request along the filter chain
               chain.doFilter(request, response);
        	}
         	else
        	{
          	  // pass the request along the filter chain
              chain.doFilter(request, response);
        	}
        }
    }
 
     
 
    public void destroy() {
        //close any resources here
    }
 
}