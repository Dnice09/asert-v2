package tz.go.mnrt.asert.modules.setup.documenttype.repository;

import org.springframework.data.jpa.domain.Specification;
import tz.go.mnrt.asert.modules.setup.documenttype.entity.DocumentType;

import javax.persistence.criteria.Expression;

public class DocumentTypeSpecification {
    public static Specification<DocumentType> search(String searchTerm) {
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

    public static Specification<DocumentType> notDeleted() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.or(
                criteriaBuilder.equal(root.get("isDeleted"), false),
                criteriaBuilder.isNull(root.get("isDeleted"))
            );
    }

}
