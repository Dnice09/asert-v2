package tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.dtos;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;

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
public class HotelAssessmentApprovalResponseDto {
  private Long id;
  private UUID uuid;

  @NotNull(message = "Created Date is required")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDateTime createdDate;

  private Long hotelId;

  private Long formId;

  private Double finalTotalScore;

  private Double finalMaxScore;

  private Double finalPercentage;

  private String finalRating;

  private Boolean hasUnresolvedVariances;

  private Integer varianceResolutionCount;

  private LocalDateTime allVariancesResolvedAt;

  private String status;

  private LocalDateTime submittedToDtAt;

  private LocalDateTime dtApprovalDate;

  private String dtRejectionReason;

  private String dtComments;

  public HotelAssessmentApprovalResponseDto(HotelAssessmentApproval entity) {
    entity.toDao(this);
  }
}
