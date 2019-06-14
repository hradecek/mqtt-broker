package com.tracker.broker.config;

import io.reactivex.Single;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;

/**
 * Reads specific configuration file in JSON format and converts to POJO class.
 *
 * @param <T> configuration POJO type
 */
public class JsonConfigReader<T> implements ConfigReader<T> {

    private final Vertx vertx;
    private final Class<T> clazz;

    /**
     * Constructor.
     *
     * @param vertx Vert.x instance
     * @param clazz configuration POJO class
     */
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
