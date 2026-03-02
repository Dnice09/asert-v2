package tz.go.mnrt.asert.modules.assessment.preference.repository;

import org.springframework.data.jpa.domain.Specification;
import tz.go.mnrt.asert.modules.assessment.preference.entity.AssessorPreference;

public class AssessorPreferenceSpecification {
    public static Specification<AssessorPreference> byAssessor(Long assessorId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("assessorId"), assessorId);
    }
}
