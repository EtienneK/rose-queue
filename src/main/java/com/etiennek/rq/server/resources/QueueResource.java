package com.etiennek.rq.server.resources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import rx.util.functions.Action1;

import com.etiennek.rq.api.QueueService;
import com.etiennek.rq.api.dtos.HeldMessage;
import com.etiennek.rq.api.dtos.Message;
import com.google.common.base.Optional;

@Path("queues")
public class QueueResource {

  private QueueService queueService;

  private String queueName = "SOME_QUEUE";

  @Inject
  public QueueResource(QueueService queueService) {
    this.queueService = queueService;
  }

  @GET
  public void hold(@Suspended final AsyncResponse asyncResponse) {
    queueService.hold(queueName, 5).subscribe(new Action1<Optional<HeldMessage>>() {

      @Override
      public void call(Optional<HeldMessage> heldMessage) {
        asyncResponse.resume(heldMessage.orNull());
      }

    });
  }

  @GET
  // TODO: remove this path
  @Path("add")
  public void add(@Suspended final AsyncResponse asyncResponse) {
    queueService.add(queueName, new Message("This is a message :)")).subscribe(new Action1<Void>() {

      @Override
      public void call(Void heldMessage) {
        asyncResponse.resume("OK");
      }

    });
  }

}
