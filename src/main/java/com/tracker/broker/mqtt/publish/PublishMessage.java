package com.tracker.broker.mqtt.publish;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Data object representing publish message.
 */
@DataObject(generateConverter = true, publicConverter = false)
public class PublishMessage {

    /**
     * Represents JSON object structure - keys definition.
     */
    public enum JsonKeys {
        QOS("qos"),
        PAYLOAD("payload"),
        TOPIC_NAME("topicName");

        final String keyName;

        JsonKeys(String keyName) {
            this.keyName = keyName;
        }

        public String keyName() {
            return keyName;
        }
    }

    /**
     * Default QoS level used when QoS is not specified.
     */
    public final int DEFAULT_QOS = 0;

    /**
     * Default topic name, when no topic is specified (empty string - safer than null).
     */
    public final String DEFAULT_TOPIC_NAME = "";

    private final int qos;
    private final String topicName;
    private final JsonObject payload;

    /**
     * Default constructor, using default values.
     */
    public PublishMessage() {
        this.qos = DEFAULT_QOS;
        this.payload = new JsonObject();
        this.topicName = DEFAULT_TOPIC_NAME;
    }

    public PublishMessage(int qos, String topicName, JsonObject payload) {
        this.qos = qos;
        this.payload = new JsonObject();
        this.topicName = DEFAULT_TOPIC_NAME;
    }

    /**
     * Copy constructor.
     *
     * @param other publish message's copy
     */
    public PublishMessage(PublishMessage other) {
        this.qos = other.qos;
        this.payload = new JsonObject(other.payload.toBuffer());
        this.topicName = other.topicName;
    }

    /**
     * Copy constructor from JSON object, used especially for event bus (de)serialization.
     *
     * @param jsonObject publish message's JSON object representation
     */
    public PublishMessage(JsonObject jsonObject) {
        this.qos = jsonObject.getInteger(JsonKeys.QOS.keyName);
        this.payload = jsonObject.getJsonObject(JsonKeys.PAYLOAD.keyName);
        this.topicName = jsonObject.getString(JsonKeys.TOPIC_NAME.keyName);
    }

    /**
     * De-serialize subscription to JSON object representation.
     *
     * @return JSON object representation
     */
    public JsonObject toJson() {
        return new JsonObject().put(JsonKeys.QOS.keyName, qos)
                               .put(JsonKeys.TOPIC_NAME.keyName, topicName)
                               .put(JsonKeys.PAYLOAD.keyName, payload);
    }

    public int getQos() {
        return qos;
    }

    public String getTopicName() {
        return topicName;
    }

    public JsonObject getPayload() {
        return payload;
    }

    public int getByteSize() {
        return Integer.SIZE + topicName.length() + payload.toBuffer().length();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PublishMessage)) return false;
        PublishMessage that = (PublishMessage) o;
        return getQos() == that.getQos() &&
               getTopicName().equals(that.getTopicName()) &&
               getPayload().equals(that.getPayload());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQos(), getTopicName(), getPayload());
    }

    @Override
    public String toString() {
        return String.format("PublishMessage{qos=%d, topicName='%s', payload='%s'", qos, topicName, payload.encodePrettily());
    }
}
