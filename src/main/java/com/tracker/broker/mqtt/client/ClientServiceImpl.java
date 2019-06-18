package com.tracker.broker.mqtt.client;

import com.tracker.broker.mqtt.subscription.reactivex.SubscriptionService;
import com.tracker.broker.mqtt.topics.sys.SysTopicService;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * TODO
 */
public class ClientServiceImpl implements ClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final SysTopicService sysTopic;
    private final SubscriptionService subscriptions;

    public ClientServiceImpl(SysTopicService sysTopic, SubscriptionService subscriptions) {
        this.sysTopic = sysTopic;
        this.subscriptions = subscriptions;
    }

//    @Override
//    public ClientService connectClient(final JsonObject connection, Handler<AsyncResult<Void>> result) {
//        LOGGER.info("connectClient " + connection);
//        sysTopic.connectClient(connection.getString("clientId"), r -> {
//            if (r.succeeded()) {
//                result.handle(Future.succeededFuture());
//            } else {
//                result.handle(Future.failedFuture(r.cause()));
//            }
//        });
//        return this;
//    }

//    @Override
//    public ClientService publish(String clientId, String topicName, JsonObject message, Handler<AsyncResult<Void>> result) {
//        LOGGER.info(String.format("publish %s %s %s", clientId, topicName, message));
//        subscriptions.rxPublishUpdates(new JsonObject().put("update", "is updated")).subscribe().dispose();
//        return this;
//    }
}
