package com.etiennek.rq.server;

import java.util.logging.Level;
import java.util.logging.LogManager;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.etiennek.rq.server.binders.MainBinder;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class RqApplication extends ResourceConfig {

  private static Server server;

  public RqApplication() {
    register(new MainBinder());
    packages(getClass().getPackage().getName(), JacksonJaxbJsonProvider.class.getPackage().getName());
  }

  public static void main(String[] args) throws Exception {
    LogManager.getLogManager().reset();
    SLF4JBridgeHandler.install();
    java.util.logging.Logger.getLogger("global").setLevel(Level.FINEST);

    server = createServer();

    WebAppContext webAppContext = new WebAppContext();

    webAppContext.setContextPath("/");
    webAppContext.setParentLoaderPriority(true);

    final String webappDirLocation = "src/main/webapp/";
    webAppContext.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
    webAppContext.setResourceBase(webappDirLocation);

    server.setHandler(webAppContext);

    start();
  }

  public static void start() throws Exception {
    server.start();
    server.join();
  }

  public static void stop() throws Exception {
    server.stop();
  }

  private static Server createServer() {
    int acceptors = Runtime.getRuntime().availableProcessors() / 2;
    int selectors = Runtime.getRuntime().availableProcessors();
    int workers = Runtime.getRuntime().availableProcessors();

    int numberOfThreads = acceptors + selectors + workers;

    Server server = new Server(new QueuedThreadPool(numberOfThreads, numberOfThreads));

    String port = System.getenv("PORT");
    if (port == null || port.isEmpty()) {
      port = "8080";
    }

    ServerConnector connector = new ServerConnector(server, acceptors, selectors);
    connector.setPort(Integer.valueOf(port));
    server.setConnectors(new Connector[] { connector });

    return server;
  }

}
