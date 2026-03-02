package tz.go.mnrt.asert.modules.core.specifications;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class SoftDeleteSpecification<T> implements Specification<T> {

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (root.get("isDeleted") != null) {
            return criteriaBuilder.isFalse(root.get("isDeleted"));
        }
        return null;
    }
}
