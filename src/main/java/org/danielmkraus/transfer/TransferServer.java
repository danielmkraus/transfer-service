package org.danielmkraus.transfer;

import io.undertow.Undertow;
import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.danielmkraus.transfer.ServerProperties.*;

public class TransferServer implements AutoCloseable {
    private final UndertowJaxrsServer server;

    public static void main(String[] args) {
        startServer();
    }

    private static void loadServerProperties() {
        try {
            InputStream serverPropertiesFile =
                    TransferServer.class
                            .getClassLoader()
                            .getResourceAsStream("./application.properties");
            Objects.requireNonNull(serverPropertiesFile);
            SERVER_PROPERTIES.load(serverPropertiesFile);
        } catch (IOException e) {
            throw new IllegalStateException("Fail to load application.properties");
        }
    }

    public static TransferServer startServer() {
        loadServerProperties();
        TransferServer server = new TransferServer(
                intProperty(SERVER_PORT),
                stringProperty(SERVER_BIND_ADDRESS));
        server.deployApplication();
        return server;
    }

    private TransferServer(Integer port, String host) {
        server = new UndertowJaxrsServer().start(
                Undertow.builder()
                        .addHttpListener(port, host));
    }

    private void deployApplication() {
        ResteasyDeployment deployment = new ResteasyDeploymentImpl();
        deployment.setApplicationClass(TransferApplication.class.getName());
        server.deploy(server.undertowDeployment(deployment, "/rest")
                .setClassLoader(TransferServer.class.getClassLoader())
                .setContextPath("/")
                .setDeploymentName("TransferService"));
    }

    @Override
    public void close() {
        server.stop();
    }
}