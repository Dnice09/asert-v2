package tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.dtos;

import java.util.UUID;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.entity.AssessmentVarianceLog;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AssessmentVarianceLogRequestDto {
    private Long id;
    private UUID uuid;

    @NotNull(message = "hotelId is required")
    private Long hotelId;

    @NotNull(message = "formId is required")
    private Long formId;

    @NotNull(message = "formFieldId is required")
    private Long formFieldId;

    @NotNull(message = "varianceType is required")
    private String varianceType;

    @NotNull(message = "status is required")
    private String status;

    @NotNull(message = "scoreDifference is required")
    private Double scoreDifference;

    private LocalDateTime detectedAt;

    private LocalDateTime resolvedAt;

    public AssessmentVarianceLogRequestDto(AssessmentVarianceLog entity) {
        entity.toDao(this);
    }
}
