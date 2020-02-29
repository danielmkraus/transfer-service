package org.danielmkraus.transfer;

import io.undertow.Undertow;
import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

import static org.danielmkraus.transfer.ServerProperties.*;

public class TransferServer {
    private final UndertowJaxrsServer server;

    public static void main(String[] args) {
        startServer();
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

    public void stop() {
        server.stop();
    }
}