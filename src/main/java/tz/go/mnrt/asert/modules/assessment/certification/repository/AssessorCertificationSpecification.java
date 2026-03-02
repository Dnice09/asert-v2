package tz.go.mnrt.asert.modules.assessment.certification.repository;

import org.springframework.data.jpa.domain.Specification;
import tz.go.mnrt.asert.modules.assessment.certification.entity.AssessorCertification;

import javax.persistence.criteria.Expression;

public class AssessorCertificationSpecification {
    public static Specification<AssessorCertification> search(String searchTerm) {
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

    public static Specification<AssessorCertification> notDeleted() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.or(
                criteriaBuilder.equal(root.get("isDeleted"), false),
                criteriaBuilder.isNull(root.get("isDeleted"))
            );
    }

    public static Specification<AssessorCertification> byAssessor(Long assessorId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("assessorId"), assessorId);
    }
}
