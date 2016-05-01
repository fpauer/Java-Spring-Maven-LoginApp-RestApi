package org.fpauer.test;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fpauer.filters.Config;
import org.fpauer.json.JSONObject;
import org.fpauer.servlet.LogoutServlet;
import org.fpauer.servlet.Utils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author fpauer
 *
 */
public class LogoutServletTest extends Mockito {

	@Test
	public void test() throws IOException, ServletException {

        //getting a cookie
        StringBuilder sUrl = new StringBuilder();
        sUrl.append("http://").append(Config.get(Config.Keys.HOST)).append(":").append(Config.get(Config.Keys.PORT))
        .append(Config.get(Config.Keys.REST_PATH)).append("bob").append("/").append("bobspassword");
		
		URL url = new URL(sUrl.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		}
		
		JSONObject json = Utils.INSTANCE.getResponseData(conn);
		conn.disconnect();
		
        if ( json != null && json.has("lookup") ) {
        	String value = "{\"Status\":\"OK\",\"acessToken\":\""+json.getString("accessToken")+"\",\"userData\":"+json.get("lookup").toString()+"}";
            Cookie accessCookie = new Cookie( Config.get(Config.Keys.COOKIE_NAME), value);
            accessCookie.setMaxAge(30*60);
            
            //testing DataSerlet
    		HttpServletRequest request = mock(HttpServletRequest.class);       
            HttpServletResponse response = mock(HttpServletResponse.class);    
            when(request.getCookies()).thenReturn(new Cookie[]{accessCookie});

            final ByteArrayOutputStream resultData = new ByteArrayOutputStream();
            ServletOutputStream output = new ServletOutputStream() {

                @Override
                public void write(int val) throws IOException {
                    resultData.write(val);
                }

				@Override
				public boolean isReady() {
					return false;
				}

				@Override
				public void setWriteListener(WriteListener arg0) {}
            };

            when(response.getOutputStream()).thenReturn(output);
            
            new LogoutServlet().doGet(request, response);
            assertTrue(resultData.toString("UTF-8").contains(Config.get(Config.Keys.JSON_LOGIN_ERROR)));

            final ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
            verify(response).addCookie(captor.capture());
            final Cookie cookies = captor.getValue();

            assertTrue(cookies.getMaxAge()==0);
        }
        
	}
	
    
}
