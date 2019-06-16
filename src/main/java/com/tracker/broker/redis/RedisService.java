package com.tracker.broker.redis;

import com.tracker.broker.mqtt.subscription.Subscription;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.mqtt.messages.MqttPublishMessage;

import java.util.Set;

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
     * Static factory method for instance creation.
     *
     * @param config redis configuration
     * @param readyHandler async result
     * @return RedisService
     */
    @GenIgnore
    static RedisService create(RedisConfig config, Handler<AsyncResult<RedisService>> readyHandler) {
        return new RedisServiceImpl(config, readyHandler);
    }

    /**
     * Static factory method for event bus service proxy creation.
     *
     * @param vertx Vert.x instance
     * @param address service event-bus address
     * @return Rx-fied subscription service proxy
     */
    @GenIgnore
    static com.tracker.broker.redis.reactivex.RedisService createProxy(Vertx vertx, String address) {
        return new com.tracker.broker.redis.reactivex.RedisService(new RedisServiceVertxEBProxy(vertx, address));
    }

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
    RedisService addCoordinates(String clientId, double lat, double lng ,Handler<AsyncResult<Void>> resultHandler);

    /**
     * Add specified topic subscription with QoS level.
     *
     * @param clientId client's ID, that subscription will be added
     * @param topicsName topics' names of subscriptions, that will be added
     * @param resultHandler async result
     * @return RedisService
     */
    @Fluent
    RedisService addSubscriptions(String clientId, Set<Subscription> topicsName, Handler<AsyncResult<Void>> resultHandler);

    /**
     * Remove specified topic subscriptions for specified client.
     *
     * <p>Only subscriptions, that exists are removed meaning if client wants to unsubscribe from topic it was not
     * subscribe to, nothing will happen.
     *
     * @param clientId client's ID, that subscriptions of will be removed
     * @param topicsName topics' names of subscriptions, that will be removed
     * @param resultHandler async result
     * @return RedisService
     */
    @Fluent
    RedisService removeSubscriptions(String clientId, Set<String> topicsName, Handler<AsyncResult<Void>> resultHandler);
}
