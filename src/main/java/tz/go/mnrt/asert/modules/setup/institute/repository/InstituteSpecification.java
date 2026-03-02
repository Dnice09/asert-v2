package tz.go.mnrt.asert.modules.setup.institute.repository;

import org.springframework.data.jpa.domain.Specification;
import tz.go.mnrt.asert.modules.setup.institute.entity.Institute;

import javax.persistence.criteria.Expression;

public class InstituteSpecification {
    public static Specification<Institute> search(String searchTerm) {
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

    public static Specification<Institute> notDeleted() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.or(
                criteriaBuilder.equal(root.get("isDeleted"), false),
                criteriaBuilder.isNull(root.get("isDeleted"))
            );
    }

    public static Specification<Institute> byCountry(Long countryId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("country").get("id"), countryId);
    }

}
