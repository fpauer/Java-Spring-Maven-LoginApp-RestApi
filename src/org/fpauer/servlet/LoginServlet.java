package org.fpauer.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fpauer.filters.Config;
import org.fpauer.json.*;
 
/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 
        // get request parameters for userID and password
        String user = request.getParameter("user");
        String pwd = request.getParameter("pwd");

        StringBuilder sUrl = new StringBuilder();
        sUrl.append("http://").append("127.0.1.1").append(":").append("9998").append("/auth/ldap/")
        .append(user).append("/").append(pwd);
		
		URL url = new URL(sUrl.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {

			if (conn.getResponseCode() == 401) 
			{
	            RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.html");
	            PrintWriter out= response.getWriter();
	            out.println("<font color=red>Either user name or password is wrong.</font>");
	            rd.include(request, response);				
			}
			else
			{
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
		}
		else
		{
			JSONObject json = getResponseData(conn);
			conn.disconnect();
			
	        if ( json != null && json.has("lookup") ) {
	            Cookie accessCookie = new Cookie( Config.COOKIE_NAME, json.getString("accessToken"));
	            accessCookie.setMaxAge(30*60);
	            accessCookie.setPath("/");
	            response.addCookie(accessCookie);

            	HttpSession session = request.getSession();
            	if( session.getAttribute("callback") != null )
            	{
            		response.sendRedirect(session.getAttribute("callback").toString());
            	}
            	else 
	            	response.sendRedirect("LoginSuccess.jsp");
            	
	        } else {
	            RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.html");
	            PrintWriter out= response.getWriter();
	            out.println("<font color=red>Either user name or password is wrong.</font>");
	            rd.include(request, response);
	        }
		}
    }
    
    private JSONObject getResponseData(HttpURLConnection conn)
    {
    	JSONObject json = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output = null;
			String data = "";
			while ((output = br.readLine()) != null) {
				data += output;
			}
			if(!data.isEmpty())
			{
				System.out.println("getResponseData:" + data);
				json = new JSONObject(data);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
    }
 
}