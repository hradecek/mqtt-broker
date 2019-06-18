package com.tracker.broker.redis;

public class RedisConfig {

    private RedisConfigRoot redis;

    public void setRedis(final RedisConfigRoot redis) {
        this.redis = redis;
    }

    public String getHost() {
        return redis.getHost();
    }

    public int getPort() {
        return redis.getPort();
    }

    public static class RedisConfigRoot {

        private static final int DEFAULT_PORT = 6379;
        private static final String DEFAULT_HOST = "127.0.0.1";

        private int port = DEFAULT_PORT;
        private String host = DEFAULT_HOST;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    @Override
    public String toString() {
        return String.format("RedisConfig{host=\"%s\", port=%d}", getHost(), getPort());
    }
}
