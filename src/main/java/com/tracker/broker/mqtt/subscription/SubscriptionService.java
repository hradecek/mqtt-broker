package com.tracker.broker.mqtt.subscription;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * TODO
 */
@ProxyGen
@VertxGen
public interface SubscriptionService {

    String ADDRESS = "subscription.queue";

    @GenIgnore
    static SubscriptionService create(Vertx vertx) {
        return new SubscriptionServiceImpl(vertx);
    }

    @GenIgnore
    static com.tracker.broker.mqtt.subscription.reactivex.SubscriptionService createProxy(Vertx vertx, String address) {
        return new com.tracker.broker.mqtt.subscription.reactivex.SubscriptionService(new SubscriptionServiceVertxEBProxy(vertx, address));
    }

    @Fluent
    SubscriptionService addSubscriptions(String clientId, JsonArray topics, Handler<AsyncResult<Void>> resultHandler);

    /**
     * TODO
     *
     * @param unsubscriptions
     * @param resultHandler
     * @return
     */
    @Fluent
    SubscriptionService removeSubscriptions(final JsonObject unsubscriptions, Handler<AsyncResult<Void>> resultHandler);

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
