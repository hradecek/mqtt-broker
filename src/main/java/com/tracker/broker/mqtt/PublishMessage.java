package com.tracker.broker.mqtt;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * TODO
 */
@DataObject(generateConverter = true, publicConverter = false)
public class PublishMessage {

    /**
     * Represents JSON object structure - keys definition.
     */
    public enum JsonKeys {
        CLIENT_ID("clientId"),
        BODY("body");

        final String keyName;

        JsonKeys(String keyName) {
            this.keyName = keyName;
        }

        public String keyName() {
            return keyName;
        }
    }

    /**
     * Default client ID.
     */
    public static final String DEFAULT_CLIENT_ID = "";

    /**
     * Default body.
     */
    public static final JsonObject DEFAULT_BODY = new JsonObject();

    private final String clientId;
    private final JsonObject body;

    /**
     * Default constructor, using default values.
     */
    public PublishMessage() {
        this.body = DEFAULT_BODY;
        this.clientId = DEFAULT_CLIENT_ID;
    }

    /**
     * Constructor.
     *
     * @param clientId client identification
     * @param body     body in JSON format
     */
    public PublishMessage(String clientId, JsonObject body) {
        this.clientId = clientId;
        this.body = body;
    }

    /**
     * Copy constructor.
     *
     * @param other publish message's copy
     */
    public PublishMessage(PublishMessage other) {
        this.clientId = other.clientId;
        this.body = other.body;
    }

    /**
     * Copy constructor from JSON object, used especially for event bus (de)serialization.
     *
     * @param jsonObject publish message's JSON object representation
     */
    public PublishMessage(JsonObject jsonObject) {
        this.clientId = jsonObject.getString(JsonKeys.CLIENT_ID.keyName);
        this.body = jsonObject.getJsonObject(JsonKeys.BODY.keyName);
    }

    /**
     * De-serialize coordinates to JSON object representation.
     *
     * @return JSON object representation
     */
    public JsonObject toJson() {
        return new JsonObject().put(JsonKeys.CLIENT_ID.keyName, clientId)
                               .put(JsonKeys.BODY.keyName, body);
    }

    /**
     * Get client ID.
     *
     * @return client ID
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Get message's body as JSON.
     *
     * @return message's body
     */
    public JsonObject getBody() {
        return body;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof PublishMessage)) {
            return false;
        }

        PublishMessage that = (PublishMessage) other;

        return Objects.equals(getClientId(), that.getClientId()) && Objects.equals(getBody(), that.getBody());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClientId(), getBody());
    }

    @Override
    public String toString() {
        return String.format("PublishMessage{clientId='%s', body='%s'}", clientId, body.encodePrettily());
    }
}
