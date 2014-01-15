package com.etiennek.rq.api;

import java.util.NoSuchElementException;

import com.etiennek.rq.api.dtos.HeldMessage;
import com.etiennek.rq.api.dtos.Message;
import com.google.common.base.Optional;

import rx.Observable;

public interface QueueService {

  Observable<Optional<HeldMessage>> hold(String queueName, int secondsToHold);

  Observable<Void> add(String queueName, Message message);

  Observable<Void> deleteHeldMessage(String id) throws NoSuchElementException;

}
