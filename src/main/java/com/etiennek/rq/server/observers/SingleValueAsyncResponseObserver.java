package com.etiennek.rq.server.observers;

import javax.ws.rs.container.AsyncResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observer;

public class SingleValueAsyncResponseObserver<T> implements Observer<T> {

  private final Logger logger = LoggerFactory.getLogger(SingleValueAsyncResponseObserver.class);

  private final AsyncResponse asyncResponse;
  private T responseObject = null;

  public SingleValueAsyncResponseObserver(AsyncResponse asyncResponse) {
    this.asyncResponse = asyncResponse;
  }

  @Override
  public void onCompleted() {
    asyncResponse.resume(responseObject);
  }

  @Override
  public void onNext(T args) {
    responseObject = args;
  }

  @Override
  public void onError(Throwable e) {
    logger.error("Unknown error occured", e);
    asyncResponse.resume(e);
  }

}
