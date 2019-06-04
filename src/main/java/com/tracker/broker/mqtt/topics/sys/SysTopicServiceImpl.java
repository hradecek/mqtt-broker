package com.tracker.broker.mqtt.topics.sys;

import com.tracker.broker.redis.RedisServiceHelper;
import com.tracker.broker.redis.RedisTopicsHelper;

import io.reactivex.Completable;
import io.reactivex.Maybe;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.redis.RedisClient;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;

import java.util.function.Consumer;

import static com.tracker.broker.mqtt.subscription.SubscriptionMessageSourceHelper.publishMessage;

/**
 * {@link SysTopicService $SYS topic service} implementation.
 */
public class SysTopicServiceImpl implements SysTopicService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SysTopicService.class);

    private static final long DEFAULT_CLIENTS_MAXIMUM = 0L;

    private static final String SYS = "$SYS";
    private static final String BROKER = "broker";
    private static final String CLIENTS = "clients";
    private static final String CONNECTED = "connected";
    private static final String DISCONNECTED = "disconnected";
    private static final String MAXIMUM = "maximum";
    private static final String TOTAL = "total";
    private static final String MESSAGES = "messages";
    private static final String RECEIVED = "received";
    private static final String SENT = "sent";
    private static final String BROKER_CLIENTS_CONNECTED = RedisTopicsHelper.toRedis(SYS, BROKER, CLIENTS, CONNECTED);
    private static final String BROKER_CLIENTS_DISCONNECTED = RedisTopicsHelper.toRedis(SYS, BROKER, CLIENTS, DISCONNECTED);
    private static final String BROKER_CLIENTS_MAXIMUM = RedisTopicsHelper.toRedis(SYS, BROKER, CLIENTS, MAXIMUM);
    private static final String BROKER_CLIENTS_TOTAL = RedisTopicsHelper.toRedis(SYS, BROKER, CLIENTS, TOTAL);
    private static final String BROKER_MESSAGES_RECEIVED = RedisTopicsHelper.toRedis(SYS, BROKER, MESSAGES, RECEIVED);
    private static final String BROKER_LOAD_BYTES_SENT = RedisTopicsHelper.toRedis(SYS, BROKER, MESSAGES, SENT);

    private EventBus eventBus;
    private RedisClient redisClient;

    /**
     * Constructor
     *
     * @param eventBus Vert.x's event bus instance
     * @param discovery service discovery instance
     * @param readyHandler async result
     */
    SysTopicServiceImpl(EventBus eventBus, ServiceDiscovery discovery, Handler<AsyncResult<SysTopicService>> readyHandler) {
        this.eventBus = eventBus;
        RedisServiceHelper.getClient(discovery)
                          .subscribe(redisClient -> {
                              this.redisClient = redisClient;
                              readyHandler.handle(Future.succeededFuture(this));
                          }, ex -> readyHandler.handle(Future.failedFuture(ex)))
                          .dispose();
    }

    @Override
    public SysTopicService connectClient(String clientId, Handler<AsyncResult<Void>> resultHandler) {
        addNewClient(clientId).andThen(recomputeMaximum())
                              .andThen(incReceivedMessages())
                              .subscribe(() -> resultHandler.handle(Future.succeededFuture()),
                                         ex -> resultHandler.handle(Future.failedFuture(ex)));
        return this;
    }

    private Completable addNewClient(String clientId) {
        return redisClient.rxSadd(BROKER_CLIENTS_CONNECTED, clientId)
                          .doOnSuccess(checkAddNewClient(clientId)::accept)
                          .ignoreElement()
                          .doOnComplete(this::publishConnected);
    }

    private Consumer<Long> checkAddNewClient(String clientId) {
        return result -> {
            if (result == 0L) {
                LOGGER.warn(String.format("Client '%s' is already connected.", clientId));
            }
        };
    }

    private void publishConnected() {
        redisClient.rxSmembers(BROKER_CLIENTS_CONNECTED)
                .flatMapCompletable(members ->
                        publishMessage(eventBus, new JsonObject().put(BROKER_CLIENTS_CONNECTED, members))).subscribe();
    }

    private Completable recomputeMaximum() {
        return redisClient.rxScard(BROKER_CLIENTS_CONNECTED)
                          .flatMapMaybe(connected ->
                                  getClientsMaximum()
                                          .flatMap(maximum -> setClientsMaximum(connected, maximum)))
                          .doOnSuccess(this::publishMaximum)
                          .ignoreElement();
    }

    private Maybe<Long> getClientsMaximum() {
        return redisClient.rxGet(BROKER_CLIENTS_MAXIMUM)
                          .map(Long::valueOf)
                          .defaultIfEmpty(DEFAULT_CLIENTS_MAXIMUM);
    }

    private Maybe<Long> setClientsMaximum(long connected, long maximum) {
        return connected > maximum
            ? redisClient.rxSet(BROKER_CLIENTS_MAXIMUM, String.valueOf(connected)).andThen(Maybe.just(connected))
            : Maybe.empty();
    }

    private void publishMaximum(long maximum) {
        publishMessage(eventBus, new JsonObject().put(BROKER_CLIENTS_MAXIMUM, maximum)).subscribe();
    }

    private Completable incReceivedMessages() {
        return redisClient.rxIncr(BROKER_MESSAGES_RECEIVED)
                          .doOnError(ex -> LOGGER.error(String.format("Cannot increment '%s'.", BROKER_MESSAGES_RECEIVED), ex))
                          .ignoreElement();
    }

    @Override
    public SysTopicService closeClient(String clientId, Handler<AsyncResult<Void>> resultHandler) {
        redisClient.rxSmove(BROKER_CLIENTS_CONNECTED, BROKER_CLIENTS_DISCONNECTED, clientId)
                   .doOnSuccess(checkCloseClient(clientId)::accept)
                   .doOnError(ex -> LOGGER.error(String.format("Cannot move redisClient '%s' from '%s' to %s'.", clientId, BROKER_CLIENTS_CONNECTED, BROKER_CLIENTS_DISCONNECTED), ex))
                   .ignoreElement()
                   .andThen(incReceivedMessages())
                   .subscribe(() -> resultHandler.handle(Future.succeededFuture()),
                              ex -> resultHandler.handle(Future.failedFuture(ex)));
        return this;
    }

    private Consumer<Long> checkCloseClient(String clientId) {
        return result -> {
            if (result == 0L) {
                LOGGER.warn(String.format("Client '%s' not found in %s.", clientId, BROKER_CLIENTS_CONNECTED));
            }
        };
    }

    @Override
    public SysTopicService disconnectClient(String clientId, Handler<AsyncResult<Void>> resultHandler) {
        redisClient.rxSrem(BROKER_CLIENTS_CONNECTED, clientId)
                   .doOnSuccess(result -> {
                       if (result == 0L) {
                          LOGGER.warn(String.format("Client %s was not connected.", clientId));
                       }
                   })
                   .doOnError(ex -> LOGGER.error(String.format("Cannot remove redisClient '%s' from '%s'.", clientId, BROKER_CLIENTS_CONNECTED), ex))
                   .ignoreElement()
                   .andThen(incReceivedMessages())
                   .subscribe(() -> resultHandler.handle(Future.succeededFuture()),
                              ex -> resultHandler.handle(Future.failedFuture(ex)));

        return this;
    }

    @Override
    public SysTopicService receivedMessage(Handler<AsyncResult<Void>> resultHandler) {
        return this;
    }

    @Override
    public SysTopicService sentMessage(Handler<AsyncResult<Void>> resultHandler) {
        return this;
    }
