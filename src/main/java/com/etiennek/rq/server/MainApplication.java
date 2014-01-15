package com.etiennek.rq.server;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.server.ResourceConfig;

import com.etiennek.rq.api.QueueService;
import com.etiennek.rq.server.binders.MainBinder;
import com.etiennek.rq.server.resources.QueueResource;
import com.etiennek.rq.server.services.MemoryQueueService;

public class MainApplication extends ResourceConfig {

  @Inject
  public MainApplication(ServiceLocator serviceLocator) {
    // packages("com.etiennek.rq");
    // GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
    // GuiceIntoHK2Bridge guiceBridge =
    // serviceLocator.getService(GuiceIntoHK2Bridge.class);
    // guiceBridge.bridgeGuiceInjector(Main.injector);

    register(new MainBinder());
    packages("com.etiennek.rq");
  }

}
