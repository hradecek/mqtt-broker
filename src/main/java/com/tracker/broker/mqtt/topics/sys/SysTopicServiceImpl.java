package com.tracker.broker.mqtt.topics.sys;

import com.tracker.broker.redis.RedisServiceHelper;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.redis.RedisClient;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;

/**
 * TODO
 */
public class SysTopicServiceImpl implements SysTopicService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SysTopicService.class);

    private static final long DEFAULT_CLIENTS_MAXIMUM = 0L;

    /**
     * TODO
     */
    // $SYS
    private static final String BROKER_CLIENTS_CONNECTED = "broker:clients:connected";
    private static final String BROKER_CLIENTS_DISCONNECTED = "broker:clients:disconnected";
    private static final String BROKER_CLIENTS_MAXIMUM = "broker:clients:maximum";
//    private static final String BROKER_CLIENTS_TOTAL = "broker:clients:total";
//
//    private static final String BROKER_MESSAGES_RECEIVED = "broker:messages:received";
//    private static final String BROKER_LOAD_BYTES_SENT = "broker:load:bytes:sent";

    // Subscriptions
//    private static final String SUBSCRIPTIONS_CLIENTS = "subscriptions:";
//
//    private static final String CLIENTS_COORDINATES = "coordinates";
//    private static final UnaryOperator<String> CLIENTS_CLIENT = clientId -> String.format("clients:%s:", clientId);

    private RedisClient redisClient;

    /**
     * TODO
     */
    SysTopicServiceImpl(ServiceDiscovery discovery, Handler<AsyncResult<SysTopicService>> readyHandler) {
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
                              .subscribe(() -> resultHandler.handle(Future.succeededFuture()),
                                         ex -> resultHandler.handle(Future.failedFuture(ex)));
        return this;
    }

    private Completable addNewClient(String clientId) {
        return redisClient.rxSadd(BROKER_CLIENTS_CONNECTED, clientId)
                          .doOnSuccess(result -> {
                              if (result == 0L) {
                                  LOGGER.warn(String.format("Client '%s' is already connected.", clientId));
                              }
                          })
                          .ignoreElement();
    }

    // TODO: Use compose?
    private Completable recomputeMaximum() {
        return redisClient.rxScard(BROKER_CLIENTS_CONNECTED)
                          .flatMapCompletable(connected ->
                                  getClientsMaximum().flatMapCompletable(maximum -> setClientsMaximum(connected, maximum)));
    }

    private Maybe<Long> getClientsMaximum() {
        return redisClient.rxGet(BROKER_CLIENTS_MAXIMUM)
                          .map(Long::valueOf)
                          .defaultIfEmpty(DEFAULT_CLIENTS_MAXIMUM);
    }

    private Completable setClientsMaximum(long connected, long maximum) {
        return connected > maximum
            ? redisClient.rxSet(BROKER_CLIENTS_MAXIMUM, String.valueOf(connected))
            : Completable.complete();
    }

    @Override
    public SysTopicService closeClient(String clientId, Handler<AsyncResult<Void>> resultHandler) {
        redisClient.rxSmove(BROKER_CLIENTS_CONNECTED, BROKER_CLIENTS_DISCONNECTED, clientId)
                   .doOnSuccess(result -> {
                     if (result == 0L) {
                         LOGGER.warn(String.format("Client '%s' not found in %s.", clientId, BROKER_CLIENTS_CONNECTED));
                     }})
                   .doOnError(ex -> LOGGER.error(String.format("Cannot move redisClient '%s' from '%s' to %s'.", clientId, BROKER_CLIENTS_CONNECTED, BROKER_CLIENTS_DISCONNECTED)))
                   .ignoreElement()
                   .subscribe(() -> resultHandler.handle(Future.succeededFuture()),
                              ex -> resultHandler.handle(Future.failedFuture(ex)));
        return this;
    }

    @Override
    public SysTopicService disconnectClient(String clientId, Handler<AsyncResult<Void>> resultHandler) {
        redisClient.rxSrem(BROKER_CLIENTS_CONNECTED, clientId)
                   .doOnSuccess(result -> {
                       if (result == 0L) {
                          LOGGER.warn(String.format("Client %s was not connected.", clientId));
                       }
                   })
                   .doOnError(ex -> LOGGER.error(String.format("Cannot remove redisClient '%s' from '%s'.", clientId, BROKER_CLIENTS_CONNECTED)))
                   .ignoreElement()
                   .subscribe(() -> resultHandler.handle(Future.succeededFuture()),
                              ex -> resultHandler.handle(Future.failedFuture(ex)));

        return this;
    }
//
//    @Override
//    public SysTopicService receivedMessage(int bytes, Handler<AsyncResult<Void>> resultHandler) {
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
//        final JsonObject value = new JsonObject().put("lat", String.valueOf(lat)).put("lng", String.valueOf(lng));
//        redisClient.lpush(Arrays.asList(CLIENTS_CLIENT.apply(clientId) + CLIENTS_COORDINATES, value.toString()), result -> {
//            if (result.succeeded()) {
//                if (result.result().toString().equals("OK")) {
//                    LOGGER.info(String.format("Added coordinates for %s: (%f, %f)", clientId, lat, lng));
//                }
//                resultHandler.handle(Future.succeededFuture());
//            } else {
//                LOGGER.error(String.format("Cannot add coordinates for %s: (%f, %f)", clientId, lat, lng));
//                resultHandler.handle(Future.failedFuture(result.cause()));
//            }
//        });
//
//        return this;
//    }
}
