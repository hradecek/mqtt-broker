package com.tracker.broker.mqtt;

import com.tracker.broker.redis.reactivex.RedisService;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.mqtt.messages.MqttPublishMessage;

/**
 * TODO
 */
public class CoordinatesTopic implements Topic {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoordinatesTopic.class);

    // TODO: move to json, yaml or whatever, definitions, like routing
    public static final String TOPIC_COORDINATES = "coordinates";

    private static final String MESSAGE_COORDINATES_LAT = "lat";
    private static final String MESSAGE_COORDINATES_LNG = "lng";

    private final RedisService redis;

    public CoordinatesTopic(final RedisService redis) {
        this.redis = redis;
    }

    @Override
    public void process(final String clientId, final MqttPublishMessage message) {
        final JsonObject jsonMessage = message.payload().toJsonObject();
        if (!jsonMessage.containsKey(MESSAGE_COORDINATES_LAT) && !(jsonMessage.containsKey(MESSAGE_COORDINATES_LNG))) {
            LOGGER.warn(String.format("Missing required properties for %s (%s)", TOPIC_COORDINATES, message.toString()));
            return;
        }
        redis.rxAddCoordinates(clientId, jsonMessage.getDouble(MESSAGE_COORDINATES_LAT), jsonMessage.getDouble(MESSAGE_COORDINATES_LNG)).subscribe();
    }
}
