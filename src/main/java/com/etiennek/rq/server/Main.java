package com.etiennek.rq.server;

import java.util.logging.Level;
import java.util.logging.LogManager;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class Main {

  public static void main(String[] args) throws Exception {
    LogManager.getLogManager().reset();
    SLF4JBridgeHandler.install();
    java.util.logging.Logger.getLogger("global").setLevel(Level.FINEST);

    String webPort = System.getenv("PORT");
    if (webPort == null || webPort.isEmpty()) {
      webPort = "8080";
    }

    final Server server = new Server(Integer.valueOf(webPort));
    final WebAppContext root = new WebAppContext();

    root.setContextPath("/");
    root.setParentLoaderPriority(true);

    final String webappDirLocation = "src/main/webapp/";
    root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
    root.setResourceBase(webappDirLocation);

    server.setHandler(root);

    server.start();
    server.join();
  }

}
