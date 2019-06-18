package com.tracker.broker.mqtt.client;

import com.tracker.broker.mqtt.ServiceUtils;
import com.tracker.broker.mqtt.subscription.reactivex.SubscriptionService;
import io.reactivex.Completable;
import io.vertx.reactivex.core.AbstractVerticle;

public class ClientEndpointVerticle extends AbstractVerticle {

//    @Override
//    public Completable rxStart() {
//        RedisServiceHelper redis = com.tracker.broker.redis.RedisServiceHelper.createProxy(vertx, com.tracker.broker.redis.RedisServiceHelper.ADDRESS);
//        SubscriptionService subscriptions = com.tracker.broker.mqtt.subscription.SubscriptionService.createProxy(vertx.getDelegate(), com.tracker.broker.mqtt.subscription.SubscriptionService.ADDRESS);
//
//        return ServiceUtils.registerService(vertx.getDelegate(), ClientService.class, ClientService.create(redis, subscriptions), ClientService.ADDRESS);
//    }
}
