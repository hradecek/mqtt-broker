package com.tracker.broker.config;

import io.reactivex.Single;

/**
 * Reads configuration file and returns POJO representation.
 *
 * @param <T> type of POJO
 */
@FunctionalInterface
public interface ConfigReader<T> {

    /**
     * Read configuration file.
     *
     * @param configPath path to configuration ile
     * @return Single representing configuration
     */
    Single<T> read(final String configPath);
}
