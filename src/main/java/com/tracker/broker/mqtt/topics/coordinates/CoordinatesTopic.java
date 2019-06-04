package com.tracker.broker.mqtt.topics.coordinates;

import com.tracker.broker.mqtt.PublishMessage;
import com.tracker.broker.mqtt.topics.MqttTopic;
import com.tracker.broker.redis.RedisServiceHelper;

import com.tracker.broker.redis.RedisTopicsHelper;
import io.reactivex.Completable;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.redis.RedisClient;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.types.MessageSource;

/**
 * TODO
 */
// TODO: move to json, yaml or whatever, definitions, like routing
public class CoordinatesTopic {//implements MqttTopic<Coordinates> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoordinatesTopic.class);

    /**
     * Message source service name.
     */
    public static final String SERVICE_NAME = "service.mqtt.topics.coordinatesMessageSource";

    /**
     * Message source service address.
     */
    public static final String ADDRESS = "coordinates";

    public static final String TOPIC_COORDINATES = RedisTopicsHelper.toRedis("coordinates");

    private RedisClient redisClient;
    private ServiceDiscovery discovery;

    public CoordinatesTopic(ServiceDiscovery discovery) {
        this.discovery = discovery;
        RedisServiceHelper.getClient(discovery)
                          .subscribe(redisClient -> this.redisClient = redisClient)
                          .dispose();
    }

    // TODO: message might be a DataObject?
    public Completable consume() {
        return Completable.fromAction(() -> MessageSource.<PublishMessage>rxGetConsumer(discovery, new JsonObject().put("name", SERVICE_NAME))
                          .subscribe(consumer -> consumer.handler(message -> process(message.body()))));
    }


    public Completable publish() {
        return MqttTopic.publishMessageSource(discovery,
                                              CoordinatesTopic.SERVICE_NAME,
                                              CoordinatesTopic.ADDRESS,
                                              PublishMessage.class)
                        .doOnError(ex -> LOGGER.error("Cannot publish coordinates MQTT topic.", ex));
    }

    public void process(PublishMessage message) {
        System.out.println(message);
        String clientId = message.getClientId();
        JsonObject coordinates = message.getBody();

        if (!(coordinates.containsKey(Coordinates.JsonKeys.LAT.keyName) && coordinates.containsKey(Coordinates.JsonKeys.LNG.keyName))) {
            LOGGER.warn(String.format("Missing required properties for %s (%s)", SERVICE_NAME, message.toString()));
            return;
        }

        addCoordinates(clientId, coordinates);
    }

    private void addCoordinates(String clientId, JsonObject coordinates) {
        redisClient.rxLpush(RedisTopicsHelper.toRedis(TOPIC_COORDINATES, clientId), coordinates.toString())
                   .doOnSuccess(result -> {
                       if (!result.toString().equals("OK")) {
                           LOGGER.warn(String.format("Cannot add coordinates for %s: (%s)", clientId, coordinates));
                       }
                   }).subscribe();
    }
}
