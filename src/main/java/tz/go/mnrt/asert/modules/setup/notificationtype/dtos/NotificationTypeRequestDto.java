package tz.go.mnrt.asert.modules.setup.notificationtype.dtos;

import java.io.Serializable;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.*;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.setup.notificationtype.entity.NotificationType;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class NotificationTypeRequestDto implements Serializable {
  private Long id;
  private UUID uuid;

  @NotNull
  private String name;

  @NotNull
  private String code;

  private String description;
  private Boolean isActive;

  public NotificationTypeRequestDto(NotificationType notificationType) {
    BeanUtils.copyProperties(notificationType, this);
  }
}
