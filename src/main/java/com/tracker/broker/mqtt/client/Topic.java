package com.tracker.broker.mqtt.client;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.mqtt.messages.MqttPublishMessage;

public interface Topic {

    void process(final String clientId, final JsonObject message);
}
