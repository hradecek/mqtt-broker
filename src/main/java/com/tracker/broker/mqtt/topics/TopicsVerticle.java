package com.tracker.broker.mqtt.topics;

import com.tracker.broker.mqtt.PublishMessage;
import com.tracker.broker.mqtt.topics.coordinates.CoordinatesTopic;
import com.tracker.broker.mqtt.topics.sys.SysTopicService;

import io.reactivex.Completable;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * Publishes and register all MQTT topics related services.
 */
public class TopicsVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicsVerticle.class);

    private ServiceDiscovery discovery;

    @Override
    public Completable rxStart() {
        discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));

        CoordinatesTopic coordinatesTopic = new CoordinatesTopic(discovery);
        vertx.eventBus().<JsonObject>consumer("coordinates").handler(message -> System.out.println(message.body()));

        return registerSysTopicService();
//        return coordinatesTopic.publish().andThen(coordinatesTopic.consume()).andThen(registerSysTopicService());
    }

    private Completable registerSysTopicService() {
        return Completable.fromAction(() ->
            SysTopicService.create(vertx.eventBus(), discovery, result -> {
                if (result.succeeded()) {
                    new ServiceBinder(vertx.getDelegate()).setAddress(SysTopicService.ADDRESS)
                                                          .register(SysTopicService.class, result.result());
                } else {
                    LOGGER.error(String.format("Cannot create SysTopicService instance: %s", result.cause()));
                }
            })
        );
    }

}
