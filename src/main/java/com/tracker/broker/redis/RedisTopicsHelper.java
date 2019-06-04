package com.tracker.broker.redis;

/**
 * TODO
 */
public class RedisTopicsHelper {

    private RedisTopicsHelper() {
        throw new UnsupportedOperationException(String.format("Cannot instantiate '%s' class", RedisTopicsHelper.class.getName()));
    }

    public static String fromRedis(String topic) {
        return topic.replaceAll(":", "/");
    }

    public static String toRedis(String topic) {
        return topic.replaceAll("/", ":");
    }

    public static String toRedis(String... topic) {
        return String.join(":", topic);
    }
}
