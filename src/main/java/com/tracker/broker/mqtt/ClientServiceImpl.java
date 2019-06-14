package com.tracker.broker.mqtt;

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

    public ClientServiceImpl(RedisService redis) {
        this.redis = redis;
    }

    @Override
    public ClientServiceImpl connectClient(final JsonObject connection, Handler<AsyncResult<Void>> result) {
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
}
