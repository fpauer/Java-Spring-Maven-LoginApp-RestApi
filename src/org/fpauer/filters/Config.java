/**
 * Copyright 2016 fpauer
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fpauer.filters;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author fpauer
 *
 */
public class Config {
	
	public static enum Keys {
        HOST("server.host"), PORT("server.port"), REST_PATH("server.path"), 
        COOKIE_NAME("cookie.name"), JSON_LOGIN_ERROR("json.login.error"), MESSAGE_LOGIN_ERROR("message.login.error"), ;

        private final String key;

        Keys(String key) {
            this.key = key;
        }

        public String key() {
            return key;
        }
    }
    
    private final static Properties serverConfig = new Properties();

    static {
        try {
        	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            serverConfig.load( classLoader.getResourceAsStream("config.properties") );
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
