package com.tracker.broker.mqtt.subscription;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;

import java.util.Set;

/**
 * Subscription service handles all subscription based MQTT request.
 */
@ProxyGen
@VertxGen
public interface SubscriptionService {

    /**
     * Service's default event bus address.
     */
    String ADDRESS ="service.mqtt.subscription";

    /**
     * Static factory method.
     *
     * @param discovery service discovery instance
     * @param readyHandler async result
     * @return SubscriptionService
     */
    @GenIgnore
    static SubscriptionService create(ServiceDiscovery discovery, Handler<AsyncResult<SubscriptionService>> readyHandler) {
        return new SubscriptionServiceImpl(discovery, readyHandler);
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
}
