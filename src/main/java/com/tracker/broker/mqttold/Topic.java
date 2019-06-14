package com.tracker.broker.mqttold;

import io.vertx.reactivex.mqtt.messages.MqttPublishMessage;

public interface Topic {

    void process(final String clientId, final MqttPublishMessage message);
}
