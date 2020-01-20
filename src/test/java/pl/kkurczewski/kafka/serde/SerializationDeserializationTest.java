package pl.kkurczewski.kafka.serde;

import com.google.gson.*;
import io.javalin.plugin.json.JavalinJson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.kkurczewski.topic.dto.Record;
import pl.kkurczewski.topic.kafka.serde.KafkaJsonDeserializer;
import pl.kkurczewski.topic.kafka.serde.KafkaJsonSerializer;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializationDeserializationTest {

    private static final Gson gson = new GsonBuilder().create();

    @BeforeAll
    static void setUp() {
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "[{ 'key': 123, 'value': 456 }]",
            "[{ 'key': 123, 'value': '456' }]",
            "[{ 'key': 123, 'value': {'foo': 'bar'} }]",
            "[{ 'key': 123, 'value': '{``foo``:` `bar``}' }]",
            "[{ 'key': '123', 'value': 456 }]",
            "[{ 'key': '123', 'value': '456' }]",
            "[{ 'key': '123', 'value': {'foo': 'bar'} }]",
            "[{ 'key': '123', 'value': '{``foo``: ``bar``}' }]",
            "[{ 'key': {'id': '123'}, 'value': 456 }]",
            "[{ 'key': {'id': '123'}, 'value': '456' }]",
            "[{ 'key': {'id': '123'}, 'value': {'foo': 'bar'} }]",
            "[{ 'key': {'id': '123'}, 'value': '{``foo``: ``bar``}' }]",
            "[{ 'key': '{/id/: /123/}', 'value': 456 }]",
            "[{ 'key': '{/id/: /123/}', 'value': '456' }]",
            "[{ 'key': '{/id/: /123/}', 'value': {'foo': 'bar'} }]",
            "[{ 'key': '{/id/: /123/}', 'value': '{``foo``: ``bar``}' }]",
    })
    void shouldSerializeAndDeserializeRecord(String denormalizedJson) {
        String json = normalized(denormalizedJson);
        Record[] records = gson.fromJson(json, Record[].class);

        assertThat(records).hasSize(1);
        assertThat(records[0].key()).isNotNull();
        assertThat(records[0].value()).isNotNull();

        byte[] serializedKey = new KafkaJsonSerializer().serialize("dummy", records[0].key());
        JsonElement deserializedKey = new KafkaJsonDeserializer().deserialize("dummy", serializedKey);

        assertThat(deserializedKey).isEqualTo(records[0].key());

        byte[] serializedValue = new KafkaJsonSerializer().serialize("dummy", records[0].value());
        JsonElement deserializedValue = new KafkaJsonDeserializer().deserialize("dummy", serializedValue);

        assertThat(deserializedValue).isEqualTo(records[0].value());
    }

    public String normalized(String json) {
        return json
                .replace("'", "\"")
                .replace("``", "\\\"");
    }
}
