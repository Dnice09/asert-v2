package tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.dtos;

import java.util.UUID;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.entity.HotelAssessmentApproval;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HotelAssessmentApprovalRequestDto {
  private Long id;
  private UUID uuid;

  @NotNull(message = "hotelId is required")
  private Long hotelId;

  @NotNull(message = "formId is required")
  private Long formId;

  private Double finalTotalScore;

  private Double finalMaxScore;

  private Double finalPercentage;

  private String finalRating;

  private Boolean hasUnresolvedVariances;

  private Integer varianceResolutionCount;

  private LocalDateTime allVariancesResolvedAt;

  @NotNull(message = "status is required")
  private String status;

  private LocalDateTime submittedToDtAt;

  private LocalDateTime dtApprovalDate;

  private String dtRejectionReason;

  private String dtComments;

  public HotelAssessmentApprovalRequestDto(HotelAssessmentApproval entity) {
    entity.toDao(this);
  }
}
