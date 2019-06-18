package com.tracker.broker.mqtt.subscription;

import com.tracker.broker.redis.RedisServiceHelper;

import io.reactivex.Completable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.redis.RedisClient;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO
 */
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    private RedisClient redisClient;

    /**
     * TODO
     *
     * @param discovery
     * @param readyHandler
     */
    SubscriptionServiceImpl(ServiceDiscovery discovery, Handler<AsyncResult<SubscriptionService>> readyHandler) {
        RedisServiceHelper.getClient(discovery)
                          .subscribe(redisClient -> {
                              this.redisClient = redisClient;
                              readyHandler.handle(Future.succeededFuture(this));
                          }, ex -> readyHandler.handle(Future.failedFuture(ex)))
                          .dispose();
    }

    @Override
    public SubscriptionService addSubscriptions(String clientId, Set<Subscription> subscriptions, Handler<AsyncResult<Void>> resultHandler) {
        redisClient.rxSaddMany(subscriptionsTopicsKey(clientId), subscriptionsToList(subscriptions))
                   .doOnError(ex -> LOGGER.error(String.format("Cannot add subscriptions '%s' to '%s'", subscriptions, subscriptionsTopicsKey(clientId))))
                   .subscribe();
        return this;
    }

    private static List<String> subscriptionsToList(Set<Subscription> subscriptions) {
        return subscriptions.stream()
                            .map(subscription -> subscription.toJson().toString())
                            .collect(Collectors.toList());
    }

    @Override
    public SubscriptionService removeSubscriptions(String clientId, Set<String> topicNames, Handler<AsyncResult<Void>> resultHandler) {
        String key = subscriptionsTopicsKey(clientId);
        redisClient.rxSmembers(key)
                   .flatMap(subscriptions ->
                           redisClient.rxSremMany(key, unsubscriptions(topicNames, subscriptionsFromJsonArray(new JsonArray(subscriptions.toString())))))
                                      .doOnError(ex -> LOGGER.error(String.format("Cannot remove '%s' from '%s': %s", topicNames, key, ex)))
                   .ignoreElement()
                   .subscribe(() -> resultHandler.handle(Future.succeededFuture()),
                              ex -> resultHandler.handle(Future.failedFuture(ex)));
        return this;
    }

    // TODO Create redis utils for creating topic "string"
    private static String subscriptionsTopicsKey(String clientId) {
        return String.format("subscriptions:%s:topics", clientId);
    }

    private static List<String> unsubscriptions(Set<String> unsubscriptions, List<Subscription> subscriptions) {
        return subscriptions.stream()
                            .filter(subscription -> unsubscriptions.contains(subscription.getTopicName()))
                            .map(Subscription::toJson)
                            .map(JsonObject::toString)
                            .collect(Collectors.toList());
    }

    private static List<Subscription> subscriptionsFromJsonArray(JsonArray jsonArray) {
        return jsonArray.stream()
                        .map(subscription -> new Subscription(new JsonObject(subscription.toString())))
                        .collect(Collectors.toList());
    }


    private <C extends Completable> void subscribeWithAsyncHandler(C completable, Handler<AsyncResult<Void>> resultHandler) {
        completable.subscribe(() -> resultHandler.handle(Future.succeededFuture()),
                              ex -> resultHandler.handle(Future.failedFuture(ex)))
                    .dispose();
    }

//    @Override
//    public SubscriptionService publishUpdates(JsonObject updates, Handler<AsyncResult<Void>> resultHandler) {
//        vertx.eventBus().publish(SubscriptionsVerticle.SERVICE_ADDRESS_SUBSCRIPTIONS_CHANGES, updates);
//        return this;
//    }
//
}
