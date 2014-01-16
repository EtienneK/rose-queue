package com.etiennek.rq.server.resources;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

import com.etiennek.rq.api.QueueService;
import com.etiennek.rq.api.dtos.Message;
import com.etiennek.rq.server.observers.SingleValueAsyncResponseObserver;
import com.google.common.base.Optional;

@Singleton
@Path("queues")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class QueueResource {

  private final QueueService queueService;

  @Inject
  public QueueResource(QueueService queueService) {
    this.queueService = queueService;
  }

  @GET
  @Path("{queueName}")
  public void hold(@Suspended final AsyncResponse asyncResponse, @PathParam("queueName") String queueName,
      @DefaultValue("10") @QueryParam("secondsToHold") int secondsToHold) {
    queueService.hold(queueName, secondsToHold).subscribe(
        new SingleValueAsyncResponseObserver<Optional<Message>>(asyncResponse));
  }

  @POST
  @Path("{queueName}")
  public void add(@Suspended final AsyncResponse asyncResponse, @PathParam("queueName") String queueName,
      Message message) {
    queueService.add(queueName, message).subscribe(new SingleValueAsyncResponseObserver<Void>(asyncResponse));
  }

  @DELETE
  @Path("heldMessage/{id}")
  public void delete(@Suspended final AsyncResponse asyncResponse, @PathParam("id") String id) {
    queueService.deleteHeldMessage(id).subscribe(new SingleValueAsyncResponseObserver<Void>(asyncResponse));
  }
}
