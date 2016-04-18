package org.fpauer.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class Config {
	
	public static enum Keys {
        HOST("server.host"), PORT("server.port"), REST_PATH("server.path"), 
        COOKIE_NAME("cookie.name"), MESSAGE_LOGIN_ERROR("message.login.error"), ;

        private final String key;

        Keys(String key) {
            this.key = key;
        }

        public String key() {
            return key;
        }
    }
    
    private final static Properties serverConfig = new Properties();
    private static Logger logger = Logger.getLogger(Config.class.getName());

    static {
        try {
        	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            serverConfig.load( classLoader.getResourceAsStream("/config.properties") );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isConfigured(String s) {
        return serverConfig.get(s) != null;
    }

    public static String get(Keys key) {
        return serverConfig.getProperty(key.key());
    }

    public static Properties getServerConfig() {
        return serverConfig;
    }

}
