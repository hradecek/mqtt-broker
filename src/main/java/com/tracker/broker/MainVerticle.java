package com.tracker.broker;

import com.tracker.broker.mqtt.MqttServerVerticle;
import com.tracker.broker.redis.RedisVerticle;
import io.reactivex.Completable;
import io.vertx.core.Verticle;
import io.vertx.reactivex.core.AbstractVerticle;

/**
 * Main application verticle.
 *
 * <p>This verticle serves as application entry point and it deploys all other verticles.
 */
public class MainVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        return deployVerticle(new RedisVerticle()).andThen(deployVerticle(new MqttServerVerticle()));
    }

    private Completable deployVerticle(Verticle verticle) {
        return vertx.rxDeployVerticle(verticle).ignoreElement();
    }
}
