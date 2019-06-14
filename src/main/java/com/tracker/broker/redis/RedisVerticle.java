package com.tracker.broker.redis;

import com.tracker.broker.config.JsonConfigReader;

import io.reactivex.Completable;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * Redis verticle reads redis configuration, based on which it creates and register {@link RedisService redis service}.
 */
public class RedisVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    /**
     * Default configuration file path.
     */
    private static final String CONFIG_REDIS_PATH = "redis/config.json";

    @Override
    public Completable rxStart() {
        return new JsonConfigReader<>(vertx, RedisConfig.class)
                .read(CONFIG_REDIS_PATH)
                .doOnError(ex -> LOGGER.error("Cannot read configuration " + CONFIG_REDIS_PATH, ex))
                .flatMapCompletable(this::registerService)
                .doOnComplete(() -> LOGGER.info("Started " + RedisVerticle.class.getSimpleName()));
    }

    /**
     * Creates redis service and register it.
     *
     * @param config redis configuration
     * @return completable
     */
    private Completable registerService(final RedisConfig config) {
        return Completable.fromAction(() ->
            RedisService.create(config, result -> {
                if (result.succeeded()) {
                    new ServiceBinder(vertx.getDelegate()).setAddress(RedisService.ADDRESS)
                                                          .register(RedisService.class, result.result());
                } else {
                    LOGGER.error("Cannot create redis service.");
                }
            })
        );
    }
}
