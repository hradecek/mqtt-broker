package com.tracker.broker.config;

import io.reactivex.Single;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;

/**
 * TODO
 * @param <T>
 */
public class JsonConfigReader<T> implements ConfigReader<T> {

    private final Vertx vertx;
    private final Class<T> clazz;

    public JsonConfigReader(final Vertx vertx, final Class<T> clazz) {
        this.vertx = vertx;
        this.clazz = clazz;
    }

    @Override
    public Single<T> read(final String configPath) {
        return vertx.fileSystem()
                    .rxReadFile(configPath)
                    .map(Buffer::toJsonObject)
                    .map(jsonObject -> jsonObject.mapTo(clazz));
    }
}
