package com.tracker.broker;

import com.tracker.broker.mqtt.MqttServerVerticle;
import com.tracker.broker.mqtt.subscription.SubscriptionVerticle;
import com.tracker.broker.redis.RedisServiceHelper;
import com.tracker.broker.mqtt.topics.TopicsVerticle;

import io.reactivex.Completable;
import io.vertx.core.Verticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Main application verticle.
 *
 * <p>This verticle serves as application entry point and it deploys all other verticles.
 */
public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

    /**
     * All verticles registered by main verticle (in order).
     */
    // TODO: Can be moved to configuration file
    private static final Collection<Verticle> verticles = new ArrayList<Verticle>() {{
        add(new TopicsVerticle());
        add(new SubscriptionVerticle());
        add(new MqttServerVerticle());
    }};

    @Override
    public Completable rxStart() {
        ServiceDiscovery discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));
        return RedisServiceHelper.registerRedis(vertx, discovery).andThen(deployVerticles());
    }

    private Completable deployVerticles() {
        return verticles.stream().map(this::deployVerticle).reduce(Completable.complete(), Completable::andThen);
    }

    private Completable deployVerticle(Verticle verticle) {
        return vertx.rxDeployVerticle(verticle)
                     .doOnError(ex -> LOGGER.error(String.format("Cannot deploy verticle: %s", verticle.getClass().getName())))
                     .doOnSuccess(id -> LOGGER.info(String.format("Deployed verticle: %s - %s", verticle.getClass().getName(), id)))
                     .ignoreElement();
    }
}
