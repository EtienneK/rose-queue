package com.etiennek.rq.client;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import rx.util.functions.Action1;

import com.etiennek.rq.api.dtos.Message;
import com.etiennek.rq.server.RqApplication;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;

public class RqClientTest {

  private static final int PORT = 7357;
  private static final String BASE_URI = "http://localhost:" + PORT;

  private static RqClient client;

  @BeforeClass
  public static void initStatic() throws Exception {
    client = new RqClient(BASE_URI);
    new Thread() {
      public void run() {
        try {
          RqApplication.start(PORT);
        } catch (Exception e) {
          Throwables.propagate(e);
        }
      };
    }.start();
    Thread.sleep(3000);
  }

  @AfterClass
  public static void destroyStatic() throws Exception {
    RqApplication.stop();
    client.close();
  }

  @Test
  public void test() throws InterruptedException {
    final AtomicInteger count = new AtomicInteger();
    final int numberOfRuns = 1;

    final String queueName = "test.queue";
    final String messageBody = "This is a test message you all :D";

    final Action1<Optional<Message>> sequence4 = new Action1<Optional<Message>>() {
      @Override
      public void call(Optional<Message> t1) {
        Assert.assertFalse(t1.isPresent());
        count.incrementAndGet();
      }
    };

    final Action1<Void> sequence3 = new Action1<Void>() {
      @Override
      public void call(Void t1) {
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        client.hold(queueName, 60).subscribe(sequence4);
      }
    };

    final Action1<Optional<Message>> sequence2 = new Action1<Optional<Message>>() {
      @Override
      public void call(Optional<Message> t1) {
        Assert.assertEquals(messageBody, t1.get().getMessageBody());
        client.deleteHeldMessage(t1.get().getId()).subscribe(sequence3);
      }
    };

    final Action1<Void> sequence1 = new Action1<Void>() {
      @Override
      public void call(Void t1) {
        client.hold(queueName, 2).subscribe(sequence2);
      }
    };

    for (int i = 0; i < numberOfRuns; ++i) {
      client.add(queueName, new Message(messageBody)).subscribe(sequence1);
    }

    await().atMost(4, SECONDS).until(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return count.get() >= numberOfRuns;
      }
    });

  }
}