//        redisClient.multi(multiResult -> {
//            if (multiResult.succeeded()) {
//                redisClient.incr(BROKER_MESSAGES_RECEIVED, result1 -> {
//                    if (result1.succeeded()) {
//                        redisClient.incrby(BROKER_LOAD_BYTES_RECEIVED, String.valueOf(bytes), result2 -> {
//                            if (result2.succeeded()) {
//                                redisClient.exec(execResult -> {
//                                    if (execResult.succeeded()) {
//                                        resultHandler.handle(Future.succeededFuture());
//                                    } else {
//                                        redisClient.discard(discardResult -> { });
//                                        resultHandler.handle(Future.failedFuture(execResult.cause()));
//                                    }
//                                });
//                            } else {
//                                redisClient.discard(discardResult -> { });
//                                resultHandler.handle(Future.failedFuture(result2.cause()));
//                            }
//                        });
//                    } else {
//                        redisClient.discard(discardResult -> { });
//                        resultHandler.handle(Future.failedFuture(result1.cause()));
//                    }
//                });
//            } else {
//                resultHandler.handle(Future.failedFuture(multiResult.cause()));
//            }
//        });
//
//        return this;
//    }
//
//    @Override
//    public SysTopicService addCoordinates(String clientId, double lat, double lng, Handler<AsyncResult<Void>> resultHandler) {
//
//        return this;
//    }
}
