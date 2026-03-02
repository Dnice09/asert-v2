package tz.go.mnrt.asert.configs;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class RelationshipIgnoringSerializer extends StdSerializer<Object> {

    private String[] includes;

    public RelationshipIgnoringSerializer() {
        this(null);
    }

    public RelationshipIgnoringSerializer(Class<Object> t) {
        super(t);
    }

    public static RelationshipIgnoringSerializer withInclude(String[] includes) {
        RelationshipIgnoringSerializer s = new RelationshipIgnoringSerializer();
        s.includes = includes;
        return s;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        // Get all fields of the object and exclude the ones with relationship types
        List<Field> fields = Arrays.asList(value.getClass().getDeclaredFields()).stream()
                .filter(field -> !isRelationship(field))
                .collect(Collectors.toList());

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                gen.writeObjectField(field.getName(), field.get(value));
            } catch (IllegalAccessException e) {
                // Ignore errors
            }
        }

        gen.writeEndObject();
    }

    private boolean isRelationship(Field field) {
        // Check if the field has any of the common relationship annotations
        List<String> annotations = Arrays.stream(field.getDeclaredAnnotations())
                .map(a -> a.annotationType().getSimpleName())
                .collect(Collectors.toList());
        if (includes != null && Arrays.asList(includes).contains(field.getName())) {
            return false;
        }

        return (annotations.contains("ManyToOne")
                || annotations.contains("ManyToMany")
                || annotations.contains("OneToMany")
                || annotations.contains("OneToOne")
                || annotations.contains("TrackIgnore"));
    }
}
