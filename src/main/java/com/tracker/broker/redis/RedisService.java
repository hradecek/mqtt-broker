package com.tracker.broker.redis;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.mqtt.messages.MqttPublishMessage;

/**
 * TODO
 */
@ProxyGen
@VertxGen
public interface RedisService {

    /**
     * Default service address.
     */
    String ADDRESS ="redis.queue";

    /**
     * TODO
     *
     * @param clientId
     * @param resultHandler
     * @return
     */
    @Fluent
    RedisService addClient(final String clientId, Handler<AsyncResult<Void>> resultHandler);

    /**
     *
     * @param clientId
     * @param resultHandler
     * @return
     */
    @Fluent
    RedisService disconnectClient(String clientId, Handler<AsyncResult<Void>> resultHandler);

    /**
     * TODO
     *
     * @param clientId
     * @param resultHandler
     * @return
     */
    @Fluent
    RedisService remClient(final String clientId, Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    RedisService receivedMessage(int bytes, Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    RedisService getClientsConnected(Handler<AsyncResult<Integer>> resultHandler);

    /**
     * TODO
     *
     * @param clientId
     * @param lat
     * @param lng
     * @param resultHandler
     * @return
     */
    @Fluent
    RedisService addCoordinates(final String clientId, double lat, double lng ,Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    RedisService addSubscription(String clientId, String topicName, Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    RedisService removeSubscription(String clientId, String topicName, Handler<AsyncResult<Void>> resultHandler);

    @GenIgnore
    static RedisService create(RedisConfig config, Handler<AsyncResult<RedisService>> readyHandler) {
        return new RedisServiceImpl(config, readyHandler);
    }

    @GenIgnore
    static com.tracker.broker.redis.reactivex.RedisService createProxy(Vertx vertx, String address) {
        return new com.tracker.broker.redis.reactivex.RedisService(new RedisServiceVertxEBProxy(vertx, address));
    }
}
