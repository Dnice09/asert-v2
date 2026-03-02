package tz.go.mnrt.asert.modules.assessment.assessor.repository;

import org.springframework.data.jpa.domain.Specification;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessmentStatus;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessorHotel;

import java.time.LocalDate;

public class AssessorHotelSpecification {

    public static Specification<AssessorHotel> byAssessor(Long assessorId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("assessorId"), assessorId);
    }

    public static Specification<AssessorHotel> byHotel(Long hotelId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("hotelId"), hotelId);
    }

    public static Specification<AssessorHotel> byDateRange(LocalDate fromDate, LocalDate toDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("dateAssigned"), fromDate, toDate);
    }

    public static Specification<AssessorHotel> byStatus(AssessmentStatus status) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), status));
    }

    public static Specification<AssessorHotel> dataNotCollected() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.or(
                criteriaBuilder.equal(root.get("dataCollected"), false),
                criteriaBuilder.isNull(root.get("dataCollected"))
            );
    }

    public static Specification<AssessorHotel> selfAssessment(boolean selfAssessmentRequest) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("selfAssessmentRequest"), selfAssessmentRequest);
    }

    public static Specification<AssessorHotel> dataCollected() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("dataCollected"), true);
    }
}
