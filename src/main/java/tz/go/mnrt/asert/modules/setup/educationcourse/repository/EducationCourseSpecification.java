package tz.go.mnrt.asert.modules.setup.educationcourse.repository;

import org.springframework.data.jpa.domain.Specification;
import tz.go.mnrt.asert.modules.setup.educationcourse.entity.EducationCourse;

import javax.persistence.criteria.Expression;

public class EducationCourseSpecification {
    public static Specification<EducationCourse> search(String searchTerm) {
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

    public static Specification<EducationCourse> notDeleted() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.or(
                criteriaBuilder.equal(root.get("isDeleted"), false),
                criteriaBuilder.isNull(root.get("isDeleted"))
            );
    }

    public static Specification<EducationCourse> byEducationLevel(Long educationLevelId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("educationLevel").get("id"), educationLevelId);
    }

}
