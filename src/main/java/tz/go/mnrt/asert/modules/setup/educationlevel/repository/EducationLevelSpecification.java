package tz.go.mnrt.asert.modules.setup.educationlevel.repository;

import org.springframework.data.jpa.domain.Specification;
import tz.go.mnrt.asert.modules.setup.educationlevel.entity.EducationLevel;

import javax.persistence.criteria.Expression;

public class EducationLevelSpecification {
    public static Specification<EducationLevel> search(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String searchTermLowerCase = searchTerm.toLowerCase();

            Expression<String> titleField = criteriaBuilder.lower(root.get("name"));

            return criteriaBuilder.or(
                criteriaBuilder.like(titleField, "%" + searchTermLowerCase + "%")
            );
        };
    }

    public static Specification<EducationLevel> notDeleted() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.or(
                criteriaBuilder.equal(root.get("isDeleted"), false),
                criteriaBuilder.isNull(root.get("isDeleted"))
            );
    }

}
