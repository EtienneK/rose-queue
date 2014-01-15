package com.etiennek.rq.server.binders;

import javax.inject.Singleton;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.etiennek.rq.api.QueueService;
import com.etiennek.rq.server.services.MemoryQueueService;

public class MainBinder extends AbstractBinder {

  @Override
  protected void configure() {
    bind(MemoryQueueService.class).to(QueueService.class).in(Singleton.class);
  }

}
