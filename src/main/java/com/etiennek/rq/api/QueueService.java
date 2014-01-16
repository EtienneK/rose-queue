package com.etiennek.rq.api;

import java.util.NoSuchElementException;

import org.jvnet.hk2.annotations.Contract;

import com.etiennek.rq.api.dtos.Message;
import com.google.common.base.Optional;

import rx.Observable;

@Contract
public interface QueueService {

  Observable<Optional<Message>> hold(String queueName, int secondsToHold);

  Observable<Void> add(String queueName, Message message);

  Observable<Void> deleteHeldMessage(String id) throws NoSuchElementException;

}
