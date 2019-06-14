package com.tracker.broker.mqttold;

import io.vertx.reactivex.core.AbstractVerticle;

public class ServicesVerticle extends AbstractVerticle {

//    @Override
//    public Completable rxStart() {
//        final RedisService redis = com.tracker.broker.redis.RedisService.createProxy(vertx.getDelegate(), RedisVerticle.CONFIG_REDIS_QUEUE);
//        return registerService(SubscriptionService.class, SubscriptionService.create(), SubscriptionService.ADDRESS)
//                .andThen(registerService(ClientService.class, ClientService.create(redis), ClientService.ADDRESS));
//    }
//
//    private <T> Completable registerService(final Class<T> clazz, final T service, final String address) {
//        return Completable.fromAction(() ->
//                new ServiceBinder(vertx.getDelegate()).setAddress(address).register(clazz, service));
//    }
}
