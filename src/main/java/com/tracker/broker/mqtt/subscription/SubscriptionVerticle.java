package com.tracker.broker.mqtt.subscription;

import io.reactivex.Completable;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * Subscription verticles handles all MQTT subscription related operation such as subscribe, unsubscribe requests,
 * creating messages for subscribed clients.
 */
public class SubscriptionVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionVerticle.class);

    private ServiceDiscovery discovery;

    @Override
    public Completable rxStart() {
        discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));
        return SubscriptionMessageSourceHelper.publishMessageSource(discovery).andThen(registerSubscriptionService());
    }

    private Completable registerSubscriptionService() {
        return Completable.fromAction(() ->
                SubscriptionService.create(discovery, result -> {
                    if (result.succeeded()) {
                        new ServiceBinder(vertx.getDelegate()).setAddress(SubscriptionService.ADDRESS)
                                                              .register(SubscriptionService.class, result.result());
                    } else {
                        LOGGER.error(String.format("Cannot create SubscriptionService instance: %s", result.cause()));
                    }
                })
        );
    }
}
