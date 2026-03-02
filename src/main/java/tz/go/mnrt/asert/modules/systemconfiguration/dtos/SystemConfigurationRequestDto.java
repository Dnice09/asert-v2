package tz.go.mnrt.asert.modules.systemconfiguration.dtos;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
public class SystemConfigurationRequestDto {
  private Long id;
  private UUID uuid;

  @NotNull(message = "configKey is required")
  private String configKey;

  @NotNull(message = "configValue is required")
  private String configValue;

  @NotNull(message = "configType is required")
  private String configType;

  private String description;

  private String category;

  private Boolean isEditable;

  public SystemConfigurationRequestDto(SystemConfiguration entity) {
    entity.toDao(this);
  }
}
