package com.tracker.broker.redis;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.buffer.Buffer;
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

    /**
     * Default service address.
     */
    public static final String CONFIG_REDIS_QUEUE ="redis.queue";

    @Override
    public Completable rxStart() {
        return readConfig().flatMapCompletable(this::registerService)
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
                    new ServiceBinder(vertx.getDelegate()).setAddress(CONFIG_REDIS_QUEUE)
                                                          .register(RedisService.class, result.result());
                } else {
                    LOGGER.error("Cannot create redis service.");
                }
            })
        );
    }

    /**
     * Read json configuration.
     *
     * @return redis configuration
     */
    private Single<RedisConfig> readConfig() {
        return vertx.fileSystem()
                    .rxReadFile(CONFIG_REDIS_PATH)
                    .map(RedisVerticle::bufferToRedisConfig)
                    .doOnError(ex -> LOGGER.error("Cannot read redis configuration " + CONFIG_REDIS_PATH, ex));
    }

    private static RedisConfig bufferToRedisConfig(final Buffer buffer) {
        return buffer.toJsonObject().mapTo(RedisConfig.class);
    }
}
