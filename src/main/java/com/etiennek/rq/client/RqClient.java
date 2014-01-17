package com.etiennek.rq.client;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jetty.connector.JettyConnectorProvider;

import rx.Observable;
import rx.Observable.OnSubscribeFunc;
import rx.Observer;
import rx.Subscription;

import com.etiennek.rq.api.QueueService;
import com.etiennek.rq.api.dtos.Message;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;

public class RqClient implements QueueService {

  private Client client;
  private WebTarget baseWebTarget;

  public RqClient(int port) {
    client = ClientBuilder.newClient(new ClientConfig().connectorProvider(
        new JettyConnectorProvider()).register(JacksonJaxbJsonProvider.class,
        MessageBodyReader.class, MessageBodyWriter.class));

    baseWebTarget = client.target("http://localhost:" + port);
  }

  @Override
  public Observable<Optional<Message>> hold(String queueName, int secondsToHold) {

    final AsyncInvoker asyncInvoker = baseWebTarget.path("queues").path(queueName)
        .queryParam("secondsToHold", secondsToHold).request(MediaType.APPLICATION_JSON_TYPE)
        .async();

    try {
      asyncInvoker.get(new InvocationCallback<Message>() {

        public void completed(Message message) {
          System.out.println(message);
        }

        public void failed(Throwable throwable) {
          throwable.printStackTrace();
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  public Observable<Void> add(String queueName, Message message) {
    WebTarget webTarget = baseWebTarget.path("queues").path(queueName);

    Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
    Response response = invocationBuilder.post(Entity.entity(message,
        MediaType.APPLICATION_JSON_TYPE));

    System.out.println(response.getStatus());
    System.out.println(response.readEntity(Void.class));

    return null;
  }

  @Override
  public Observable<Void> deleteHeldMessage(String id) throws NoSuchElementException {
    // TODO Auto-generated method stub
    return null;
  }

}
