package tz.go.mnrt.asert.modules.ratingcriteria.dtos;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;

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
public class RatingCriteriaResponseDto {
    private Long id;
    private UUID uuid;

    @NotNull(message = "Created Date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;

    private PropertyType propertyType;
    private Integer starLevel;
    private String name;
    private Double minScore;
    private Double maxScore;
    private Double totalPossibleScore;
    private String criteriaDescription;
    private Double percentageRequired;

    // Legacy field for backward compatibility
    @Deprecated
    private String ratingLevel;

    public RatingCriteriaResponseDto(RatingCriteria entity) {
        entity.toDao(this);
    }
}
