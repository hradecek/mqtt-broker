package com.tracker.broker.mqtt.topics;

import io.reactivex.Completable;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.types.MessageSource;
import io.vertx.servicediscovery.Record;

/**
 * Represents MQTT topic.
 *
 * @param <T> type of the message topic works with
 */
@FunctionalInterface
public interface MqttTopic<T> {

    Logger LOGGER = LoggerFactory.getLogger(MqttTopic.class);

    /**
     *
     * @param clientId
     * @param message
     */
    void process(String clientId, T message);

    /**
     * Publish topic {@link MessageSource message source} service via {@link ServiceDiscovery service discovery}.
     *
     * @param discovery service discovery instance
     * @param serviceName message source service name
     * @param address message source service address
     * @param clazz message source message type
     * @return completable
     */
    // TODO move to class?
    static Completable publishMessageSource(ServiceDiscovery discovery, String serviceName, String address, Class<?> clazz) {
        Record record = MessageSource.createRecord(serviceName, address, clazz.getName());
        return discovery.rxPublish(record)
                        .doOnError(ex -> LOGGER.error("Cannot publish coordinates message source.", ex))
                        .ignoreElement();
    }
}
