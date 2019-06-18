package com.tracker.broker.mqtt.client;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * TODO
 */
public class CoordinatesTopic {

//    private static final Logger LOGGER = LoggerFactory.getLogger(CoordinatesTopic.class);
//
//    // TODO: move to json, yaml or whatever, definitions, like routing
//    public static final String TOPIC_COORDINATES = "coordinates";
//
//    private static final String MESSAGE_COORDINATES_LAT = "lat";
//    private static final String MESSAGE_COORDINATES_LNG = "lng";
//
//    private final RedisServiceHelper redis;
//
//    public CoordinatesTopic(final RedisServiceHelper redis) {
//        this.redis = redis;
//    }
//
//    @Override
//    public void process(final String clientId, final JsonObject message) {
//        if (!message.containsKey(MESSAGE_COORDINATES_LAT) && !(message.containsKey(MESSAGE_COORDINATES_LNG))) {
//            LOGGER.warn(String.format("Missing required properties for %s (%s)", TOPIC_COORDINATES, message.toString()));
//            return;
//        }
//        redis.rxAddCoordinates(clientId, message.getDouble(MESSAGE_COORDINATES_LAT), message.getDouble(MESSAGE_COORDINATES_LNG)).subscribe();
//    }
}
