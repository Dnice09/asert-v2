package tz.go.mnrt.asert.modules.setup.assessorrejectionreason.repository;

import org.springframework.data.jpa.domain.Specification;
import tz.go.mnrt.asert.modules.setup.assessorrejectionreason.entity.AssessorRejectionReason;

import javax.persistence.criteria.Expression;

public class AssessorRejectionReasonSpecification {
    public static Specification<AssessorRejectionReason> search(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String searchTermLowerCase = searchTerm.toLowerCase();

            Expression<String> code = criteriaBuilder.lower(root.get("code"));
            Expression<String> reason = criteriaBuilder.lower(root.get("reason"));

            return criteriaBuilder.or(
                criteriaBuilder.like(code, "%" + searchTermLowerCase + "%"),
                criteriaBuilder.like(reason, "%" + searchTermLowerCase + "%")
            );
        };
    }

    public static Specification<AssessorRejectionReason> notDeleted() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.or(
                criteriaBuilder.equal(root.get("isDeleted"), false),
                criteriaBuilder.isNull(root.get("isDeleted"))
            );
    }
}
