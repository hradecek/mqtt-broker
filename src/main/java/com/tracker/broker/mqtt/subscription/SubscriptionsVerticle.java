package com.tracker.broker.mqtt.subscription;

import io.reactivex.Completable;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.types.MessageSource;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * TODO
 */
public class SubscriptionsVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionsVerticle.class);

    /**
     * TODO
     */
    private static final String SERVICE_NAME_SUBSCRIPTIONS = "subscriptions";

    /**
     * TODO
     */
    public static final String SERVICE_ADDRESS_SUBSCRIPTIONS_CHANGES = "subscriptions-changes";

    private ServiceDiscovery discovery;

    @Override
    public Completable rxStart() {
        discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));
        return publishMessageSource().andThen(registerSubscriptionService());
    }

    private Completable publishMessageSource() {
        Record record = MessageSource.createRecord(SERVICE_NAME_SUBSCRIPTIONS, SERVICE_ADDRESS_SUBSCRIPTIONS_CHANGES, JsonObject.class);
        return discovery.rxPublish(record)
                        .doOnError(ex -> LOGGER.error(String.format("Cannot publish subscription message source: %s", ex)))
                        .ignoreElement();

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
