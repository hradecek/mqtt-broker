package com.tracker.broker.mqtt.publish;

//import com.tracker.broker.redis.reactivex.RedisServiceHelper;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
@VertxGen
public interface PublishService {

    /**
     * Service's default event bus address.
     */
    String ADDRESS = "publish.queue";

    /**
     * Static factory method.
     *
     * @param vertx Vert.x instance
     * @param redis redis service
     * @return PublishService
     */
//    @GenIgnore
//    static PublishService create(Vertx vertx, RedisServiceHelper redis) {
//        return new PublishServiceImpl(vertx, redis);
//    }
//
//    /**
//     * Static factory method for event bus service proxy creation.
//     *
//     * @param vertx Vert.x instance
//     * @param address service event-bus address
//     * @return Rx-fied subscription service proxy
//     */
//    @GenIgnore
//    static com.tracker.broker.mqtt.publish.reactivex.PublishService createProxy(Vertx vertx, String address) {
//        return new com.tracker.broker.mqtt.publish.reactivex.PublishService(new PublishServiceVertxEBProxy(vertx, address));
//    }
//
//    @Fluent
//    PublishService publish(String clientId, int qos, String topicName, JsonObject message, Handler<AsyncResult<Void>> result);
}
