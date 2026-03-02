package tz.go.mnrt.asert.modules.systemconfiguration.dtos;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.systemconfiguration.entity.SystemConfiguration;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SystemConfigurationResponseDto {
  private Long id;
  private UUID uuid;

  @NotNull(message = "Created Date is required")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDateTime createdDate;

  private String configKey;

  private String configValue;

  private String configType;

  private String description;

  private String category;

  private Boolean isEditable;

  public SystemConfigurationResponseDto(SystemConfiguration entity) {
    entity.toDao(this);
  }
}
