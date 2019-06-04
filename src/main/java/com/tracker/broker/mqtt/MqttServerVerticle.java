package com.tracker.broker.mqtt;

import com.tracker.broker.redis.reactivex.RedisService;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mqtt.MqttServerOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.mqtt.MqttEndpoint;
import io.vertx.reactivex.mqtt.MqttServer;

import static com.tracker.broker.redis.RedisVerticle.CONFIG_REDIS_QUEUE;

/**
 * MQTT server verticle is responsible for starting MQTT Broker server instance and handles client <=> broker
 * communication.
 */
public class MqttServerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttServerVerticle.class);

    private static final String CONFIG_BROKER_PATH = "config.json";

    @Override
    public Completable rxStart() {
        return readConfig().flatMap(this::createMqttServer)
                .ignoreElement()
                .doOnComplete(() -> LOGGER.info("Started " + MqttServerOptions.class.getSimpleName()));
    }

    private Single<MqttServer> createMqttServer(final BrokerConfig config) {
        final String redisSysTopicQueue = config().getString(CONFIG_REDIS_QUEUE, CONFIG_REDIS_QUEUE);
        final RedisService redisService = com.tracker.broker.redis.RedisService.createProxy(vertx.getDelegate(), redisSysTopicQueue);

        final MqttServer mqttServer = MqttServer.create(vertx, new MqttServerOptions().setHost(config.getHost()).setPort(config.getPort()));
        mqttServer.endpointHandler(new ClientEndpoint(redisService));

        return mqttServer.rxListen()
                .doOnSuccess(ms -> LOGGER.info(String.format("MQTT Server is listening on %d", ms.actualPort())))
                .doOnError(ex -> LOGGER.error("Cannot start MQTT server", ex));
    }

    public static class ClientEndpoint implements Handler<MqttEndpoint> {
        private static final Logger LOGGER = LoggerFactory.getLogger(ClientEndpoint.class);

        private final RedisService redis;

        public ClientEndpoint(RedisService redis) {
            this.redis = redis;
        }

        @Override
        public void handle(final MqttEndpoint endpoint) {
            // Used custom ID instead of endpoint ID - might be duplicated
            LOGGER.info("Connected: " + endpoint.clientIdentifier());
            redis.rxAddClient(endpoint.clientIdentifier()).subscribe();
            final Subscription subscription = new Subscription(redis, endpoint);

            endpoint.publishHandler(new PublishHandler(redis, endpoint.clientIdentifier()))
                    .subscribeHandler(subscription::subscribeHandler)
                    .unsubscribeHandler(subscription::unsubscribeHandler)
                    // .exceptionHandler() TODO: Dump all you can
                    .closeHandler(__ -> close(endpoint))
                    .disconnectHandler(__ -> disconnect(endpoint))
                    .accept();
        }

        private void close(final MqttEndpoint endpoint) {
            LOGGER.info("Closed: " + endpoint.clientIdentifier());
            redis.rxRemClient(endpoint.clientIdentifier())
                 .subscribe();
        }

        private void disconnect(final MqttEndpoint endpoint) {
            LOGGER.info("Disconnected: " + endpoint.clientIdentifier());
            redis.rxRemClient(endpoint.clientIdentifier()).subscribe();
        }
    }

    private Single<BrokerConfig> readConfig() {
        return vertx.fileSystem()
                .rxReadFile(CONFIG_BROKER_PATH)
                .map(MqttServerVerticle::bufferToBrokerConfig)
                .doOnError(ex -> LOGGER.error("Cannot read MQTT broker configuration " + CONFIG_BROKER_PATH, ex));
    }

    private static BrokerConfig bufferToBrokerConfig(final Buffer buffer) {
        return buffer.toJsonObject().mapTo(BrokerConfig.class);
    }
}
