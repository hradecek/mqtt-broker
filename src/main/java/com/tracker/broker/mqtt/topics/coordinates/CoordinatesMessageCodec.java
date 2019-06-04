package com.tracker.broker.mqtt.topics.coordinates;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * TODO
 */
public class CoordinatesMessageCodec implements MessageCodec<JsonObject, Coordinates> {
    @Override
    public void encodeToWire(Buffer buffer, JsonObject jsonObject) {
        String jsonObjectStr = jsonObject.encode();

        buffer.appendInt(jsonObjectStr.length());
        buffer.appendString(jsonObjectStr);
    }

    @Override
    public Coordinates decodeFromWire(int position, Buffer buffer) {
        int pos = position;
        int length = buffer.getInt(pos);

        String jsonStr = buffer.getString(pos += 4, pos + length);
        JsonObject json = new JsonObject(jsonStr);

        return new Coordinates(json.getDouble(Coordinates.JsonKeys.LAT.keyName), json.getDouble(Coordinates.JsonKeys.LNG.keyName));
    }

    @Override
    public Coordinates transform(JsonObject jsonObject) {
        return new Coordinates(jsonObject);
    }

    @Override
    public String name() {
        return CoordinatesMessageCodec.class.getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
