package com.tracker.broker.mqtt.subscription;

import com.tracker.broker.redis.reactivex.RedisService;
import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.types.MessageSource;

import static com.tracker.broker.mqtt.ServiceUtils.registerService;

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
    static final String SERVICE_ADDRESS_SUBSCRIPTIONS_CHANGES = "subscriptions-changes";

    @Override
    public Completable rxStart() {
        final RedisService redis = com.tracker.broker.redis.RedisService.createProxy(vertx.getDelegate(), com.tracker.broker.redis.RedisService.ADDRESS);

        return publishMessageSource().andThen(registerSubscription(redis));
    }

    private Completable publishMessageSource() {
        Record record = MessageSource.createRecord(SERVICE_NAME_SUBSCRIPTIONS, SERVICE_ADDRESS_SUBSCRIPTIONS_CHANGES, JsonObject.class);

        return ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()))
                               .rxPublish(record)
                               .doOnError(ex -> LOGGER.error(String.format("Cannot publish subscription message source: %s", ex.getCause())))
                               .ignoreElement();

    }

    private Completable registerSubscription(RedisService redis) {
        return registerService(vertx.getDelegate(),
                               SubscriptionService.class,
                               SubscriptionService.create(vertx.getDelegate(), redis),
                               SubscriptionService.ADDRESS)
                .doOnError(ex -> LOGGER.error(String.format("Cannot register subscription service: %s", ex.getCause())));
    }

}
