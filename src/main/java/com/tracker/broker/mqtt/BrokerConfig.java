package com.tracker.broker.mqtt;

import io.vertx.mqtt.MqttServerOptions;

/**
 * Represents MQTT Broker configuration.
 */
public class BrokerConfig {

    private BrokerConfigRoot broker;

    /**
     * Set top-level configuration.
     *
     * @param broker top-level configuration
     */
    public void setBroker(BrokerConfigRoot broker) {
        this.broker = broker;
    }

    /**
     * Get host MQTT broker is running on.
     *
     * @return host
     */
    public String getHost() {
        return broker.getHost();
    }

    /**
     * Get port MQTT broker is listening on.
     *
     * @return port
     */
    public int getPort() {
        return broker.getPort();
    }

    /**
     * Represents top-level MQTT broker configuration.
     */
    public static class BrokerConfigRoot {

        private static final int DEFAULT_PORT = MqttServerOptions.DEFAULT_PORT;
        private static final String DEFAULT_HOST = MqttServerOptions.DEFAULT_HOST;

        private int port = DEFAULT_PORT;
        private String host = DEFAULT_HOST;

        /**
         * Get host MQTT broker is running on.
         *
         * @return host
         */
        public String getHost() {
            return host;
        }

        /**
         * Set MQTT broker's host.
         *
         * @param host host
         */
        public void setHost(String host) {
            this.host = host;
        }

        /**
         * Get port MQTT broker is listening on.
         *
         * @return port
         */
        public int getPort() {
            return port;
        }

        /**
         * Set MQTT broker's port.
         *
         * @param port port
         */
        public void setPort(int port) {
            this.port = port;
        }
    }
}
