package com.tracker.broker.mqtt.subscription;

import com.tracker.broker.redis.reactivex.RedisService;
import io.reactivex.Completable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Set;

/**
 * TODO
 */
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    private final Vertx vertx;
    private final RedisService redis;

    /**
     * TODO
     *
     * @param vertx
     * @param redis
     */
    SubscriptionServiceImpl(Vertx vertx, RedisService redis) {
        this.vertx = vertx;
        this.redis = redis;
    }

    @Override
    public SubscriptionService addSubscriptions(String clientId, Set<Subscription> subscriptions, Handler<AsyncResult<Void>> resultHandler) {
        subscribeWithAsyncHandler(redis.rxAddSubscriptions(clientId, subscriptions), resultHandler);
        return this;
    }

    @Override
    public SubscriptionService removeSubscriptions(String clientId, Set<String> topicNames, Handler<AsyncResult<Void>> resultHandler) {
        subscribeWithAsyncHandler(redis.rxRemoveSubscriptions(clientId, topicNames), resultHandler);
        return this;
    }

    private <C extends Completable> void subscribeWithAsyncHandler(C completable, Handler<AsyncResult<Void>> resultHandler) {
        completable.subscribe(() -> resultHandler.handle(Future.succeededFuture()),
                              ex -> resultHandler.handle(Future.failedFuture(ex)))
                    .dispose();
    }

    @Override
    public SubscriptionService publishUpdates(JsonObject updates, Handler<AsyncResult<Void>> resultHandler) {
        vertx.eventBus().publish(SubscriptionsVerticle.SERVICE_ADDRESS_SUBSCRIPTIONS_CHANGES, updates);
        return this;
    }

//    public SubscriptionService removeSubscription(final String clientId,
//                                                  final MqttUnsubscribeMessage message,
//                                                  Handler<AsyncResult<Void>> resultHandler) {
//        if (subscriptions.containsKey(clientId)) {
//            final List<MqttTopicSubscription> updated =
//                    subscriptions.get(clientId)
//                            .stream()
//                            .filter(subscription -> !message.topics().contains(subscription.topicName()))
//                            .collect(Collectors.toList());
//
//            if (!updated.isEmpty()) {
//                subscriptions.put(clientId, updated);
//            } else {
//                subscriptions.remove(clientId);
//            }
//        } else {
//            resultHandler.subscribe(Future.failedFuture(String.format("Client %s does not exist", clientId)));
//        }
//        resultHandler.subscribe(Future.succeededFuture());
//
//        return this;
//    }
}
