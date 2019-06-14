package com.tracker.broker.mqtt.subscription;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 */
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    private final Map<String, JsonArray> subscriptions = new HashMap<>();

    private final Vertx vertx;

    public SubscriptionServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public SubscriptionService addSubscriptions(String clientId, JsonArray topics, Handler<AsyncResult<Void>> resultHandler) {
        LOGGER.info("addSubscriptions " + topics);

        if (subscriptions.containsKey(clientId)) {
            subscriptions.get(clientId).addAll(topics);
        } else {
            subscriptions.put(clientId, topics);
        }
        resultHandler.handle(Future.succeededFuture());

        return this;
    }

    @Override
    public SubscriptionService removeSubscriptions(JsonObject unsubscriptions, Handler<AsyncResult<Void>> resultHandler) {
        return this;
    }

    @Override
    public SubscriptionService publishUpdates(JsonObject updates, Handler<AsyncResult<Void>> resultHandler) {
//        for (Map.Entry<String, JsonArray> subscription : subscriptions.entrySet()) {
//            String clientId = subscription.getKey();
//            for (int i = 0; i < subscription.getValue().size(); ++i) {
        vertx.eventBus().publish("subscriptions-changes", updates);
//            }
//        }

        return this;
    }

//    public SubscriptionService addSubscription(final String clientId,
//                                               final MqttSubscribeMessage message,
//                                               Handler<AsyncResult<Void>> resultHandler) {
//        if (subscriptions.containsKey(clientId)) {
//            subscriptions.get(clientId).addAll(message.topicSubscriptions());
//        } else {
//            subscriptions.put(clientId, message.topicSubscriptions());
//        }
//        resultHandler.handle(Future.succeededFuture());
//
//        return this;
//    }
//
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
//            resultHandler.handle(Future.failedFuture(String.format("Client %s does not exist", clientId)));
//        }
//        resultHandler.handle(Future.succeededFuture());
//
//        return this;
//    }
}
