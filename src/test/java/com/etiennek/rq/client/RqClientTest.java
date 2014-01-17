package com.etiennek.rq.client;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.etiennek.rq.api.dtos.Message;
import com.etiennek.rq.server.RqApplication;
import com.google.common.base.Throwables;

public class RqClientTest {

  private static final int port = 7357;

  @BeforeClass
  public static void initStatic() throws Exception {
    new Thread() {
      public void run() {
        try {
          RqApplication.start(port);
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
  }

  @Test
  public void test() {
    RqClient rqClient = new RqClient(port);
    rqClient.add("SomeQueue", new Message("Some string body"));
    rqClient.hold("SomeQueue", 60);
  }

}
