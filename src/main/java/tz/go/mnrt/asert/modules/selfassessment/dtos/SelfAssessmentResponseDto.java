package tz.go.mnrt.asert.modules.selfassessment.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.selfassessment.entity.SelfAssessment;
import tz.go.mnrt.asert.modules.selfassessment.entity.SelfAssessmentStatus;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SelfAssessmentResponseDto {

    private Long id;
    private UUID uuid;
    private UUID hotelUuid;
    private String hotelName;
    private UUID formUuid;
    private String formName;
    private String submittedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submittedAt;

    private Double totalScore;
    private Double maxPossibleScore;
    private Double percentage;
    private String estimatedRating;
    private SelfAssessmentStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public SelfAssessmentResponseDto(SelfAssessment entity) {
        this.id = entity.getId();
        this.uuid = entity.getUuid();
        this.hotelUuid = entity.getHotel() != null ? entity.getHotel().getUuid() : null;
        this.hotelName = entity.getHotel() != null ? entity.getHotel().getName() : null;
        this.formUuid = entity.getForm() != null ? entity.getForm().getUuid() : null;
        this.formName = entity.getForm() != null ? entity.getForm().getName() : null;
        this.submittedBy = entity.getSubmittedBy();
        this.submittedAt = entity.getSubmittedAt();
        this.totalScore = entity.getTotalScore();
        this.maxPossibleScore = entity.getMaxPossibleScore();
        this.percentage = entity.getPercentage();
        this.estimatedRating = entity.getEstimatedRating();
        this.status = entity.getStatus();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }
}
