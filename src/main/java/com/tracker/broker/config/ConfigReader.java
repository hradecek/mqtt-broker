package com.tracker.broker.config;

import io.reactivex.Single;

/**
 * TODO
 *
 * @param <T>
 */
@FunctionalInterface
public interface ConfigReader<T> {

    /**
     * TODO
     *
     * @param configPath
     * @return
     */
    Single<T> read(final String configPath);
}
