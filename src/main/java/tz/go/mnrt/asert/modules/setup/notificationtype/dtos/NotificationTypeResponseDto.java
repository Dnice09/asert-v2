package tz.go.mnrt.asert.modules.setup.notificationtype.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.*;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.setup.notificationtype.entity.NotificationType;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NotificationTypeResponseDto implements Serializable {
  private Long id;
  private UUID uuid;

  private String name;

  private String code;

  private String description;
  private Boolean isActive;

  @NotNull(message = "Created Date is required")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDateTime createdDate;

  public NotificationTypeResponseDto(NotificationType notificationType) {
      BeanUtils.copyProperties(notificationType, this);
  }
}
