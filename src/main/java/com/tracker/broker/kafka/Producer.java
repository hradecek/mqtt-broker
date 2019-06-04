package com.tracker.broker.kafka;

import io.reactivex.Single;
import io.vertx.kafka.client.producer.RecordMetadata;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.kafka.client.producer.KafkaProducer;
import io.vertx.reactivex.kafka.client.producer.KafkaProducerRecord;

import java.util.HashMap;
import java.util.Map;

public class Producer {

    private final KafkaProducer<String, String> producer;

    public Producer(final Vertx vertx, final KafkaConfig config) {
        this.producer = KafkaProducer.create(vertx, configToMap(config));
    }

    public Single<RecordMetadata> doSendMessage(final String topic, final String message) {
        return producer.rxWrite(KafkaProducerRecord.create(topic, message))
                       .doAfterSuccess(r -> System.out.println(String.format("Message %s written to %s", message, r.getTopic())));
    }

    private static Map<String, String> configToMap(final KafkaConfig config) {
        final Map<String, String> mapConfig = new HashMap<>();
        mapConfig.put("bootstrap.servers", addressWithPort(config.getHost(), config.getPort()));
        mapConfig.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        mapConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        mapConfig.put("acks", "1");

        System.out.println(mapConfig);
        return mapConfig;
    }

    private static String addressWithPort(final String address, final int port) {
        return String.format("%s:%d", address, port);
    }
}
