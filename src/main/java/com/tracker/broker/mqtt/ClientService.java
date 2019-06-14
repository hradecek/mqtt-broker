package com.tracker.broker.mqtt;

import com.tracker.broker.redis.reactivex.RedisService;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * TODO
 */
@ProxyGen
@VertxGen
public interface ClientService {

    String ADDRESS = "client.queue";

    @Fluent
    ClientService connectClient(final JsonObject connection, Handler<AsyncResult<Void>> result);

    @GenIgnore
    static ClientService create(RedisService redis) {
        return new ClientServiceImpl(redis);
    }

    @GenIgnore
    static com.tracker.broker.mqtt.reactivex.ClientService createProxy(Vertx vertx, String address) {
        return new com.tracker.broker.mqtt.reactivex.ClientService(new ClientServiceVertxEBProxy(vertx, address));
    }
}
