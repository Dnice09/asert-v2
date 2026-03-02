package tz.go.mnrt.asert.modules.ratingcriteria.dtos;

import java.util.UUID;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;
import tz.go.mnrt.asert.modules.ratingcriteria.entity.RatingCriteria;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RatingCriteriaRequestDto {
    private Long id;
    private UUID uuid;

    @NotNull(message = "Property type is required")
    private PropertyType propertyType;

    @NotNull(message = "Star level is required")
    @Min(value = 1, message = "Star level must be at least 1")
    @Max(value = 5, message = "Star level cannot exceed 5")
    private Integer starLevel;

    @NotNull(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Minimum score is required")
    @Min(value = 0, message = "Minimum score cannot be negative")
    private Double minScore;

    @NotNull(message = "Maximum score is required")
    @Min(value = 0, message = "Maximum score cannot be negative")
    private Double maxScore;

    @NotNull(message = "Total possible score is required")
    @Min(value = 0, message = "Total possible score cannot be negative")
    private Double totalPossibleScore;

    @Size(max = 1000, message = "Criteria description cannot exceed 1000 characters")
    private String criteriaDescription;

    @Min(value = 0, message = "Percentage required cannot be negative")
    @Max(value = 100, message = "Percentage required cannot exceed 100")
    private Double percentageRequired;

    // Legacy field for backward compatibility
    @Deprecated
    @Size(max = 50, message = "Rating level cannot exceed 50 characters")
    private String ratingLevel;

    public RatingCriteriaRequestDto(RatingCriteria entity) {
        entity.toDao(this);
    }
}
