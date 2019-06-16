package com.tracker.broker.mqtt;

import io.vertx.mqtt.MqttServerOptions;

/**
 * TODO
 */
public class BrokerConfig {

    private BrokerConfigRoot broker;

    public void setBroker(BrokerConfigRoot broker) {
        this.broker = broker;
    }

    public String getHost() {
        return broker.getHost();
    }

    public int getPort() {
        return broker.getPort();
    }

    public static class BrokerConfigRoot {

        private static final int DEFAULT_PORT = MqttServerOptions.DEFAULT_PORT;
        private static final String DEFAULT_HOST = MqttServerOptions.DEFAULT_HOST;

        private int port = DEFAULT_PORT;
        private String host = DEFAULT_HOST;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }
}
