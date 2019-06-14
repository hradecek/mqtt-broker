package com.tracker.broker.mqtt;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * TODO
 */
@ProxyGen
@VertxGen
public interface SubscriptionService {

    String ADDRESS = "subscription.queue";

    @Fluent
    SubscriptionService addSubscriptions(final JsonObject subscriptions, Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    SubscriptionService removeSubscriptions(final JsonObject unsubscriptions, Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    SubscriptionService publishUpdates(final JsonObject updates, Handler<AsyncResult<Void>> resultHandler);

    @GenIgnore
    static SubscriptionService create() {
        return new SubscriptionServiceImpl();
    }

    @GenIgnore
    static com.tracker.broker.mqtt.reactivex.SubscriptionService createProxy(Vertx vertx, String address) {
        return new com.tracker.broker.mqtt.reactivex.SubscriptionService(new SubscriptionServiceVertxEBProxy(vertx, address));
    }

//    @Fluent
//    SubscriptionService addSubscription(final String clientId,
//                                        final MqttSubscribeMessage message,
//                                        Handler<AsyncResult<Void>> resultHandler);
//
//    @Fluent
//    SubscriptionService removeSubscription(final String clientId,
//                                           final MqttUnsubscribeMessage message,
//                                           Handler<AsyncResult<Void>> resultHandler);
//
//    @GenIgnore
//    static SubscriptionService create(Handler<AsyncResult<SubscriptionService>> readyHandler) {
//        return new SubscriptionServiceImpl(readyHandler);
//    }

//    @GenIgnore
//    static com.tracker.broker.redis.reactivex. createProxy(Vertx vertx, String address) {
//        return new com.tracker.broker.redis.reactivex.RedisService(new RedisServiceVertxEBProxy(vertx, address));
//    }
}
