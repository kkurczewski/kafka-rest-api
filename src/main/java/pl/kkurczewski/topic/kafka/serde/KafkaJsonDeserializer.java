package pl.kkurczewski.topic.kafka.serde;

import com.google.gson.JsonElement;
import io.javalin.plugin.json.JavalinJson;
import org.apache.kafka.common.serialization.Deserializer;

public class KafkaJsonDeserializer implements Deserializer<JsonElement> {

    @Override
    public JsonElement deserialize(String topic, byte[] data) {
        return JavalinJson.fromJson(new String(data), JsonElement.class);
    }
}
