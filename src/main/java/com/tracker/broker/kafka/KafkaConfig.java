package com.tracker.broker.kafka;

public class KafkaConfig {

    private KafkaConfigRoot kafka;

    public void setKafka(KafkaConfigRoot kafka) {
        this.kafka = kafka;
    }

    public String getHost() {
        return kafka.getHost();
    }

    public int getPort() {
        return kafka.getPort();
    }

    public static class KafkaConfigRoot {
        private static final int DEFAULT_PORT = 9092;
        private static final String DEFAULT_HOST = "0.0.0.0";

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
