package com.etiennek.rq.server.integration;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import rx.Observable;
import rx.util.functions.Action1;
import rx.util.functions.Func1;

import com.etiennek.rq.api.dtos.Message;
import com.etiennek.rq.client.RqClient;
import com.etiennek.rq.server.AbstractIntegrationTest;
import com.google.common.base.Optional;

public class RqClientFunctionalTest extends AbstractIntegrationTest {

  private static RqClient client;

  @BeforeClass
  public static void initStatic() throws Exception {
    startServer();
    client = new RqClient(BASE_URI);
  }

  @AfterClass
  public static void destroyStatic() throws Exception {
    stopServer();
    client.close();
  }

  @Test
  public void test() throws InterruptedException {
    final AtomicInteger count = new AtomicInteger();
    final int numberOfRuns = 5;

    final String queueName = "test.queue";
    final String messageBody = "This is a test message you all :D";

    for (int i = 0; i < numberOfRuns; ++i) {

      // ADD a message to the queue
      client.add(queueName, new Message(messageBody)).flatMap(new Func1<Void, Observable<Optional<Message>>>() {
        // and then HOLD it
        @Override
        public Observable<Optional<Message>> call(Void t1) {
          return client.hold(queueName, 2);
        }
      })

      .flatMap(new Func1<Optional<Message>, Observable<Void>>() {
        // DELETE the held message
        @Override
        public Observable<Void> call(Optional<Message> t1) {
          Assert.assertEquals(messageBody, t1.get().getMessageBody());
          return client.deleteHeldMessage(t1.get().getId());
        }
      })

      .flatMap(new Func1<Void, Observable<Optional<Message>>>() {
        // HOLD another message
        @Override
        public Observable<Optional<Message>> call(Void t1) {
          try {
            Thread.sleep(3000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          return client.hold(queueName, 60);
        }
      })

      .subscribe(new Action1<Optional<Message>>() {
        // there should be NO messages to HOLD
        @Override
        public void call(Optional<Message> t1) {
          Assert.assertFalse(t1.isPresent());
          count.incrementAndGet();
        }
      }, new OnErrorAction());

    }

    await().atMost(4, SECONDS).until(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return count.get() >= numberOfRuns;
      }
    });

  }
}
