package com.etiennek.rq.server.integration;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rx.Observable;
import rx.util.functions.Action1;
import rx.util.functions.Func1;

import com.etiennek.rq.api.dtos.Message;
import com.etiennek.rq.client.RqClient;
import com.etiennek.rq.server.AbstractIntegrationTest;
import com.google.common.base.Optional;

public class RqClientBenchmarkTest extends AbstractIntegrationTest {

  private static RqClient client;

  private AtomicLong totalTime;
  private AtomicInteger errorCount;

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

  @Before
  @Override
  public void init() {
    super.init();
    totalTime = new AtomicLong();
    errorCount = new AtomicInteger();
  }

  @Test
  public void test() throws InterruptedException {
    final AtomicInteger count = new AtomicInteger();
    final int numberOfRuns = 1000;

    final String queueName = "test.queue";
    final String messageBody = "This is a test message you all :D";

    for (int i = 0; i < numberOfRuns; ++i) {
      final long startTime = System.currentTimeMillis();

      // ADD a message to the queue
      client.add(queueName, new Message(messageBody)).flatMap(new Func1<Void, Observable<Optional<Message>>>() {
        // and then HOLD it
        @Override
        public Observable<Optional<Message>> call(Void t1) {
          return client.hold(queueName, 60);
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

      .subscribe(new Action1<Void>() {
        @Override
        public void call(Void t1) {
          incrementCount(count, startTime, numberOfRuns);
        }
      },

      new OnErrorAction() {
        @Override
        public void call(Throwable t1) {
          errorCount.incrementAndGet();
          incrementCount(count, startTime, numberOfRuns);
          // super.call(t1);
        }
      });

    }

    await().until(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return count.get() >= numberOfRuns;
      }
    });

  }

  private void incrementCount(AtomicInteger count, long startTime, int numberOfRuns) {
    totalTime.addAndGet(System.currentTimeMillis() - startTime);
    int countInc = count.incrementAndGet();
    if (countInc >= numberOfRuns) {
      System.out.println(String.format(
          "Number of Runs: [%s]; Total Time: [%s]; Time per full request run: [%s]; Error count: [%s]", numberOfRuns,
          totalTime, (totalTime.get() / (double) numberOfRuns) / 1000.0, errorCount));
    }
  }
}
