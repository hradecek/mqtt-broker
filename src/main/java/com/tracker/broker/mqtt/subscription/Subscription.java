package com.tracker.broker.mqtt.subscription;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Data object representing subscription request.
 *
 * <p>Subscription request consists of:
 * <ul>
 *     <li>QoS - Quality of service represented by integer (0, 1, 2)
 *     <li>Topic name - name of the topic to be subscribe to
 * </ul>
 */
@DataObject(generateConverter = true, publicConverter = false)
public class Subscription {

    /**
     * Represents JSON object structure - keys definition.
     */
    public enum JsonKeys {
        QOS("qos"),
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
    public static final int DEFAULT_QOS = 0;

    /**
     * Default topic name, when no topic is specified (empty string - safer than null).
     */
    public static final String DEFAULT_TOPIC_NAME = "";

    private final int qos;
    private final String topicName;

    /**
     * Default constructor, using default values.
     */
    public Subscription() {
        this.qos = DEFAULT_QOS;
        this.topicName = DEFAULT_TOPIC_NAME;
    }

    /**
     * Constructor.
     *
     * @param qos QoS level (0, 1, 2)
     * @param topicName topic's name
     */
    public Subscription(int qos, String topicName) {
        // TODO validate QoS level or Enum
        this.qos = qos;
        this.topicName = topicName;
    }

    /**
     * Copy constructor.
     *
     * @param other subscription's copy
     */
    public Subscription(Subscription other) {
        this.qos = other.qos;
        this.topicName = other.topicName;
    }

    /**
     * Copy constructor from JSON object, used especially for event bus (de)serialization.
     *
     * @param jsonObject subscription JSON object representation
     */
    public Subscription(JsonObject jsonObject) {
        this.qos = jsonObject.getInteger(JsonKeys.QOS.keyName);
        this.topicName = jsonObject.getString(JsonKeys.TOPIC_NAME.keyName);
    }

    /**
     * De-serialize subscription to JSON object representation.
     *
     * @return JSON object representation
     */
    public JsonObject toJson() {
        return new JsonObject().put(JsonKeys.QOS.keyName, qos)
                               .put(JsonKeys.TOPIC_NAME.keyName, topicName);
    }

    /**
     * Getter for QoS.
     *
     * @return QoS level
     */
    public int getQos() {
        return qos;
    }

    /**
     * Getter for topic name.
     *
     * @return topic's name
     */
    public String getTopicName() {
        return topicName;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Subscription)) {
            return false;
        }

        Subscription that = (Subscription) other;
        return qos == that.qos && topicName.equals(that.topicName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qos, topicName);
    }

    @Override
    public String toString() {
        return String.format("Subscription{QoS=%d, topicName=\"%s\"}", qos, topicName);
    }
}
