package com.etiennek.rq.server;

import org.junit.After;
import org.junit.Before;

import rx.util.functions.Action1;

import com.google.common.base.Throwables;

public abstract class AbstractIntegrationTest {
  protected static final int PORT = 7357;
  protected static final String BASE_URI = "http://localhost:" + PORT;

  protected Throwable error = null;

  protected static void startServer() throws Exception {
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

  protected static void stopServer() throws Exception {
    RqApplication.stop();
  }

  @Before
  public void init() {
    error = null;
  }

  @After
  public void cleanup() throws Throwable {
    if (error != null) {
      throw error;
    }
  }

  public class OnErrorAction implements Action1<Throwable> {
    @Override
    public void call(Throwable t1) {
      error = t1;
      t1.printStackTrace();
    }
  }

}
