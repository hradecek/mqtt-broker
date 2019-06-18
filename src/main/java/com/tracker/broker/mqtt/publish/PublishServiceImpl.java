package com.tracker.broker.mqtt.publish;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class PublishServiceImpl implements PublishService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublishServiceImpl.class);

//    private final Vertx vertx;
//    private final RedisServiceHelper redis;
//
//    public PublishServiceImpl(Vertx vertx, RedisServiceHelper redis) {
//        this.vertx = vertx;
//        this.redis = redis;
//    }
//
//    @Override
//    public PublishService publish(String clientId, int qos, String topicName, JsonObject message, Handler<AsyncResult<Void>> result) {
////        redis.rxReceivedMessage(message.)
//        return this;
//    }
}
