package com.etiennek.rq.client;

import java.util.NoSuchElementException;
import java.util.concurrent.Future;

import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
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
import com.etiennek.rq.server.providers.ObjectMapperProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.common.base.Optional;

public class RqClient implements QueueService, AutoCloseable {

  private Client client;
  private WebTarget baseWebTarget;

  public RqClient(String baseUri) {
    client = ClientBuilder.newClient(
        new ClientConfig().connectorProvider(new JettyConnectorProvider()).register(JacksonJaxbJsonProvider.class,
            MessageBodyReader.class, MessageBodyWriter.class)).register(ObjectMapperProvider.class);
    baseWebTarget = client.target(baseUri);
  }

  @Override
  public Observable<Optional<Message>> hold(String queueName, int secondsToHold) {

    final AsyncInvoker asyncInvoker = baseWebTarget.path("queues").path(queueName)
        .queryParam("secondsToHold", secondsToHold).request(MediaType.APPLICATION_JSON_TYPE).async();

    return Observable.create(new OnSubscribeFunc<Optional<Message>>() {

      @Override
      public Subscription onSubscribe(final Observer<? super Optional<Message>> t1) {
        final Future<Optional<Message>> future = asyncInvoker.get(new InvocationCallback<Optional<Message>>() {

          public void completed(Optional<Message> message) {
            t1.onNext(message);
            t1.onCompleted();
          }

          public void failed(Throwable throwable) {
            t1.onError(throwable);
          }

        });

        return new Subscription() {
          @Override
          public void unsubscribe() {
            future.cancel(true);
          }
        };

      }
    });
  }

  @Override
  public Observable<Void> add(String queueName, final Message message) {
    final AsyncInvoker asyncInvoker = baseWebTarget.path("queues").path(queueName)
        .request(MediaType.APPLICATION_JSON_TYPE).async();

    return Observable.create(new OnSubscribeFunc<Void>() {
      @Override
      public Subscription onSubscribe(final Observer<? super Void> t1) {

        final Future<Void> future = asyncInvoker.post(Entity.entity(message, MediaType.APPLICATION_JSON_TYPE),
            new InvocationCallback<Void>() {

              @Override
              public void completed(Void response) {
                t1.onNext(response);
                t1.onCompleted();
              }

              @Override
              public void failed(Throwable throwable) {
                t1.onError(throwable);
              }
            });

        return new Subscription() {
          @Override
          public void unsubscribe() {
            future.cancel(true);
          }
        };

      }
    });
  }

  @Override
  public Observable<Void> deleteHeldMessage(String id) throws NoSuchElementException {
    final AsyncInvoker asyncInvoker = baseWebTarget.path("queues").path("heldMessage").path(id)
        .request(MediaType.APPLICATION_JSON_TYPE).async();

    return Observable.create(new OnSubscribeFunc<Void>() {
      @Override
      public Subscription onSubscribe(final Observer<? super Void> t1) {

        final Future<Void> future = asyncInvoker.delete(new InvocationCallback<Void>() {

          @Override
          public void completed(Void response) {
            t1.onNext(response);
            t1.onCompleted();
          }

          @Override
          public void failed(Throwable throwable) {
            t1.onError(throwable);
          }
        });

        return new Subscription() {
          @Override
          public void unsubscribe() {
            future.cancel(true);
          }
        };

      }
    });
  }

  @Override
  public void close() {
    client.close();
  }

}
