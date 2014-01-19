package com.etiennek.rq.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.etiennek.rq.server.binders.MainBinder;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.common.util.concurrent.MoreExecutors;

public class RqApplication extends ResourceConfig {

  private static Server server;
  private static ExecutorService executorService;

  public RqApplication() {
    if (executorService == null) {
      throw new NullPointerException(
          "ExecutorService is null. Are you running this as a WAR? Because that is not supported yet");
    }
    register(new MainBinder(executorService));
    packages(getClass().getPackage().getName(), JacksonJaxbJsonProvider.class.getPackage().getName());
  }

  public static void main(String[] args) throws Exception {
    String port = System.getenv("PORT");
    if (port == null || port.isEmpty()) {
      port = "8080";
    }

    start(Integer.parseInt(port));
  }

  public static void start(int port) throws Exception {
    LogManager.getLogManager().reset();
    SLF4JBridgeHandler.install();
    java.util.logging.Logger.getLogger("global").setLevel(Level.FINEST);

    server = createServer(port);

    WebAppContext webAppContext = new WebAppContext();

    webAppContext.setContextPath("/");
    webAppContext.setParentLoaderPriority(true);

    final String webappDirLocation = "src/main/webapp/";
    webAppContext.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
    webAppContext.setResourceBase(webappDirLocation);

    server.setHandler(webAppContext);

    server.start();
    server.join();
  }

  public static void stop() throws Exception {
    server.stop();
  }

  private static Server createServer(int port) {
    int acceptors = Runtime.getRuntime().availableProcessors() / 2;
    int selectors = Runtime.getRuntime().availableProcessors();
    int workers = Runtime.getRuntime().availableProcessors();

    int numberOfThreads = acceptors + selectors + workers;

    executorService = Executors.newFixedThreadPool(numberOfThreads);
    ExecutorThreadPool threadPool = new ExecutorThreadPool(executorService);

    Server server = new Server(threadPool);

    ServerConnector connector = new ServerConnector(server, acceptors, selectors);
    connector.setPort(port);
    server.setConnectors(new Connector[] { connector });

    return server;
  }

}
