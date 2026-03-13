package aog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonString {

    private final ObjectMapper object_mapper;
    private final TypeReference<Map<String, Object>> type_reference;

    public JsonString() {
        object_mapper = new ObjectMapper();
        type_reference = new TypeReference<>() {};
    }

    public JsonNode getJsonNode(String json_string) throws JsonProcessingException {
        return object_mapper.readTree(json_string);
    }

    public Map<String, Object> getJson(String json_string) throws JsonProcessingException {
        return object_mapper.readValue(json_string, type_reference);
    }
}
