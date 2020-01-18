package pl.kkurczewski.parser;

import com.google.gson.*;
import io.javalin.plugin.json.JavalinJson;
import org.junit.jupiter.api.Test;
import pl.kkurczewski.topic.dto.Record;
import pl.kkurczewski.topic.kafka.serde.KafkaJsonDeserializer;
import pl.kkurczewski.topic.kafka.serde.KafkaJsonSerializer;

import static org.assertj.core.api.Assertions.assertThat;

public class ParsingTest {

    // TODO review

    private final Gson gson = new GsonBuilder().create();

    @Test
    void shouldParseJsonAsValue() {
        String json = normalized("[{'key':'1', 'value':{'foo':'bar'}}]");
        Record[] records = gson.fromJson(json, Record[].class);

        assertThat(records).hasSize(1);
        assertThat(records[0].value()).isInstanceOf(JsonObject.class);
        assertThat(records[0].value().getAsJsonObject().get("foo").getAsString()).isEqualTo("bar");
    }

    @Test
    void shouldParseJsonStringAsValue() {
        String json = normalized("[{'key':'1', 'value':'{/foo/:/bar/}'}]");
        Record[] records = gson.fromJson(json, Record[].class);

        assertThat(records).hasSize(1);
        assertThat(records[0].value()).isInstanceOf(JsonPrimitive.class);
        assertThat(records[0].value().getAsString()).isEqualTo(normalized("{'foo':'bar'}"));
    }

    @Test
    void shouldParseIntAsValue() {
        String json = normalized("[{'key':'1', 'value':123}]");
        Record[] records = gson.fromJson(json, Record[].class);

        assertThat(records).hasSize(1);
        assertThat(records[0].value()).isInstanceOf(JsonPrimitive.class);
        assertThat(records[0].value().getAsInt()).isEqualTo(123);
    }

    @Test
    void shouldParseStringAsValue() {
        String json = normalized("[{'key':'1', 'value':'foo'}]");
        Record[] records = gson.fromJson(json, Record[].class);

        assertThat(records).hasSize(1);
        assertThat(records[0].value()).isInstanceOf(JsonPrimitive.class);
        assertThat(records[0].value().getAsString()).isEqualTo("foo");
    }

    @Test
    void shouldSerializeRecordValue() {
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);

        String json = normalized("[{'key':'1', 'value':{'foo':'bar'}}]");
        Record[] records = gson.fromJson(json, Record[].class);

        byte[] serialized = new KafkaJsonSerializer().serialize("dummy", records[0].value());
        JsonElement deserialized = new KafkaJsonDeserializer().deserialize("dummy", serialized);

        assertThat(deserialized).isEqualTo(records[0].value());
    }

    @Test
    void shouldSerializeRecordValue_2() {
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);

        String json = normalized("[{'key':'1', 'value':'foo'}]");
        Record[] records = gson.fromJson(json, Record[].class);

        byte[] serialized = new KafkaJsonSerializer().serialize("dummy", records[0].value());
        JsonElement deserialized = new KafkaJsonDeserializer().deserialize("dummy", serialized);

        assertThat(deserialized.getAsString()).isEqualTo("foo");
    }

    @Test
    void shouldSerializeRecordValue_3() {
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);

        String json = normalized("[{'key':'1', 'value':1}]");
        Record[] records = gson.fromJson(json, Record[].class);

        byte[] serialized = new KafkaJsonSerializer().serialize("dummy", records[0].value());
        JsonElement deserialized = new KafkaJsonDeserializer().deserialize("dummy", serialized);

        assertThat(deserialized.getAsInt()).isEqualTo(1);
    }

    public String normalized(String json) {
        return json
                .replace("'", "\"")
                .replace("/", "\\\"");
    }
}
