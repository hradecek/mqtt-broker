package com.tracker.broker.mqttold;

import com.tracker.broker.config.JsonConfigReader;
//import com.tracker.broker.mqttold.reactivex.ClientService;
//import com.tracker.broker.mqttold.reactivex.SubscriptionService;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mqtt.MqttServerOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.mqtt.MqttEndpoint;
import io.vertx.reactivex.mqtt.MqttServer;
import io.vertx.reactivex.mqtt.MqttTopicSubscription;

/**
 * MQTT server verticle is responsible for starting MQTT Broker server instance and handles client <=> broker
 * communication.
 */
public class MqttServerVerticle extends AbstractVerticle {

//    private static final Logger LOGGER = LoggerFactory.getLogger(MqttServerVerticle.class);
//
//    private static final String CONFIG_BROKER_PATH = "config.json";
//
//    @Override
//    public Completable rxStart() {
//        return deployMqttServer().andThen(vertx.rxDeployVerticle(new ServicesVerticle())).ignoreElement();
//    }
//
//    private Completable deployMqttServer() {
//        return new JsonConfigReader<>(vertx, BrokerConfig.class)
//                .read(CONFIG_BROKER_PATH)
//                .doOnError(ex -> LOGGER.error("Cannot read MQTT broker configuration " + CONFIG_BROKER_PATH, ex))
//                .flatMap(this::createMqttServer)
//                .ignoreElement()
//                .doOnComplete(() -> LOGGER.info("Started " + MqttServerOptions.class.getSimpleName()));
//    }
//
//    private Single<MqttServer> createMqttServer(final BrokerConfig config) {
//        final SubscriptionService subscriptions = com.tracker.broker.mqtt.subscription.SubscriptionService.createProxy(vertx.getDelegate(), SubscriptionService.ADDRESS);
//        final ClientService client = com.tracker.broker.mqtt.client.ClientService.createProxy(vertx.getDelegate(), ClientService.ADDRESS);
//
//        return MqttServer.create(vertx, new MqttServerOptions().setHost(config.getHost()).setPort(config.getPort()))
//                         .endpointHandler(new ClientEndpoint(subscriptions, client))
//                         .rxListen()
//                         .doOnSuccess(ms -> LOGGER.info(String.format("MQTT Server is listening on %d", ms.actualPort())))
//                         .doOnError(ex -> LOGGER.error("Cannot start MQTT server", ex));
//    }
//
//    public static class ClientEndpoint implements Handler<MqttEndpoint> {
//
//        private static final Logger LOGGER = LoggerFactory.getLogger(ClientEndpoint.class);
//
//        private SubscriptionService subscriptions;
//        private ClientService clients;
//
//        public ClientEndpoint(SubscriptionService subscriptions, ClientService clients) {
//            this.subscriptions = subscriptions;
//            this.clients = clients;
//        }
//
//        public void handle(final MqttEndpoint endpoint) {
//            LOGGER.info("Connected: " + endpoint.clientIdentifier());
//            final JsonObject connection = new JsonObject().put("clientId", endpoint.clientIdentifier());
//            clients.rxConnectClient(connection).subscribe().dispose();
//            endpoint
//                    .publishHandler(message -> LOGGER.info("Publish: " + message.topicName() + " " + message.payload().toJsonObject()))
////                    .subscribeHandler(message -> LOGGER.info("Subscribe: " + message.topicSubscriptions()))
//                    .subscribeHandler(message -> {
//                        final JsonArray topics = new JsonArray();
//                        for (MqttTopicSubscription subscription : message.topicSubscriptions()) {
//                            topics.add(new JsonObject().put("topic", subscription.topicName())
//                                    .put("qos", subscription.qualityOfService()));
//                        }
//                        final JsonObject json =
//                                new JsonObject().put("clientId", endpoint.clientIdentifier())
//                                        .put("topic", topics);
//
//                        subscriptions.rxAddSubscriptions(json).subscribe().dispose();
//                            })
//                    .unsubscribeHandler(message -> LOGGER.info("Unsubscribe: " + message.topics()))
//                    .exceptionHandler(e -> LOGGER.error(e.getMessage())) // TODO: Dump all you can
//                    .closeHandler(__ -> LOGGER.info("Closed: " + endpoint.clientIdentifier()))
//                    .disconnectHandler(__ -> LOGGER.info("Disconnected: " + endpoint.clientIdentifier()))
//                    .accept();
//        }
//    }
}
