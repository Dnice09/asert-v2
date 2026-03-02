package tz.go.mnrt.asert.modules.selfassessment.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.selfassessment.entity.SelfAssessment;
import tz.go.mnrt.asert.modules.selfassessment.entity.SelfAssessmentStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SelfAssessmentSummaryDto {

    private UUID uuid;
    private String hotelName;
    private String formName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submittedAt;

    private Double percentage;
    private String estimatedRating;
    private SelfAssessmentStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public SelfAssessmentSummaryDto(SelfAssessment entity) {
        this.uuid = entity.getUuid();
        this.hotelName = entity.getHotel() != null ? entity.getHotel().getName() : null;
        this.formName = entity.getForm() != null ? entity.getForm().getName() : null;
        this.submittedAt = entity.getSubmittedAt();
        this.percentage = entity.getPercentage();
        this.estimatedRating = entity.getEstimatedRating();
        this.status = entity.getStatus();
        this.createdAt = entity.getCreatedAt();
    }
}
