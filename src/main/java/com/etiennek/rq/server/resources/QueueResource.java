package com.etiennek.rq.server.resources;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.util.functions.Action1;

import com.etiennek.rq.api.QueueService;
import com.etiennek.rq.api.dtos.HeldMessage;
import com.etiennek.rq.api.dtos.Message;
import com.google.common.base.Optional;

@Singleton
@Path("queues")
public class QueueResource {

  private final Logger logger = LoggerFactory.getLogger(QueueResource.class);

  private final QueueService queueService;

  private String queueName = "SOME_QUEUE"; // TODO: remove

  @Inject
  public QueueResource(QueueService queueService) {
    this.queueService = queueService;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public void hold(@Suspended final AsyncResponse asyncResponse) {

    Action1<Optional<HeldMessage>> onSuccess = new Action1<Optional<HeldMessage>>() {
      public void call(Optional<HeldMessage> heldMessage) {
        asyncResponse.resume(heldMessage);
      }
    };

    Action1<Throwable> onError = new Action1<Throwable>() {
      public void call(Throwable t) {
        logger.error("Unknown exception occured", t);
        asyncResponse.resume(t);
      }
    };

    queueService.hold(queueName, 5).subscribe(onSuccess, onError);
  }

  @GET
  // TODO: remove this path
  @Path("add")
  @Produces(MediaType.APPLICATION_JSON)
  public void add(@Suspended final AsyncResponse asyncResponse) {
    queueService.add(queueName, new Message("This is a message :)")).subscribe(new Action1<Void>() {

      @Override
      public void call(Void heldMessage) {
        asyncResponse.resume(Arrays.asList(new String[] { "OK!", "World!" }));
      }

    });
  }

}
