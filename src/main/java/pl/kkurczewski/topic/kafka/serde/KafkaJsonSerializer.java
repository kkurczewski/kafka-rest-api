package pl.kkurczewski.topic.kafka.serde;

import com.google.gson.JsonElement;
import io.javalin.plugin.json.JavalinJson;
import org.apache.kafka.common.serialization.Serializer;

public class KafkaJsonSerializer implements Serializer<JsonElement> {

    @Override
    public byte[] serialize(String topic, JsonElement data) {
        return JavalinJson.toJson(data).getBytes();
    }
}
