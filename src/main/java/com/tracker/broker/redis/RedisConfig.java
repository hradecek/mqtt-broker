package com.tracker.broker.redis;

/**
 * Represents Redis configuration.
 */
public class RedisConfig {

    private RedisConfigRoot redis;

    /**
     * Set top-level configuration.
     *
     * @param redis configuration's top-level object
     */
    public void setRedis(final RedisConfigRoot redis) {
        this.redis = redis;
    }

    /**
     * Get host redis is running on.
     *
     * @return host
     */
    public String getHost() {
        return redis.getHost();
    }

    /**
     * Get port redis is listening on.
     *
     * @return port
     */
    public int getPort() {
        return redis.getPort();
    }

    /**
     * Represents top-level redis configuration.
     */
    public static class RedisConfigRoot {

        private static final int DEFAULT_PORT = 6379;
        private static final String DEFAULT_HOST = "127.0.0.1";

        private int port = DEFAULT_PORT;
        private String host = DEFAULT_HOST;

        /**
         * Get host redis is running on.
         *
         * @return host
         */
        public String getHost() {
            return host;
        }

        /**
         * Set host redis is running on.
         *
         * @param host host
         */
        public void setHost(String host) {
            this.host = host;
        }

        /**
         * Get port redis is listening on.
         *
         * @return port
         */
        public int getPort() {
            return port;
        }

        /**
         * Set port redis is listening on.
         *
         * @param port port
         */
        public void setPort(int port) {
            this.port = port;
        }
    }

    @Override
    public String toString() {
        return String.format("RedisConfig{host=\"%s\", port=%d}", getHost(), getPort());
    }
}
