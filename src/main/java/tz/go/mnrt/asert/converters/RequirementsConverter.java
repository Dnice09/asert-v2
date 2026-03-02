package tz.go.mnrt.asert.converters;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter
public class RequirementsConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> requirements) {
        if (requirements == null) {
            return null; // Handle null values
        }
        try {
            // Extract only the "staffs" field
            Object staffs = requirements.get("staffs");
            Map<String, Object> filteredRequirements = staffs != null
                    ? Map.of("staffs", staffs)
                    : Collections.emptyMap();

            return objectMapper.writeValueAsString(filteredRequirements);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing requirements to JSON", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> convertToEntityAttribute(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyMap(); // Handle null or empty JSON
        }
        try {
            return objectMapper.readValue(json, Map.class); // Deserialize JSON into a Map
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing JSON to requirements", e);
        }
    }
}
