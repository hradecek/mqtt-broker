package com.tracker.broker.redis;

import com.tracker.broker.config.JsonConfigReader;

import io.reactivex.Completable;
import io.reactivex.Single;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.redis.RedisClient;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.types.RedisDataSource;

/**
 * Static helper class for manipulating with {@link RedisDataSource Redis Data Source}.
 *
 * <p>Note: this class cannot be instantiated.
 */
public class RedisServiceHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisServiceHelper.class);

    private RedisServiceHelper() {
        throw new UnsupportedOperationException(String.format("Cannot instantiate '%s' class", RedisServiceHelper.class.getName()));
    }

    /**
     * Redis service name.
     */
    private static final String SERVICE_NAME_REDIS = "service.redis";

    /**
     * Default redis configuration file path.
     */
    private static final String CONFIG_REDIS_PATH = "redis/config.json";

    private static final String JSON_KEY_URL = "url";
    private static final String JSON_KEY_NAME = "name";

    /**
     * Register {@link RedisDataSource redis data source} via {@link ServiceDiscovery service discovery} based on
     * provided JSON configuration.
     *
     * @param vertx Vert.x instance
     * @param discovery service discovery instance
     * @return completable
     */
    public static Completable registerRedis(Vertx vertx, ServiceDiscovery discovery) {
        return readConfiguration(vertx).flatMapCompletable(config -> registerRedisDataSource(discovery, config));
    }

    private static Single<RedisConfig> readConfiguration(Vertx vertx) {
        return new JsonConfigReader<>(vertx, RedisConfig.class)
                .read(CONFIG_REDIS_PATH)
                .doOnSuccess(config -> LOGGER.info(String.format("Redis configuration: %s", config)))
                .doOnError(ex -> LOGGER.error("Cannot read configuration " + CONFIG_REDIS_PATH, ex));
    }

    private static Completable registerRedisDataSource(ServiceDiscovery discovery, RedisConfig config) {
        return discovery.rxPublish(RedisDataSource.createRecord(SERVICE_NAME_REDIS, location(config), new JsonObject()))
                        .ignoreElement()
                        .doOnComplete(() -> LOGGER.info("Registered redis data source"))
                        .doOnError(ex -> LOGGER.error(String.format("Cannot registerRedis redis data source: %s", ex)));
    }

    private static JsonObject location(RedisConfig config) {
        return new JsonObject().put(JSON_KEY_URL, hostPort(config.getHost(), config.getPort()));
    }

    // TODO: Move to some utility class
    private static String hostPort(String host, int port) {
        return String.format("%s:%d", host, port);
    }

    /**
     * Retrieve {@link RedisDataSource redis data source} client via {@link ServiceDiscovery service discovery}.
     *
     * @param discovery service discovery instance
     * @return redis client
     */
    public static Single<RedisClient> getClient(ServiceDiscovery discovery) {
        return RedisDataSource.rxGetRedisClient(discovery, new JsonObject().put(JSON_KEY_NAME, SERVICE_NAME_REDIS))
                              .doOnError(ex -> LOGGER.error(String.format("Cannot get redis data source client: %s", ex)));
    }
}
