package com.tracker.broker.mqtt;

import com.tracker.broker.config.JsonConfigReader;
import com.tracker.broker.mqtt.client.reactivex.ClientService;
import com.tracker.broker.mqtt.subscription.Subscription;
import com.tracker.broker.mqtt.subscription.reactivex.SubscriptionService;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mqtt.MqttServerOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.mqtt.MqttEndpoint;
import io.vertx.reactivex.mqtt.MqttServer;
import io.vertx.reactivex.mqtt.MqttTopicSubscription;
import io.vertx.reactivex.mqtt.messages.MqttSubscribeMessage;
import io.vertx.reactivex.mqtt.messages.MqttUnsubscribeMessage;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.types.MessageSource;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MQTT server verticle is responsible for starting MQTT Broker server instance and handles client <=> broker
 * communication.
 */
public class MqttServerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttServerVerticle.class);

    private static final String CONFIG_BROKER_PATH = "config.json";

    private ServiceDiscovery discovery;
    private ClientService client;
    private SubscriptionService subscriptions;

    @Override
    public Completable rxStart() {
        discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));
        subscriptions = com.tracker.broker.mqtt.subscription.SubscriptionService.createProxy(vertx.getDelegate(), com.tracker.broker.mqtt.subscription.SubscriptionService.ADDRESS);
        client = com.tracker.broker.mqtt.client.ClientService.createProxy(vertx.getDelegate(), com.tracker.broker.mqtt.client.ClientService.ADDRESS);

        return deployMqttServer();

    }

    private Completable deployMqttServer() {
        return new JsonConfigReader<>(vertx, BrokerConfig.class)
                .read(CONFIG_BROKER_PATH)
                .doOnError(ex -> LOGGER.error("Cannot read MQTT broker configuration " + CONFIG_BROKER_PATH, ex))
                .flatMap(this::startMqttServer)
                .ignoreElement()
                .doOnComplete(() -> LOGGER.info("Started " + MqttServerOptions.class.getSimpleName()));
    }

    private Single<MqttServer> startMqttServer(final BrokerConfig config) {
        return MqttServer.create(vertx, new MqttServerOptions().setHost(config.getHost()).setPort(config.getPort()))
                         .endpointHandler(new ClientEndpoint(discovery, client, subscriptions))
                         .rxListen()
                         .doOnSuccess(ms -> LOGGER.info(String.format("MQTT Server is listening on %d", ms.actualPort())))
                         .doOnError(ex -> LOGGER.error("Cannot start MQTT server", ex));
    }

    public static class ClientEndpoint implements Handler<MqttEndpoint> {

        private static final Logger LOGGER = LoggerFactory.getLogger(ClientEndpoint.class);

        private final ServiceDiscovery discovery;
        private final ClientService client;
        private final SubscriptionService subscriptions;

        ClientEndpoint(ServiceDiscovery discovery, ClientService client, SubscriptionService subscriptions) {
            this.discovery = discovery;
            this.client = client;
            this.subscriptions = subscriptions;
        }

        public void handle(final MqttEndpoint endpoint) {
            LOGGER.info("Connected: " + endpoint.clientIdentifier());

            // Get Subscriptions changes consumer
            // TODO can be moved above???
            LOGGER.info("GETTING consumer");
            MessageSource.<JsonObject>getConsumer(
                    discovery.getDelegate(),
                    new JsonObject().put("name", "subscriptions"),
                    ar -> {
                        if (ar.succeeded()) {
                            ar.result().handler(message -> {
                                LOGGER.info(">>> UPDATE " + message.body());
                            });
                        } else {
                            LOGGER.error(ar.cause());
                        }
                    }
            );

            SubscriptionHandler subscription = new SubscriptionHandler(endpoint.clientIdentifier(), subscriptions);

            endpoint.subscribeHandler(subscription::subscribe)
                    .unsubscribeHandler(subscription::unsubscribe)
//                    .publishHandler(message -> LOGGER.info("Publish: " + message.topicName() + " " + message.payload().toJsonObject()))
                    .publishHandler(message -> {
                        client.rxPublish(endpoint.clientIdentifier(), message.topicName(), message.payload().toJsonObject()).subscribe().dispose();
                    })
                    .exceptionHandler(e -> LOGGER.error(e.getMessage())) // TODO: Dump all you can
                    .closeHandler(__ -> LOGGER.info("Closed: " + endpoint.clientIdentifier()))
                    // TODO When disconnecting remove message consumer for subscriptions created above
                    // TODO Remove Subscriptions
                    .disconnectHandler(__ -> LOGGER.info("Disconnected: " + endpoint.clientIdentifier()))
                    .accept();
        }

//        private static class PublishHandler {
//
//            private final String clientId;
//
//            PublishHandler(String clientId, Publish subscriptions) {
//                this.clientId = clientId;
//                this.subscriptions = subscriptions;
//            }
//            void publish(MqttPublishMessage publishMessage) {
//
//            }
//        }

        private static class SubscriptionHandler {

            private final String clientId;
            private final SubscriptionService subscriptions;

            SubscriptionHandler(String clientId, SubscriptionService subscriptions) {
                this.clientId = clientId;
                this.subscriptions = subscriptions;
            }

            void subscribe(MqttSubscribeMessage subscribeMessage) {
                subscriptions.rxAddSubscriptions(clientId, subscribeMessageToSet(subscribeMessage))
                             .subscribe()
                             .dispose();
            }

            private static Set<Subscription> subscribeMessageToSet(MqttSubscribeMessage message) {
                return message.topicSubscriptions()
                              .stream()
                              .map(SubscriptionHandler::toSubscription)
                              .collect(Collectors.toSet());
            }

            private static Subscription toSubscription(MqttTopicSubscription topicSubscription) {
                return new Subscription(topicSubscription.qualityOfService().value(), topicSubscription.topicName());
            }

            void unsubscribe(MqttUnsubscribeMessage unsubscribeMessage) {
                subscriptions.rxRemoveSubscriptions(clientId, new HashSet<>(unsubscribeMessage.topics()))
                             .subscribe()
                             .dispose();
            }
        }
    }
}
