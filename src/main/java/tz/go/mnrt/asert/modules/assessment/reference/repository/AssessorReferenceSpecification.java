package tz.go.mnrt.asert.modules.assessment.reference.repository;

import org.springframework.data.jpa.domain.Specification;
import tz.go.mnrt.asert.modules.assessment.reference.entity.AssessorReference;

import javax.persistence.criteria.Expression;

public class AssessorReferenceSpecification {
    public static Specification<AssessorReference> search(String searchTerm) {
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

    public static Specification<AssessorReference> notDeleted() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.or(
                criteriaBuilder.equal(root.get("isDeleted"), false),
                criteriaBuilder.isNull(root.get("isDeleted"))
            );
    }

    public static Specification<AssessorReference> byAssessor(Long assessorId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("assessorId"), assessorId);
    }
}
