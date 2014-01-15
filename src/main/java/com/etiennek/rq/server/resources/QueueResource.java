package com.etiennek.rq.server.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import rx.util.functions.Action1;

import com.etiennek.rq.api.QueueService;
import com.etiennek.rq.api.dtos.HeldMessage;
import com.etiennek.rq.api.dtos.Message;
import com.etiennek.rq.server.services.MemoryQueueService;
import com.google.common.base.Optional;

@Path("queues")
public class QueueResource {

  private static final QueueService queueService = new MemoryQueueService();

  private String queueName = "SOME_QUEUE";

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
  @Path("add")
  // TODO: remove this path
  public void add(@Suspended final AsyncResponse asyncResponse) {
    queueService.add(queueName, new Message("This is a message :)")).subscribe(new Action1<Void>() {

      @Override
      public void call(Void heldMessage) {
        asyncResponse.resume("OK");
      }

    });
  }

}
