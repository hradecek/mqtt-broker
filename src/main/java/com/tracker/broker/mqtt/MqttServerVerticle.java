package com.tracker.broker.mqtt;

import com.tracker.broker.config.JsonConfigReader;
import com.tracker.broker.mqtt.subscription.Subscription;
import com.tracker.broker.mqtt.subscription.SubscriptionMessageSourceHelper;
import com.tracker.broker.mqtt.subscription.reactivex.SubscriptionService;
import com.tracker.broker.mqtt.topics.sys.reactivex.SysTopicService;

import io.netty.handler.codec.mqtt.MqttQoS;

import io.reactivex.Completable;
import io.reactivex.Single;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mqtt.MqttServerOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.mqtt.MqttEndpoint;
import io.vertx.reactivex.mqtt.MqttServer;
import io.vertx.reactivex.mqtt.MqttTopicSubscription;
import io.vertx.reactivex.mqtt.messages.MqttPublishMessage;
import io.vertx.reactivex.mqtt.messages.MqttSubscribeMessage;
import io.vertx.reactivex.mqtt.messages.MqttUnsubscribeMessage;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * MQTT server verticle is responsible for starting MQTT Broker server instance and handles MQTT client <=> broker
 * communication.
 */
public class MqttServerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttServerVerticle.class);

    private static final String CONFIG_BROKER_PATH = "config.json";

    private SysTopicService sysTopic;
    private ServiceDiscovery discovery;
    private SubscriptionService subscriptions;

    @Override
    public Completable rxStart() {
        sysTopic = com.tracker.broker.mqtt.topics.sys.SysTopicService.createProxy(vertx, SysTopicService.ADDRESS);
        discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));
        subscriptions = com.tracker.broker.mqtt.subscription.SubscriptionService.createProxy(vertx.getDelegate(), com.tracker.broker.mqtt.subscription.SubscriptionService.ADDRESS);

        return deployMqttServer();
    }

    private Completable deployMqttServer() {
        return readConfiguration().flatMap(this::startMqttServer)
                                  .ignoreElement()
                                  .doOnComplete(() -> LOGGER.info(String.format("Started MQTT Broker (%s)", MqttServerOptions.class.getSimpleName())));
    }

    private Single<BrokerConfig> readConfiguration() {
        return new JsonConfigReader<>(vertx, BrokerConfig.class)
                .read(CONFIG_BROKER_PATH)
                .doOnError(ex -> LOGGER.error(String.format("Cannot read MQTT broker configuration '%s'", CONFIG_BROKER_PATH), ex));
    }

    private Single<MqttServer> startMqttServer(final BrokerConfig config) {
        return MqttServer.create(vertx, new MqttServerOptions().setHost(config.getHost()).setPort(config.getPort()))
                         .endpointHandler(new ClientEndpoint(vertx.eventBus(), discovery, sysTopic,  subscriptions))
                         .rxListen()
                         .doOnSuccess(ms -> LOGGER.info(String.format("MQTT Server is listening on %d.", ms.actualPort())))
                         .doOnError(ex -> LOGGER.error("Cannot start MQTT server", ex));
    }

    public static class ClientEndpoint implements Handler<MqttEndpoint> {

        private static final Logger LOGGER = LoggerFactory.getLogger(ClientEndpoint.class);

        private final EventBus eventBus;
        private final ServiceDiscovery discovery;
        private final SysTopicService sysTopic;
        private final SubscriptionService subscriptions;

        ClientEndpoint(EventBus eventBus,
                       ServiceDiscovery discovery,
                       SysTopicService sysTopic,
                       SubscriptionService subscriptions) {
            this.eventBus = eventBus;
            this.discovery = discovery;
            this.sysTopic = sysTopic;
            this.subscriptions = subscriptions;
        }

        public void handle(final MqttEndpoint endpoint) {
            LOGGER.info(String.format("Client '%s' connected.", endpoint.clientIdentifier()));
            sysTopic.rxConnectClient(endpoint.clientIdentifier())
                    .subscribe()
                    .dispose();

            // TODO on err
            SubscriptionMessageSourceHelper.subscriptionMessageConsumer(discovery)
                                           .subscribe(consumer -> consumer.handler(message -> {
                                               System.out.println("Update " + message.body());
                                               endpoint.rxPublish("$SYS/broker/clients/maximum", Buffer.buffer(message.body().toString()), MqttQoS.AT_LEAST_ONCE, false, false)
                                                       .subscribe(s -> System.out.println("DONE " + s));
                                           }));

            SubscriptionHandler subscription = new SubscriptionHandler(endpoint.clientIdentifier(), subscriptions);

            endpoint.subscribeHandler(subscription::subscribe)
                    .unsubscribeHandler(subscription::unsubscribe)
                    .publishHandler(publishHandler(endpoint.clientIdentifier())::accept)
                    // TODO: Dump all you can
                    .exceptionHandler(e -> LOGGER.error(e.getMessage()))
                    .closeHandler(__ -> sysTopic.rxCloseClient(endpoint.clientIdentifier()).subscribe())
                    // TODO When disconnecting remove message consumer for subscriptions created above
                    // TODO Remove Subscriptions
                    .disconnectHandler(__ -> sysTopic.rxDisconnectClient(endpoint.clientIdentifier()).subscribe())
                    .accept();
        }

        private Consumer<MqttPublishMessage> publishHandler(String clientId) {
            return publishMessage -> {
                LOGGER.info("Publishing " + publishMessage.payload().toJsonObject().encodePrettily() + " to " + publishMessage.topicName());
//                eventBus.publish(publishMessage.topicName(), new PublishMessage(clientId, publishMessage.payload().toJsonObject()));
                eventBus.publish(publishMessage.topicName(), publishMessage.payload().toJsonObject());
            };
        }

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
