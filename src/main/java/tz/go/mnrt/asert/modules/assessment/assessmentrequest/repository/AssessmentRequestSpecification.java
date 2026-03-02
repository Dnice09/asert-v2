package tz.go.mnrt.asert.modules.assessment.assessmentrequest.repository;

import org.springframework.data.jpa.domain.Specification;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity.AssessmentRequest;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity.AssessmentRequestStatus;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssessmentRequestSpecification {

    public static Specification<AssessmentRequest> withFilters(Map<String, String> filters) {
        return (Root<AssessmentRequest> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Hotel name filter
            if (filters.containsKey("hotelName") && !filters.get("hotelName").isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("hotel").get("name")),
                    "%" + filters.get("hotelName").toLowerCase() + "%"
                ));
            }

            // Hotel property type filter
            if (filters.containsKey("propertyType") && !filters.get("propertyType").isEmpty()) {
                try {
                    predicates.add(criteriaBuilder.equal(
                        root.get("hotel").get("propertyType"),
                        filters.get("propertyType")
                    ));
                } catch (Exception e) {
                    // Invalid property type, ignore filter
                }
            }

            // Status filter
            if (filters.containsKey("status") && !filters.get("status").isEmpty()) {
                try {
                    AssessmentRequestStatus status = AssessmentRequestStatus.valueOf(filters.get("status"));
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                } catch (IllegalArgumentException e) {
                    // Invalid status, ignore filter
                }
            }

            // Email filter
            if (filters.containsKey("email") && !filters.get("email").isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")),
                    "%" + filters.get("email").toLowerCase() + "%"
                ));
            }

            // Contact person filter
            if (filters.containsKey("contactPerson") && !filters.get("contactPerson").isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("contactPerson")),
                    "%" + filters.get("contactPerson").toLowerCase() + "%"
                ));
            }

            // Submitted by user filter
            if (filters.containsKey("submittedBy") && !filters.get("submittedBy").isEmpty()) {
                try {
                    Long userId = Long.parseLong(filters.get("submittedBy"));
                    predicates.add(criteriaBuilder.equal(root.get("submittedByUser").get("id"), userId));
                } catch (NumberFormatException e) {
                    // Invalid user ID, ignore filter
                }
            }

            // Date range filters
            if (filters.containsKey("submittedFrom") && !filters.get("submittedFrom").isEmpty()) {
                try {
                    LocalDateTime fromDate = LocalDateTime.parse(filters.get("submittedFrom") + "T00:00:00");
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("submittedAt"), fromDate));
                } catch (Exception e) {
                    // Invalid date format, ignore filter
                }
            }

            if (filters.containsKey("submittedTo") && !filters.get("submittedTo").isEmpty()) {
                try {
                    LocalDateTime toDate = LocalDateTime.parse(filters.get("submittedTo") + "T23:59:59");
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("submittedAt"), toDate));
                } catch (Exception e) {
                    // Invalid date format, ignore filter
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<AssessmentRequest> hasStatus(AssessmentRequestStatus status) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<AssessmentRequest> hasSubmittedByUser(Long userId) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("submittedByUser").get("id"), userId);
    }

    public static Specification<AssessmentRequest> hasHotelNameContaining(String hotelName) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("hotel").get("name")),
                "%" + hotelName.toLowerCase() + "%"
            );
    }

    public static Specification<AssessmentRequest> hasHotelPropertyType(String propertyType) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("hotel").get("propertyType"), propertyType);
    }

    public static Specification<AssessmentRequest> submittedAfter(LocalDateTime dateTime) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.greaterThanOrEqualTo(root.get("submittedAt"), dateTime);
    }

    public static Specification<AssessmentRequest> submittedBefore(LocalDateTime dateTime) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.lessThanOrEqualTo(root.get("submittedAt"), dateTime);
    }
}