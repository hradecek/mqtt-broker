package com.tracker.broker.mqtt;

import com.tracker.broker.redis.reactivex.RedisService;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.mqtt.MqttEndpoint;
import io.vertx.reactivex.mqtt.MqttTopicSubscription;
import io.vertx.reactivex.mqtt.messages.MqttSubscribeMessage;
import io.vertx.reactivex.mqtt.messages.MqttUnsubscribeMessage;

import java.util.ArrayList;
import java.util.List;

public class Subscription {

    private static final Logger LOGGER = LoggerFactory.getLogger(Subscription.class);

    private final RedisService redis;
    private final MqttEndpoint endpoint;

    public Subscription(RedisService redis, MqttEndpoint clientId) {
        this.redis = redis;
        this.endpoint = clientId;
    }

    public void subscribeHandler(MqttSubscribeMessage message) {
        List<MqttQoS> grantedQos = new ArrayList<>();
        for (MqttTopicSubscription subscription : message.topicSubscriptions()) {
            LOGGER.info(subscription.topicName());
            grantedQos.add(subscription.qualityOfService());
            redis.rxAddSubscription(endpoint.clientIdentifier(), subscription.topicName()).subscribe();
        }
        endpoint.subscribeAcknowledge(message.messageId(), grantedQos);
    }

    public void unsubscribeHandler(MqttUnsubscribeMessage message) {
        for (String topicName : message.topics()) {
            LOGGER.info(topicName);
            redis.rxRemoveSubscription(endpoint.clientIdentifier(), topicName).subscribe();
        }
        endpoint.unsubscribeAcknowledge(message.messageId());
    }
}
