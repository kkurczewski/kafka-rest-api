package pl.kkurczewski.topic.rest;

import io.javalin.http.Context;
import pl.kkurczewski.topic.TopicService;
import pl.kkurczewski.topic.dto.Record;

import java.time.Duration;

public class TopicController {

    public static final String TOPIC_NAME = ":name";

    static final String OPT_POLL_TIMEOUT_MS = "timeout";
    static final String DEFAULT_POLL_TIMEOUT = "10000";

    static final String PARTITIONS_NUMBER = "partitions";
    static final String DEFAULT_PARTITIONS_NUMBER = "1";

    static final String REPLICAS_NUMBER = "replicas";
    static final String DEFAULT_REPLICAS_NUMBER = "1";

    private TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    public void addTopic(Context context) {
        var result = topicService.createTopic(
                context.pathParam(TOPIC_NAME),
                context.queryParam(PARTITIONS_NUMBER, Integer.class, DEFAULT_PARTITIONS_NUMBER).get(),
                context.queryParam(REPLICAS_NUMBER, Short.class, DEFAULT_REPLICAS_NUMBER).get()
        );
        context.result(result);
    }

    public void deleteTopic(Context context) {
        var result = topicService.deleteTopic(context.pathParam(TOPIC_NAME));
        context.result(result);
    }

    public void addMessages(Context context) {
        var result = topicService.sendMessages(
                context.pathParam(TOPIC_NAME),
                context.bodyAsClass(Record[].class)
        );
        context.result(result);
    }

    public void getTopics(Context context) {
        context.json(topicService.getTopics());
    }

    public void getMessages(Context context) {
        context.json(topicService.getRecords(
                context.pathParam(TOPIC_NAME),
                Duration.ofMillis(context.queryParam(OPT_POLL_TIMEOUT_MS, Long.class, DEFAULT_POLL_TIMEOUT).get())
        ));
    }
}
