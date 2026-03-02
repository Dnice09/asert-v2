package tz.go.mnrt.asert.modules.assessment.assessor.repository;

import org.springframework.data.jpa.domain.Specification;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessorStatus;
import tz.go.mnrt.asert.modules.assessment.preference.entity.AssessorPreference;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public class AssessorSpecification {
    public static Specification<Assessor> search(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String searchTermLowerCase = searchTerm.toLowerCase();

            Expression<String> firstName = criteriaBuilder.lower(root.get("firstName"));
            Expression<String> middleName = criteriaBuilder.lower(root.get("middleName"));
            Expression<String> lastName = criteriaBuilder.lower(root.get("lastName"));
            Expression<String> email = criteriaBuilder.lower(root.get("lastName"));

            return criteriaBuilder.or(
                criteriaBuilder.like(firstName, "%" + searchTermLowerCase + "%"),
                criteriaBuilder.like(middleName, "%" + searchTermLowerCase + "%"),
                criteriaBuilder.like(lastName, "%" + searchTermLowerCase + "%"),
                criteriaBuilder.like(email, "%" + searchTermLowerCase + "%")
            );
        };
    }

    public static Specification<Assessor> notDeleted() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.or(
                criteriaBuilder.equal(root.get("isDeleted"), false),
                criteriaBuilder.isNull(root.get("isDeleted"))
            );
    }

    public static Specification<Assessor> newApplication() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.and(
                criteriaBuilder.isNull(root.get("dateRejected")),
                criteriaBuilder.isNull(root.get("dateVerified"))
            );
    }

    public static Specification<Assessor> rejectedApplication() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("dateRejected"));
    }

    public static Specification<Assessor> approvedApplication() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.and(
                criteriaBuilder.isNotNull(root.get("dateVerified")),
                criteriaBuilder.isNull(root.get("dateRejected"))
            );
    }

    public static Specification<Assessor> byStatus(AssessorStatus status) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), status));
    }


    public static Specification<Assessor> hasPreference(PropertyType preference) {
        return (root, query, cb) -> {
            Join<Assessor, AssessorPreference> join = root.join("preferences", JoinType.INNER);
            return cb.equal(join.get("preference"), preference);
        };
    }

    public static Specification<Assessor> byLocation(Long locationId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("locationId"), locationId);
    }
}
