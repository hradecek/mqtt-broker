package com.tracker.broker.mqtt;

import io.vertx.reactivex.mqtt.messages.MqttPublishMessage;

public interface Topic {

    void process(final String clientId, final MqttPublishMessage message);
}
