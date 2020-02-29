package org.danielmkraus.transfer;

import java.util.Properties;

public interface ServerProperties {
    Properties SERVER_PROPERTIES = new Properties();

    String SERVER_PORT = "server.port";
    String SERVER_BIND_ADDRESS = "server.bind.address";
    String ACCOUNT_LOCK_TIMEOUT_IN_MILLISECONDS = "account.lock.timeout.ms";

    static Integer intProperty(String key){
        return Integer.parseInt(SERVER_PROPERTIES.getProperty(key));
    }

    static String stringProperty(String key){
        return SERVER_PROPERTIES.getProperty(key);
    }
}
