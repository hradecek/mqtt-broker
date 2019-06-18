package com.tracker.broker.mqtt.topics.sys;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;

/**
 * TODO
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
    static SysTopicService create(ServiceDiscovery discovery, Handler<AsyncResult<SysTopicService>> readyHandler) {
        return new SysTopicServiceImpl(discovery, readyHandler);
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
     * TODO
     *
     * @param clientId
     * @param resultHandler
     * @return
     */
    @Fluent
    SysTopicService connectClient(String clientId, Handler<AsyncResult<Void>> resultHandler);

    /**
     *
     * @param clientId
     * @param resultHandler
     * @return
     */
    @Fluent
    SysTopicService closeClient(String clientId, Handler<AsyncResult<Void>> resultHandler);

    /**
     * TODO
     *
     * @param clientId
     * @param resultHandler
     * @return
     */
    @Fluent
    SysTopicService disconnectClient(String clientId, Handler<AsyncResult<Void>> resultHandler);
//
//    @Fluent
//    SysTopicService receivedMessage(int bytes, Handler<AsyncResult<Void>> resultHandler);
//
//    @Fluent
//    SysTopicService getClientsConnected(Handler<AsyncResult<Integer>> resultHandler);

//    /**
//     * TODO
//     *
//     * @param clientId
//     * @param lat
//     * @param lng
//     * @param resultHandler
//     * @return
//     */
//    @Fluent
//    SysTopicService addCoordinates(String clientId, double lat, double lng ,Handler<AsyncResult<Void>> resultHandler);
}
