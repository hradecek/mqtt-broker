package com.tracker.broker.mqtt.client;

import com.tracker.broker.mqtt.subscription.reactivex.SubscriptionService;
import com.tracker.broker.redis.reactivex.RedisService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * TODO
 */
public class ClientServiceImpl implements ClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final RedisService redis;
    private final SubscriptionService subscriptions;

    public ClientServiceImpl(RedisService redis, SubscriptionService subscriptions) {
        this.redis = redis;
        this.subscriptions = subscriptions;
    }

    @Override
    public ClientService connectClient(final JsonObject connection, Handler<AsyncResult<Void>> result) {
        LOGGER.info("connectClient " + connection);
        redis.addClient(connection.getString("clientId"), r -> {
            if (r.succeeded()) {
                result.handle(Future.succeededFuture());
            } else {
                result.handle(Future.failedFuture(r.cause()));
            }
        });
        return this;
    }

    @Override
    public ClientService publish(String clientId, String topicName, JsonObject message, Handler<AsyncResult<Void>> result) {
        LOGGER.info(String.format("publish %s %s %s", clientId, topicName, message));
        subscriptions.rxPublishUpdates(new JsonObject().put("update", "is updated")).subscribe().dispose();
        return this;
    }
}
