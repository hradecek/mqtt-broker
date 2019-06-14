package com.tracker.broker.mqtt;

import com.tracker.broker.redis.RedisVerticle;
import com.tracker.broker.redis.reactivex.RedisService;
import io.reactivex.Completable;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.serviceproxy.ServiceBinder;

public class ServicesVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        final RedisService redis = com.tracker.broker.redis.RedisService.createProxy(vertx.getDelegate(), RedisVerticle.CONFIG_REDIS_QUEUE);
        return registerService(SubscriptionService.class, SubscriptionService.create(), SubscriptionService.ADDRESS)
                .andThen(registerService(ClientService.class, ClientService.create(redis), ClientService.ADDRESS));
    }

    private <T> Completable registerService(final Class<T> clazz, final T service, final String address) {
        return Completable.fromAction(() ->
                new ServiceBinder(vertx.getDelegate()).setAddress(address).register(clazz, service));
    }
}
