package com.etiennek.rq.server.services;

import static java.lang.System.*;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jvnet.hk2.annotations.Service;

import rx.Observable;

import com.etiennek.rq.api.QueueService;
import com.etiennek.rq.api.dtos.Message;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.AbstractScheduledService;

import static com.google.common.base.Preconditions.*;

@Service
public class InMemoryQueueService extends AbstractScheduledService implements QueueService {

  private final ExecutorService executorService;

  private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Message>> queues = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, HeldContainer> heldCache = new ConcurrentHashMap<>();

  @Inject
  public InMemoryQueueService(ExecutorService executorService) {
    this.executorService = executorService;
    start();
  }

  @Override
  public Observable<Optional<Message>> hold(String queueName, int secondsToHold) {
    checkNotNull(queueName, "queueName");

    Queue<Message> queue = queues.get(queueName);
    if (queue == null) {
      return Observable.from(Optional.<Message> absent());
    }

    Message message = queue.poll();
    if (message == null) {
      return Observable.from(Optional.<Message> absent());
    }
    heldCache.put(message.getId(), new HeldContainer(currentTimeMillis() + secondsToHold * 1000, queueName, message));
    return Observable.from(Optional.of(message));
  }

  @Override
  public Observable<Void> add(String queueName, Message message) {
    checkNotNull(queueName, "queueName");
    checkNotNull(message, "message");

    if (!queues.contains(queueName)) {
      queues.putIfAbsent(queueName, new ConcurrentLinkedQueue<Message>());
    }
    queues.get(queueName).add(message.createCopyWithId(UUID.randomUUID().toString()));
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
        if (container != null && (currentTimeMillis() > container.getHoldUntil())) {
          container = heldCache.remove(id);
          if (container != null) {
            add(container.getQueueName(), container.getMessage());
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
    private Message message;

    public HeldContainer(long holdUntil, String queueName, Message message) {
      this.holdUntil = holdUntil;
      this.queueName = queueName;
      this.message = message;
    }

    public long getHoldUntil() {
      return holdUntil;
    }

    public String getQueueName() {
      return queueName;
    }

    public Message getMessage() {
      return message;
    }

  }

}
