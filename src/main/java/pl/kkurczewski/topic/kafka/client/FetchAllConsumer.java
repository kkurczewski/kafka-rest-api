package pl.kkurczewski.topic.kafka.client;

import com.google.gson.JsonElement;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import pl.kkurczewski.topic.kafka.serde.KafkaJsonDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

public class FetchAllConsumer implements AutoCloseable {

    private final KafkaConsumer<JsonElement, JsonElement> consumer;

    public FetchAllConsumer(String bootstrapServers) {
        var consumerProps = new Properties();
        consumerProps.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(GROUP_ID_CONFIG, "fetch-all-consumer");
        consumerProps.put(ENABLE_AUTO_COMMIT_CONFIG, false);
        consumerProps.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(KEY_DESERIALIZER_CLASS_CONFIG, KafkaJsonDeserializer.class);
        consumerProps.put(VALUE_DESERIALIZER_CLASS_CONFIG, KafkaJsonDeserializer.class);

        consumer = new KafkaConsumer<>(consumerProps);
    }

    public ConsumerRecords<JsonElement, JsonElement> getMessagesFrom(String topic, Duration timeout) {
        consumer.subscribe(List.of(topic));
        try {
            return consumer.poll(timeout);
        } finally {
            consumer.unsubscribe();
        }
    }

    @Override
    public void close() {
        consumer.close();
    }
}