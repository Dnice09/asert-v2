package tz.go.mnrt.asert.helpers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSpecificationBuilder<T> {
  private T spec;

  public Specification<T> makeSpecification(Map<String, String> search) {
    Specification<T> specification = Specification.where(null);

    Field[] allFields = spec.getClass().getDeclaredFields();

    List<String> allowedProps =
        Arrays.stream(allFields).map(field -> field.getName()).collect(Collectors.toList());
    if (!search.isEmpty()) {
      for (String key : search.keySet()) {
        if (allowedProps.contains(key) && !search.get(key).isEmpty()) {
          specification =
              specification.and(
                  (root, query, builder) ->
                      builder.like(
                          builder.lower(root.get(key)), "%" + search.get(key).toLowerCase() + "%"));
        }
      }
    }

    return specification;
  }
}
