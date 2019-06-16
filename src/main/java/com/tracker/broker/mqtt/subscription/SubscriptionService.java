package com.tracker.broker.mqtt.subscription;

import com.tracker.broker.redis.reactivex.RedisService;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.Set;

/**
 * TODO
 */
@ProxyGen
@VertxGen
public interface SubscriptionService {

    /**
     * Service's default event bus address.
     */
    String ADDRESS = "subscription.queue";

    /**
     * Static factory method.
     *
     * @param vertx
     * @param redis
     * @return
     */
    @GenIgnore
    static SubscriptionService create(Vertx vertx, RedisService redis) {
        return new SubscriptionServiceImpl(vertx, redis);
    }

    /**
     * Static factory method for event bus service proxy creation.
     *
     * @param vertx Vert.x instance
     * @param address service event-bus address
     * @return Rx-fied subscription service proxy
     */
    @GenIgnore
    static com.tracker.broker.mqtt.subscription.reactivex.SubscriptionService createProxy(Vertx vertx, String address) {
        return new com.tracker.broker.mqtt.subscription.reactivex.SubscriptionService(new SubscriptionServiceVertxEBProxy(vertx, address));
    }

    /**
     * Add specified topic subscription with QoS level.
     *
     * @param clientId client's ID
     * @param subscription set of subscriptions
     * @param resultHandler async result
     * @return SubscriptionService
     */
    @Fluent
    SubscriptionService addSubscriptions(String clientId, Set<Subscription> subscription, Handler<AsyncResult<Void>> resultHandler);

    /**
     * Remove specified topic subscriptions for specified client.
     *
     * @param clientId client's ID
     * @param topicNames topics' names to be unsubscribe from
     * @param resultHandler async result
     * @return SubscriptionService
     */
    @Fluent
    SubscriptionService removeSubscriptions(String clientId, Set<String> topicNames, Handler<AsyncResult<Void>> resultHandler);

    /**
     * TODO
     *
     * @param updates
     * @param resultHandler
     * @return
     */
    @Fluent
    SubscriptionService publishUpdates(final JsonObject updates, Handler<AsyncResult<Void>> resultHandler);
}
