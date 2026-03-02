package tz.go.mnrt.asert.converters;

import java.io.IOException;
import java.util.Map;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter
public class ProposedNameConverter implements AttributeConverter<Map<String, Object>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> data) {
        if (data == null || !data.containsKey("proposedName")) {
            return null;
        }
        try {
            // Convert the entire map to JSON
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing data to JSON", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> convertToEntityAttribute(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            // Convert JSON back to Map
            return objectMapper.readValue(json, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing JSON to Map", e);
        }
    }
}
