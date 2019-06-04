package com.tracker.broker.mqtt.subscription;

import io.reactivex.Completable;
import io.reactivex.Single;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.types.MessageSource;
import io.vertx.servicediscovery.Record;

/**
 * Static helper class providing helper methods for managing subscription {@link MessageSource message source} and
 * {@link MessageConsumer message consumer}.
 *
 * <p>Subscription message source is used to publish messages for subscribed MQTT clients.
 *
 * <p>Note: this class cannot be instantiated.
 */
public class SubscriptionMessageSourceHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionMessageSourceHelper.class);

    /**
     * Message source service name.
     */
    static final String SERVICE_NAME = "service.mqtt.subscription.subscriptionMessageSource";

    /**
     * Message source service address.
     */
    public static final String ADDRESS = "service.mqtt.subscription.subscriptionMessage";

    private static final String JSON_KEY_NAME = "name";

    private SubscriptionMessageSourceHelper() {
        throw new UnsupportedOperationException(String.format("Cannot instantiate '%s' class.", SubscriptionMessageSourceHelper.class.getName()));
    }

    /**
     * Publish subscription {@link MessageSource message source} service via {@link ServiceDiscovery service discovery}.
     *
     * @param discovery service discovery instance
     * @return completable
     */
    public static Completable publishMessageSource(ServiceDiscovery discovery) {
        Record record = MessageSource.createRecord(SERVICE_NAME, ADDRESS, JsonObject.class.getName());
        return discovery.rxPublish(record)
                        .doOnError(ex -> LOGGER.error("Cannot publish subscription message source.", ex))
                        .ignoreElement();
    }

    /**
     * Publish message in JSON format to subscription message source address.
     *
     * @param eventBus Vert.x's event bus instance
     * @param message JSON message to be published
     * @return completable
     */
    public static Completable publishMessage(EventBus eventBus, JsonObject message) {
        return Completable.fromAction(() -> eventBus.publish(ADDRESS, message));
    }

    /**
     * Get subscription {@link MessageConsumer message consumer} via {@link ServiceDiscovery service discovery}.
     *
     * @param discovery service discovery instance
     * @return JSON object message consumer
     */
    public static Single<MessageConsumer<JsonObject>> subscriptionMessageConsumer(ServiceDiscovery discovery) {
        return MessageSource.rxGetConsumer(discovery, new JsonObject().put(JSON_KEY_NAME, SERVICE_NAME));
    }
}
