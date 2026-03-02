package tz.go.mnrt.asert.modules.ratingcriteria.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rating_criteria", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"property_type", "star_level"})
})
public class RatingCriteria extends BaseModel {

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false)
    private PropertyType propertyType;

    @Column(name = "star_level", nullable = false)
    private Integer starLevel;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "min_score", nullable = false)
    private Double minScore;

    @Column(name = "max_score", nullable = false)
    private Double maxScore;

    @Column(name = "total_possible_score", nullable = false)
    private Double totalPossibleScore;

    @Column(name = "criteria_description", columnDefinition = "TEXT")
    private String criteriaDescription;

    @Column(name = "percentage_required")
    private Double percentageRequired;
}
