package com.etiennek.rq.server.binders;

import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.etiennek.rq.api.QueueService;
import com.etiennek.rq.server.services.InMemoryQueueService;

public class MainBinder extends AbstractBinder {

  private ExecutorService executorService;

  public MainBinder(ExecutorService executorService) {
    this.executorService = executorService;
  }

  @Override
  protected void configure() {
    bind(executorService).to(ExecutorService.class);
    bind(InMemoryQueueService.class).to(QueueService.class).in(Singleton.class);
  }

}
