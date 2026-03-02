package tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.dtos;

import java.util.UUID;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
public class AssessorVarianceNotificationRequestDto {
  private Long id;
  private UUID uuid;

  @NotNull(message = "assessmentVarianceLogId is required")
  private Long assessmentVarianceLogId;

  @NotNull(message = "assessorId is required")
  private Long assessorId;

  @NotNull(message = "notificationType is required")
  private String notificationType;

  private String message;

  private Boolean isRead;

  private LocalDateTime readAt;

  public AssessorVarianceNotificationRequestDto(AssessorVarianceNotification entity) {
    entity.toDao(this);
  }
}
