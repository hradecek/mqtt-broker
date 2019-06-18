package com.tracker.broker.mqtt.topics;

import com.tracker.broker.mqtt.topics.sys.SysTopicService;
import io.reactivex.Completable;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.serviceproxy.ServiceBinder;

/**
 */
public class TopicsVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicsVerticle.class);

    private ServiceDiscovery discovery;

    @Override
    public Completable rxStart() {
        discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));
        return registerSysTopicService();
    }

    /**
     * TODO
     */
    private Completable registerSysTopicService() {
        return Completable.fromAction(() ->
            SysTopicService.create(discovery, result -> {
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
