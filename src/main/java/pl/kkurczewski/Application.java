package pl.kkurczewski;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.plugin.json.JavalinJson;
import pl.kkurczewski.topic.TopicService;
import pl.kkurczewski.topic.rest.TopicController;

import static io.javalin.apibuilder.ApiBuilder.*;
import static org.eclipse.jetty.http.MimeTypes.Type.APPLICATION_JSON;
import static pl.kkurczewski.topic.rest.TopicController.TOPIC_NAME;

public class Application {

    private static final String PORT_ENV = "PORT";
    private static final String PORT_DEFAULT = "9093";
    private static final String BOOTSTRAP_SERVER_ENV = "BOOTSTRAP_SERVER";
    private static final String BOOTSTRAP_SERVER_DEFAULT = "localhost:9092";

    public static void main(String[] args) {
        int port = Integer.parseInt(getEnvOrDefault(PORT_ENV, PORT_DEFAULT));
        String bootstrapServers = getEnvOrDefault(BOOTSTRAP_SERVER_ENV, BOOTSTRAP_SERVER_DEFAULT);

        var topicService = new TopicService(bootstrapServers);
        var topicController = new TopicController(topicService);

        Gson gson = new GsonBuilder().create();
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);

        Javalin.create(Application::config)
                .routes(() -> routing(topicController))
                .error(400, (ctx) -> ctx.result("Couldn't deserialize body" + tryGiveHint(ctx)))
                .start(port);
    }

    private static void config(JavalinConfig config) {
        config.asyncRequestTimeout = 5000L;
        config.defaultContentType = APPLICATION_JSON.asString();
    }

    private static void routing(TopicController topicController) {
        path("/topics", () -> {
            get(topicController::getTopics);
            path(TOPIC_NAME, () -> {
                post(topicController::addTopic);
                delete(topicController::deleteTopic);
                path("/messages", () -> {
                    get(topicController::getMessages);
                    post(topicController::addMessages);
                });
            });
        });
    }

    private static String tryGiveHint(Context ctx) {
        return ctx.body().trim().startsWith("{") ? ", expected json array, got json object" : "";
    }

    private static String getEnvOrDefault(String envKey, String orElse) {
        String envValue = System.getenv(envKey);
        return (envValue != null) ? envValue : orElse;
    }
}
