package tz.go.mnrt.asert.modules.core.services;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;

@Slf4j
public abstract class SimpleSearchService<T extends BaseModel> {

    public Specification<T> createSpecification(
            Class<T> entity, Map<String, String> search, Boolean isActive) {
        Specification<T> specification = Specification.where(null);
        boolean isOr = search.containsKey("searchType") && search.get("searchType").equals("or");
        List<String> allowedProps = Arrays.stream(entity.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());

        if (!search.isEmpty()) {
            for (String key : search.keySet()) {
                // Skip control parameters
                if (key.equals("searchType")) {
                    continue;
                }

                String searchValue = search.get(key);
                if (searchValue.isEmpty()) {
                    continue;
                }

                // Handle nested properties
                if (key.contains(".")) {
                    try {
                        String[] pathParts = key.split("\\.");
                        // Validate only the first level property
                        if (allowedProps.contains(pathParts[0])) {
                            specification = addNestedPropertyPredicate(specification, key, searchValue, isOr);
                        }
                    } catch (Exception e) {
                        log.error("Error processing nested property {}: {}", key, e.getMessage());
                    }
                }
                // Handle direct properties
                else if (allowedProps.contains(key)) {
                    try {
                        Field field = entity.getDeclaredField(key);
                        specification = addDirectPropertyPredicate(specification, field, key, searchValue, isOr);
                    } catch (Exception e) {
                        log.error("Error processing property {}: {}", key, e.getMessage());
                    }
                }
            }
        }

        Specification<T> all = isActive ? getActiveEntries() : getDeletedEntries();
        return all.and(specification);
    }

    /**
     * Adds a predicate for direct (non-nested) properties
     */
    private Specification<T> addDirectPropertyPredicate(
            Specification<T> specification, Field field, String key, String value, boolean isOr) {

        Specification<T> predicate = (root, query, builder) -> {
            if (field.getType() == Long.class) {
                return builder.equal(root.get(key), Long.parseLong(value));
            } else if (field.getType() == Integer.class) {
                return builder.equal(root.get(key), Integer.parseInt(value));
            } else if (field.getType() == Boolean.class) {
                Boolean val = value.equalsIgnoreCase("true");
                return builder.equal(root.get(key), val);
            } else {
                return builder.like(
                        builder.lower(root.get(key)),
                        "%" + value.toLowerCase() + "%");
            }
        };

        return isOr ? specification.or(predicate) : specification.and(predicate);
    }

    private Specification<T> addNestedPropertyPredicate(
            Specification<T> specification, String nestedPath, String value, boolean isOr) {

        Specification<T> predicate = (root, query, builder) -> {
            Path<?> path = getNestedPath(root, nestedPath);

            // Determine the type of the final property in the path
            Class<?> type = path.getJavaType();

            if (type == Long.class) {
                return builder.equal(path, Long.parseLong(value));
            } else if (type == Integer.class) {
                return builder.equal(path, Integer.parseInt(value));
            } else if (type == Boolean.class) {
                Boolean val = value.equalsIgnoreCase("true");
                return builder.equal(path, val);
            } else {
                // For String and other types, use like
                return builder.like(
                        builder.lower(path.as(String.class)),
                        "%" + value.toLowerCase() + "%");
            }
        };

        return isOr ? specification.or(predicate) : specification.and(predicate);
    }

    /**
     * Gets a Path for a nested property expressed with dot notation
     */
    private <X> Path<X> getNestedPath(Root<T> root, String attributeName) {
        String[] parts = attributeName.split("\\.");
        Path<X> path = root.get(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            path = path.get(parts[i]);
        }

        return path;
    }

    /**
     * Since we don't want others to change their implementations of this method
     * we need to overload this and pass true as the default value for isActive
     */
    public Specification<T> createSpecification(Class<T> entity, Map<String, String> search) {
        return createSpecification(entity, search, true);
    }

    public Specification<T> getActiveEntries() {
        return (root, query, builder) -> builder.equal(root.get("isDeleted"), false);
    }

    public Specification<T> getDeletedEntries() {
        return (root, query, builder) -> builder.equal(root.get("isDeleted"), true);
    }
}
