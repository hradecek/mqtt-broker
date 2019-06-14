package com.tracker.broker.mqtt;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.mqtt.MqttTopicSubscription;
import io.vertx.reactivex.mqtt.messages.MqttSubscribeMessage;
import io.vertx.reactivex.mqtt.messages.MqttUnsubscribeMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO
 */
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    private final Map<String, JsonArray> subscriptions = new HashMap<>();

    @Override
    public SubscriptionService addSubscriptions(JsonObject subscription, Handler<AsyncResult<Void>> resultHandler) {
        LOGGER.info("addSubscriptions " + subscription);

        final String clientId = subscription.getString("clientId");
        final JsonArray topics = subscription.getJsonArray("topics");
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
