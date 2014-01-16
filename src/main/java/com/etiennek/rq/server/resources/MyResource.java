package com.etiennek.rq.server.resources;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Path("myresource")
public class MyResource {

  private final AtomicInteger atomicInteger;
  private final Logger logger = LoggerFactory.getLogger(MyResource.class);

  public MyResource() {
    logger.info(getClass().getName() + " instantiated!!!! <<<<<<<<");
    atomicInteger = new AtomicInteger();
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getIt() {

    try {
      logger.info(atomicInteger.incrementAndGet() + " | " + Thread.currentThread().getId());
      Thread.sleep(60000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return "Got it!";
  }
}
