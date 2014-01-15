package com.etiennek.rq.server.services;

import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.jvnet.hk2.annotations.Service;

import rx.Observable;

import com.etiennek.rq.api.QueueService;
import com.etiennek.rq.api.dtos.HeldMessage;
import com.etiennek.rq.api.dtos.Message;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.AbstractScheduledService;

import static com.google.common.base.Preconditions.*;

@Service
public class MemoryQueueService extends AbstractScheduledService implements QueueService {

  private final AtomicLong heldIdAutoInc = new AtomicLong();

  private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> queues = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, HeldContainer> heldCache = new ConcurrentHashMap<>();

  @Override
  public Observable<Optional<HeldMessage>> hold(String queueName, int secondsToHold) {
    checkNotNull(queueName, "queueName");

    Queue<String> queue = queues.get(queueName);
    if (queue == null) {
      return Observable.from(Optional.<HeldMessage> absent());
    }

    String messageBody = queue.poll();
    if (messageBody == null) {
      return Observable.from(Optional.<HeldMessage> absent());
    }
    String id = Long.toString(heldIdAutoInc.incrementAndGet());
    heldCache.put(id, new HeldContainer(new Date().getTime() + secondsToHold * 1000, queueName,
        messageBody));
    return Observable.from(Optional.of(new HeldMessage(id, messageBody)));
  }

  @Override
  public Observable<Void> add(String queueName, Message message) {
    checkNotNull(queueName, "queueName");
    checkNotNull(message, "message");

    queues.putIfAbsent(queueName, new ConcurrentLinkedQueue<String>());
    queues.get(queueName).add(message.getMessageBody());
    return Observable.from((Void) null);
  }

  @Override
  public Observable<Void> deleteHeldMessage(String id) throws NoSuchElementException {
    checkNotNull(id, "id");

    HeldContainer container = heldCache.remove(id);
    if (container == null) {
      throw new NoSuchElementException("No HeldMessage with ID " + id);
    }
    return Observable.from((Void) null);
  }

  @Override
  protected void runOneIteration() {
    Iterator<String> iter = heldCache.keySet().iterator();
    while (iter.hasNext()) {
      try {
        String id = iter.next();
        HeldContainer container = heldCache.get(id);
        if (container != null && (new Date().getTime() > container.getHoldUntil())) {
          container = heldCache.remove(id);
          if (container != null) {
            add(container.getQueueName(), new Message(container.getMessageBody()));
          }
        }
      } catch (NoSuchElementException e) {
      }
    }
  }

  @Override
  protected Scheduler scheduler() {
    return Scheduler.newFixedRateSchedule(0, 100, TimeUnit.MILLISECONDS);
  }

  private static class HeldContainer {
    private long holdUntil;
    private String queueName;
    private String messageBody;

    public HeldContainer(long holdUntil, String queueName, String messageBody) {
      this.holdUntil = holdUntil;
      this.queueName = queueName;
      this.messageBody = messageBody;
    }

    public long getHoldUntil() {
      return holdUntil;
    }

    public String getQueueName() {
      return queueName;
    }

    public String getMessageBody() {
      return messageBody;
    }

  }

}
