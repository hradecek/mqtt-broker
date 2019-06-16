package com.tracker.broker.mqttold;

import com.tracker.broker.redis.reactivex.RedisService;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.mqtt.messages.MqttPublishMessage;

import java.util.HashMap;
import java.util.Map;

//import static com.tracker.broker.mqttold.CoordinatesTopic.TOPIC_COORDINATES;

public class PublishHandler { //implements Handler<MqttPublishMessage> {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(PublishHandler.class);
//
//    private final String clientId;
//    private final RedisService redis;
//    private final Map<String, Topic> topics = new HashMap<>();
//
//    public PublishHandler(RedisService redis, String clientId) {
//        this.clientId = clientId;
//        this.redis = redis;
//        topics.put(TOPIC_COORDINATES, new CoordinatesTopic(redis));
//    }
//
//    @Override
//    public void subscribe(final MqttPublishMessage message) {
//        redis.rxReceivedMessage(message.getDelegate().payload().getBytes().length).subscribe();
//        if (topics.containsKey(message.topicName())) {
//            topics.get(message.topicName()).process(clientId, message);
//        } else {
//            LOGGER.warn(String.format("Unknown topic %s", message.topicName()));
//        }
//    }
}
