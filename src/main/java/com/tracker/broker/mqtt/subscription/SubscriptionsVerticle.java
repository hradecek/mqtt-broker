package com.tracker.broker.mqtt.subscription;

import com.tracker.broker.mqtt.ServiceUtils;
import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.types.MessageSource;

public class SubscriptionsVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionsVerticle.class);

    @Override
    public Completable rxStart() {
        // Publish subscription changes
        Record record = MessageSource.createRecord(
                "subscriptions", // The service name
                "subscriptions-changes", // The event bus address
                JsonObject.class // The message payload type
        );
        ServiceDiscovery discovery = ServiceDiscovery.create(vertx.getDelegate(), new ServiceDiscoveryOptions().setBackendConfiguration(config()));
        discovery.publish(record, ar -> {});

        return ServiceUtils.registerService(vertx.getDelegate(), SubscriptionService.class, SubscriptionService.create(vertx.getDelegate()), SubscriptionService.ADDRESS);
    }
}
