package com.tracker.broker.mqtt.topics.coordinates;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Data object representing coordinates message.
 *
 * Coordinates are represented by:
 * <ul>
 *     <li>latitude - double value,
 *     <li>longitude - double value.
 * </ul>
 */
@DataObject(generateConverter = true, publicConverter = false)
public class Coordinates {

    /**
     * Represents JSON object structure - keys definition.
     */
    public enum JsonKeys {
        LAT("lat"),
        LNG("lng");

        final String keyName;

        JsonKeys(String keyName) {
            this.keyName = keyName;
        }

        public String keyName() {
            return keyName;
        }
    }

    /**
     * Default latitude value.
     */
    public static final double DEFAULT_LAT = 0.0D;

    /**
     * Default longitude value.
     */
    public static final double DEFAULT_LNG = 0.0D;

    private final double lat;
    private final double lng;

    /**
     * Default constructor, using default values.
     */
    public Coordinates() {
        this.lat = DEFAULT_LAT;
        this.lng = DEFAULT_LNG;
    }

    /**
     * Constructor.
     *
     * @param lat latitude value
     * @param lng longitude value
     */
    public Coordinates(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    /**
     * Copy constructor.
     *
     * @param other coordinates' copy
     */
    public Coordinates(Coordinates other) {
        this.lat = other.lat;
        this.lng = other.lng;
    }

    /**
     * Copy constructor from JSON object, used especially for event bus (de)serialization.
     *
     * @param jsonObject coordinates JSON object representation
     */
    public Coordinates(JsonObject jsonObject) {
        this.lat = jsonObject.getDouble(JsonKeys.LAT.keyName);
        this.lng = jsonObject.getDouble(JsonKeys.LNG.keyName);
    }

    /**
     * De-serialize coordinates to JSON object representation.
     *
     * @return JSON object representation
     */
    public JsonObject toJson() {
        return new JsonObject().put(JsonKeys.LAT.keyName, lat)
                               .put(JsonKeys.LNG.keyName, lng);
    }

    /**
     * Get latitude.
     *
     * @return latitude value
     */
    public double getLat() {
        return lat;
    }

    /**
     * Get longitude.
     *
     * @return longitude value
     */
    public double getLng() {
        return lng;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Coordinates)) {
            return false;
        }

        Coordinates that = (Coordinates) other;
        return Double.compare(that.getLat(), getLat()) == 0 && Double.compare(that.getLng(), getLng()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLat(), getLng());
    }

    @Override
    public String toString() {
        return String.format("Coordinates{lat=%f, lng=%f}", lat, lng);
    }
}
