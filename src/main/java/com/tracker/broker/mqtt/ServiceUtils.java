package com.tracker.broker.mqtt;

import io.reactivex.Completable;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ServiceBinder;

public class ServiceUtils {

    public static <T> Completable registerService(Vertx vertx, Class<T> clazz, T service, String address) {
        return Completable.fromAction(() -> new ServiceBinder(vertx).setAddress(address).register(clazz, service));
    }
}
