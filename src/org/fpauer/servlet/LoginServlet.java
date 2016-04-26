package org.fpauer.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
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
 
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 
        // get request parameters for userID and password
        String user = request.getParameter("user");
        String pwd = request.getParameter("pwd");

        if(user.isEmpty() || pwd.isEmpty())
        {            
        	emitMessage(Config.get(Config.Keys.MESSAGE_LOGIN_ERROR), request, response);
        	return;
        }
        
        StringBuilder sUrl = new StringBuilder();
        sUrl.append("http://").append(Config.get(Config.Keys.HOST)).append(":").append(Config.get(Config.Keys.PORT))
        .append(Config.get(Config.Keys.REST_PATH)).append(user).append("/").append(pwd);
		
		URL url = new URL(sUrl.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		int responseCode = -1;
		
		try
		{
			responseCode = conn.getResponseCode();
		}
		catch(ConnectException e)
		{
			emitMessage("Failed to load HTTP: " + sUrl.toString(), request, response);
            return;
		}
		
		if (responseCode != 200) {

			if (responseCode == 401) 
			{
				emitMessage(Config.get(Config.Keys.MESSAGE_LOGIN_ERROR), request, response);
			}
			else
			{
				emitMessage("Failed : " + sUrl.toString() + "HTTP error code : " + responseCode, request, response);
			}
		}
		else
		{
			JSONObject json = getResponseData(conn);
			conn.disconnect();
			
	        if ( json != null && json.has("lookup") ) {
	        	String value = "{\"Status\":\"OK\",\"acessToken\":\""+json.getString("accessToken")+"\",\"userData\":"+json.get("lookup").toString()+"}";
	        	Cookie accessCookie = new Cookie( Config.get(Config.Keys.COOKIE_NAME), value);
	            accessCookie.setMaxAge(30*60);
	            accessCookie.setPath("/");
	            response.addCookie(accessCookie);

            	HttpSession session = request.getSession();
            	if( session != null && session.getAttribute("callback") != null )
            	{
            		response.sendRedirect(session.getAttribute("callback").toString());
            	}
            	else 
	            	response.sendRedirect("LoginSuccess.jsp");
            	
	        } else {
	        	emitMessage(Config.get(Config.Keys.MESSAGE_LOGIN_ERROR), request, response);
	        }
		}
    }

    private RequestDispatcher emitMessage(String message, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
    	try
		{
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.html");
            PrintWriter out= response.getWriter();
            out.println("<font color=red>"+message+"</font>");
            rd.include(request, response);
            return rd;
		}			
		catch(NullPointerException e)
		{
			throw new ServletException(message);
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
				json = new JSONObject(data);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
    }
 
}