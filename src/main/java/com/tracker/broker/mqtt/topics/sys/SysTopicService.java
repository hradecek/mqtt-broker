package com.tracker.broker.mqtt.topics.sys;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;

/**
 * Provides methods for manipulation with <tt>$SYS</tt> topic.
 */
@ProxyGen
@VertxGen
public interface SysTopicService {

    /**
     * Service's default event bus address.
     */
    String ADDRESS ="service.mqtt.topics.sys";

    /**
     * Static factory method for instance creation.
     *
     * @param discovery service discovery instance
     * @param readyHandler async result
     * @return SysTopicService implementation
     */
    @GenIgnore
    static SysTopicService create(EventBus eventBus, ServiceDiscovery discovery, Handler<AsyncResult<SysTopicService>> readyHandler) {
        return new SysTopicServiceImpl(eventBus, discovery, readyHandler);
    }

    /**
     * Static factory method for event bus service proxy creation.
     *
     * @param vertx Vert.x instance
     * @param address service event-bus address
     * @return Rx-fied subscription service proxy
     */
    @GenIgnore
    static com.tracker.broker.mqtt.topics.sys.reactivex.SysTopicService createProxy(Vertx vertx, String address) {
        return new com.tracker.broker.mqtt.topics.sys.reactivex.SysTopicService(new SysTopicServiceVertxEBProxy(vertx.getDelegate(), address));
    }

    /**
     * Connect MQTT client.
     *
     * <p>If client was not connected <tt>$SYS/broker/clients/connected</tt> is incremented and
     * <tt>$SYS/broker/clients/maximum</tt> is recomputed according to new values.
     *
     * @param clientId client ID that has been connected
     * @param resultHandler async result
     * @return SysTopicService
     */
    @Fluent
    SysTopicService connectClient(String clientId, Handler<AsyncResult<Void>> resultHandler);

    /**
     * Client closed connection.
     *
     * <p>TODO:
     *
     * @param clientId client ID that has closed connection
     * @param resultHandler async result
     * @return SysTopicService
     */
    @Fluent
    SysTopicService closeClient(String clientId, Handler<AsyncResult<Void>> resultHandler);

    /**
     * Client disconnected.
     *
     * <p>TODO:
     *
     * @param clientId client ID that has been disconnected
     * @param resultHandler async result
     * @return SysTopicService
     */
    @Fluent
    SysTopicService disconnectClient(String clientId, Handler<AsyncResult<Void>> resultHandler);

    /**
     * TODO
     *
     * @param resultHandler async result
     * @return SysTopicService
     */
    @Fluent
    SysTopicService receivedMessage(Handler<AsyncResult<Void>> resultHandler);

    /**
     * TODO
     *
     * @param resultHandler aysnc result
     * @return SysTopicService
     */
    @Fluent
    SysTopicService sentMessage(Handler<AsyncResult<Void>> resultHandler);
}
