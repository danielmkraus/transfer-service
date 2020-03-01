package org.danielmkraus.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public final class ServerProperties {
    private static final Properties SERVER_PROPERTIES = new Properties();

    public static final String SERVER_PORT = "server.port";
    static final String SERVER_BIND_ADDRESS = "server.bind.address";
    static final String ACCOUNT_LOCK_TIMEOUT_IN_MILLISECONDS = "account.lock.timeout.ms";

    static Integer intProperty(String key){
        return Integer.parseInt(SERVER_PROPERTIES.getProperty(key));
    }

    public static String stringProperty(String key){
        return SERVER_PROPERTIES.getProperty(key);
    }

    static void loadServerProperties() {
        try {
            InputStream serverPropertiesFile =
                    TransferServer.class
                            .getClassLoader()
                            .getResourceAsStream("application.properties");
            Objects.requireNonNull(serverPropertiesFile);
            SERVER_PROPERTIES.load(serverPropertiesFile);
        } catch (IOException e) {
            throw new IllegalStateException("Fail to load application.properties");
        }
    }

    private ServerProperties(){
    }
}
