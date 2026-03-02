package tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.dtos;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.entity.AssessorVarianceNotification;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AssessorVarianceNotificationResponseDto {
  private Long id;
  private UUID uuid;

  @NotNull(message = "Created Date is required")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDateTime createdDate;

  private Long assessmentVarianceLogId;

  private Long assessorId;

  private String notificationType;

  private String message;

  private Boolean isRead;

  private LocalDateTime readAt;

  public AssessorVarianceNotificationResponseDto(AssessorVarianceNotification entity) {
    entity.toDao(this);
  }
}
