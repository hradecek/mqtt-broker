package com.tracker.broker.redis;

import com.tracker.broker.mqtt.subscription.Subscription;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.SocketAddress;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO
 */
public class RedisServiceImpl implements RedisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisServiceImpl.class);

    /**
     * TODO
     */
    // $SYS
    private static final String BROKER_LOAD_BYTES_RECEIVED = "broker:load:bytes:received";

    private static final String BROKER_MESSAGES_RECEIVED = "broker:messages:received";

    private static final String BROKER_LOAD_BYTES_SENT = "broker:load:bytes:sent";
    private static final String BROKER_CLIENTS_CONNECTED = "broker:clients:connected";
    private static final String BROKER_CLIENTS_DISCONNECTED = "broker:clients:disconnected";
    private static final String BROKER_CLIENTS_MAXIMUM = "broker:clients:maximum";

    private static final String SUBSCRIPTIONS_CLIENTS = "subscriptions:";

    private static final String CLIENTS_COORDINATES = "coordinates";
    private static final UnaryOperator<String> CLIENTS_CLIENT = clientId -> String.format("clients:%s:", clientId);

    private RedisAPI client;

    /**
     * TODO
     *
     * @param config
     * @param readyHandler
     */
    RedisServiceImpl(RedisConfig config, Handler<AsyncResult<RedisService>> readyHandler) {
        Redis.createClient(Vertx.vertx(),
                           new RedisOptions().setEndpoint(SocketAddress.inetSocketAddress(config.getPort(), config.getHost())))
             .connect(result -> {
                 if (result.succeeded()) {
                     client = RedisAPI.api(result.result());
                     readyHandler.handle(Future.succeededFuture(this));
                 } else {
                     LOGGER.error("Cannot connect to Redis ", result.cause());
                     readyHandler.handle(Future.failedFuture(result.cause()));
                 }
             });
    }

    @Override
    public RedisService addClient(String clientId, Handler<AsyncResult<Void>> resultHandler) {
        client.sadd(Arrays.asList(BROKER_CLIENTS_CONNECTED, clientId), result -> {
            if (result.succeeded()) {
                if (result.result().toInteger() != 0) {
                    LOGGER.info("Added new client: " + clientId);
                }
                resultHandler.handle(Future.succeededFuture());
            } else {
                LOGGER.error("Cannot add new client: " + clientId);
                resultHandler.handle(Future.failedFuture(result.cause()));
            }
        });

        return this;
    }

    @Override
    public RedisService getClientsConnected(Handler<AsyncResult<Integer>> resultHandler) {
        client.get(BROKER_CLIENTS_CONNECTED, result -> {
            if (result.succeeded()) {
                resultHandler.handle(Future.succeededFuture(result.result().toInteger()));
            } else {
                // TODO
                resultHandler.handle(Future.failedFuture(result.cause()));
            }
        });

        return this;
    }

    @Override
    public RedisService disconnectClient(String clientId, Handler<AsyncResult<Void>> resultHandler) {
        client.smove(BROKER_CLIENTS_CONNECTED, BROKER_CLIENTS_DISCONNECTED, clientId, result -> {
            if (result.succeeded()) {
                if (result.result().toInteger() == 0) {
                    LOGGER.info("Removed client: " + clientId);
                }
                resultHandler.handle(Future.succeededFuture());
            } else {
                LOGGER.error("Cannot remove client: " + clientId);
                resultHandler.handle(Future.failedFuture(result.cause()));
            }
        });

        return this;
    }

    @Override
    public RedisService remClient(String clientId, Handler<AsyncResult<Void>> resultHandler) {
        client.srem(Arrays.asList(BROKER_CLIENTS_CONNECTED, clientId), result -> {
            if (result.succeeded()) {
                if (result.result().toInteger() == 0) {
                    LOGGER.info("Removed client: " + clientId);
                }
                resultHandler.handle(Future.succeededFuture());
            } else {
                LOGGER.error("Cannot remove client: " + clientId);
                resultHandler.handle(Future.failedFuture(result.cause()));
            }
        });

        return this;
    }

    @Override
    public RedisService receivedMessage(int bytes, Handler<AsyncResult<Void>> resultHandler) {
        client.multi(multiResult -> {
            if (multiResult.succeeded()) {
                client.incr(BROKER_MESSAGES_RECEIVED, result1 -> {
                    if (result1.succeeded()) {
                        client.incrby(BROKER_LOAD_BYTES_RECEIVED, String.valueOf(bytes), result2 -> {
                            if (result2.succeeded()) {
                                client.exec(execResult -> {
                                    if (execResult.succeeded()) {
                                        resultHandler.handle(Future.succeededFuture());
                                    } else {
                                        client.discard(discardResult -> { });
                                        resultHandler.handle(Future.failedFuture(execResult.cause()));
                                    }
                                });
                            } else {
                                client.discard(discardResult -> { });
                                resultHandler.handle(Future.failedFuture(result2.cause()));
                            }
                        });
                    } else {
                        client.discard(discardResult -> { });
                        resultHandler.handle(Future.failedFuture(result1.cause()));
                    }
                });
            } else {
                resultHandler.handle(Future.failedFuture(multiResult.cause()));
            }
        });

        return this;
    }

    @Override
    public RedisService addCoordinates(String clientId, double lat, double lng, Handler<AsyncResult<Void>> resultHandler) {
        final JsonObject value = new JsonObject().put("lat", String.valueOf(lat)).put("lng", String.valueOf(lng));
        client.lpush(Arrays.asList(CLIENTS_CLIENT.apply(clientId) + CLIENTS_COORDINATES, value.toString()), result -> {
            if (result.succeeded()) {
                if (result.result().toString().equals("OK")) {
                    LOGGER.info(String.format("Added coordinates for %s: (%f, %f)", clientId, lat, lng));
                }
                resultHandler.handle(Future.succeededFuture());
            } else {
                LOGGER.error(String.format("Cannot add coordinates for %s: (%f, %f)", clientId, lat, lng));
                resultHandler.handle(Future.failedFuture(result.cause()));
            }
        });

        return this;
    }

    @Override
    public RedisService addSubscriptions(String clientId, Set<Subscription> subscriptions, Handler<AsyncResult<Void>> resultHandler) {
        client.sadd(subscriptionsArgs(clientId, subscriptions), result -> {
            if (result.succeeded()) {
                resultHandler.handle(Future.succeededFuture());
            } else {
                LOGGER.error(String.format("Cannot add subscriptions: %s for client %s", subscriptions, clientId));
                resultHandler.handle(Future.failedFuture(result.cause()));
            }
        });

        return this;
    }

    private static List<String> subscriptionsArgs(String clientId, Set<Subscription> subscriptions) {
        List<String> args = new ArrayList<>();
        args.add(subscriptionsTopicsKey(clientId));
        args.addAll(subscriptionsToList(subscriptions));

        return args;
    }

    private static List<String> subscriptionsToList(Set<Subscription> subscriptions) {
        return subscriptions.stream()
                            .map(subscription -> subscription.toJson().toString())
                            .collect(Collectors.toList());
    }

    @Override
    public RedisService removeSubscriptions(String clientId, Set<String> topicsName, Handler<AsyncResult<Void>> resultHandler) {
        String key = subscriptionsTopicsKey(clientId);
        client.smembers(key, result -> {
            if (result.succeeded()) {
                List<String> args = new ArrayList<>();
                args.add(key);
                args.addAll(unsubscriptions(topicsName, subscriptionsFromJsonArray(new JsonArray(result.result().toString()))));
                client.srem(args, remResult -> {
                    if (remResult.succeeded()) {
                        resultHandler.handle(Future.succeededFuture());
                    } else {
                        LOGGER.error(String.format("Cannot unsubscribe from: %s for client %s", topicsName, clientId));
                        resultHandler.handle(Future.failedFuture(remResult.cause()));
                    }
                });
            } else {
                LOGGER.error(String.format("Cannot get members of %s", key));
                resultHandler.handle(Future.failedFuture(result.cause()));
            }
        });

        return this;
    }

    private static List<String> unsubscriptions(Set<String> unsubscriptions, List<Subscription> subscriptions) {
        return subscriptions.stream()
                            .filter(subscription -> unsubscriptions.contains(subscription.getTopicName()))
                            .map(Subscription::toJson)
                            .map(JsonObject::toString)
                            .collect(Collectors.toList());
    }

    private static List<Subscription> subscriptionsFromJsonArray(JsonArray jsonArray) {
        return jsonArray.stream()
                        .map(subscription -> new Subscription((JsonObject) subscription))
                        .collect(Collectors.toList());
    }

    // TODO Create redis utils for creating topic "string"
    private static String subscriptionsTopicsKey(String clientId) {
        return String.format("subscriptions:%s:topics", clientId);
    }
}
