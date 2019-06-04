package com.tracker.broker.redis;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.SocketAddress;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;

import java.util.Arrays;
import java.util.function.UnaryOperator;

/**
 * TODO
 */
public class RedisServiceImpl implements RedisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisServiceImpl.class);

    /**
     * TODO
     */
    private static final String BROKER_CLIENTS_CONNECTED = "broker:clients:connected";
    private static final String BROKER_CLIENTS_DISCONNECTED = "broker:clients:disconnected";
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
    RedisServiceImpl(RedisConfig config,
                     Handler<AsyncResult<RedisService>> readyHandler) {
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
                if (result.result().toInteger() == 0) {
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
    public RedisService addSubscription(final String clientId, final String topicName, Handler<AsyncResult<Void>> resultHandler) {
        client.sadd(Arrays.asList(String.format("subscriptions:%s:topics", clientId), topicName), result -> {
            if (result.succeeded()) {
                resultHandler.handle(Future.succeededFuture());
            } else {
                resultHandler.handle(Future.failedFuture(result.cause()));
            }
        });

        return this;
    }

    @Override
    public RedisService removeSubscription(final String clientId, final String topicName, Handler<AsyncResult<Void>> resultHandler) {
        client.srem(Arrays.asList(String.format("subscriptions:%s:topics", clientId), topicName), result -> {
            if (result.succeeded()) {
                resultHandler.handle(Future.succeededFuture());
            } else {
                resultHandler.handle(Future.failedFuture(result.cause()));
            }
        });

        return this;
    }
}
