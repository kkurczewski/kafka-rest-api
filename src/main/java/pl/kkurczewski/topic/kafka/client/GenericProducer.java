package pl.kkurczewski.topic.kafka.client;

import com.google.gson.JsonElement;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import pl.kkurczewski.topic.dto.Record;
import pl.kkurczewski.topic.kafka.serde.KafkaJsonSerializer;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static org.apache.kafka.clients.producer.ProducerConfig.*;
import static pl.kkurczewski.util.CompletableFutures.completableFuture;

public class GenericProducer implements AutoCloseable {

    private final KafkaProducer<JsonElement, JsonElement> producer;

    public GenericProducer(String bootstrapServers) {
        var producerProps = new Properties();
        producerProps.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ACKS_CONFIG, "all");
        producerProps.put(KEY_SERIALIZER_CLASS_CONFIG, KafkaJsonSerializer.class);
        producerProps.put(VALUE_SERIALIZER_CLASS_CONFIG, KafkaJsonSerializer.class);

        producer = new KafkaProducer<>(producerProps);
    }

    public CompletableFuture<RecordMetadata> sendMessage(String topic, Record record) {
        return completableFuture(producer.send(new ProducerRecord<>(topic, record.key(), record.value())));
    }

    @Override
    public void close() {
        producer.close();
    }
}