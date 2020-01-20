package pl.kkurczewski.topic;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import pl.kkurczewski.topic.dto.Record;
import pl.kkurczewski.topic.kafka.client.FetchAllConsumer;
import pl.kkurczewski.topic.kafka.client.GenericProducer;
import pl.kkurczewski.util.CompletableFutures;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static pl.kkurczewski.util.CompletableFutures.completableFuture;

public class TopicService implements AutoCloseable {

    private final AdminClient adminClient;
    private final FetchAllConsumer consumer;
    private final GenericProducer producer;

    public TopicService(String bootstrapServers) {
        var adminClientProperties = new Properties();
        adminClientProperties.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        adminClient = KafkaAdminClient.create(adminClientProperties);
        consumer = new FetchAllConsumer(bootstrapServers);
        producer = new GenericProducer(bootstrapServers);
    }

    public CompletableFuture<Void> createTopic(String topic, int partitions, Integer replicas) {
        return completableFuture(adminClient.createTopics(List.of(new NewTopic(topic, partitions, replicas.shortValue()))).all());
    }

    public CompletableFuture<Void> sendMessages(String topic, Record[] records) {
        return CompletableFuture.allOf(Arrays.stream(records)
                .map(message -> producer.sendMessage(topic, message))
                .map(CompletableFutures::completableFuture)
                .toArray((IntFunction<CompletableFuture<?>[]>) CompletableFuture[]::new));
    }

    public CompletableFuture<Void> deleteTopic(String topic) {
        return completableFuture(adminClient.deleteTopics(List.of(topic)).all());
    }

    public CompletableFuture<Set<String>> getTopics() {
        return completableFuture(adminClient.listTopics().names());
    }

    public List<Record> getRecords(String topic, Duration timeout) {
        return StreamSupport
                .stream(consumer.getMessagesFrom(topic, timeout).spliterator(), false)
                .map(consumerRecord -> new Record(consumerRecord.key(), consumerRecord.value()))
                .collect(Collectors.toList());
    }

    @Override
    public void close() {
        adminClient.close();
        consumer.close();
        producer.close();
    }
}
