package com.etiennek.rq.client;

import java.util.NoSuchElementException;

import rx.Observable;

import com.etiennek.rq.api.QueueService;
import com.etiennek.rq.api.dtos.Message;
import com.google.common.base.Optional;

public class RqClient implements QueueService {

  @Override
  public Observable<Optional<Message>> hold(String queueName, int secondsToHold) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Observable<Void> add(String queueName, Message message) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Observable<Void> deleteHeldMessage(String id) throws NoSuchElementException {
    // TODO Auto-generated method stub
    return null;
  }

}
