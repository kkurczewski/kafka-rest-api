package pl.kkurczewski.topic.dto;

import com.google.gson.JsonElement;

import java.util.Objects;

public class Record {

    private final JsonElement key;
    private final JsonElement value;

    public Record(JsonElement key, JsonElement value) {
        this.key = key;
        this.value = value;
    }

    public JsonElement key() {
        return key;
    }

    public JsonElement value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return Objects.equals(key, record.key) && value.equals(record.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
